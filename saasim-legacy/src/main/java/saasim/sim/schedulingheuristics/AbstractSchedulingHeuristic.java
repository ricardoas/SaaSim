package saasim.sim.schedulingheuristics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.util.SimulatorProperties;


/**
 * Default schedulers behaviour.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractSchedulingHeuristic implements SchedulingHeuristic{
	
	private class Session implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1661232950273037116L;
		public long lastInteraction;
		public Machine allocatedServer;
		
		public Session(long time, Machine nextAvailableMachine) {
			lastInteraction = time;
			allocatedServer = nextAvailableMachine;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7373310542515710979L;

	private long sessionTimeOut;
	
	protected final LinkedList<Machine> machines;
	
	private long arrivalCounter;

	private long finishedCounter;
	
	private Map<Integer, Session> sessions;
	
	private final boolean enableSessionAffinity;
	
	public Statistics tierStatistics;

	/**
	 * Default constructor
	 */
	public AbstractSchedulingHeuristic() {
		machines = new LinkedList<Machine>();
		sessions = new HashMap<Integer, Session>();
		enableSessionAffinity = Configuration.getInstance().getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false);
		if(enableSessionAffinity){
			sessionTimeOut = Configuration.getInstance().getLong(SimulatorProperties.MACHINE_SESSION_TIMEOUT);
		}
		resetCounters();
	}

	protected void resetCounters() {
		arrivalCounter = 0;
		finishedCounter = 0;
		tierStatistics = new Statistics();
	}

	@Override
	public void reportFinishedRequest(Request finishedRequest) {
		tierStatistics.updateServiceTime(finishedRequest.getTotalProcessed());
		finishedCounter++;
	}

	@Override
	public Machine removeMachine() {
		return machines.size() == 1? null: machines.removeLast();
	}
	
	@Override
	public Machine removeMachine(MachineDescriptor descriptor) {
		
		assert descriptor != null: "Can't remove null descriptor.";
		
		if(machines.size() == 1) return null;
		
		for (Machine machine : machines) {
			if(machine.getDescriptor().equals(descriptor)){
				boolean result = machines.remove(machine);
				assert result: "Problem when removing machine. Check collection!";
				return machine;
			}
		}
		assert false: "Removing from wrong tier. Check DPS!";
		return null;
	}
	
	@Override
	public int getNumberOfMachines() {
		return machines.size();
	}

	@Override
	public void addMachine(Machine machine) {
		machines.addLast(machine);
	}
	
	@Override
	public Statistics getStatistics(long eventTime) {
		double averageUtilisation = 0d;
		
		for(Machine machine : machines){
			averageUtilisation += machine.computeUtilisation(eventTime);
		}
		
		if(!machines.isEmpty()){
			averageUtilisation /= machines.size();
		}
		
		long requestsArrivalCounter = arrivalCounter;
		long finishedRequestsCounter = finishedCounter;
		tierStatistics.averageUtilisation = averageUtilisation;
		tierStatistics.requestArrivals = requestsArrivalCounter;
		tierStatistics.requestCompletions = finishedRequestsCounter;
		tierStatistics.totalNumberOfActiveServers = machines.size();
		
		Statistics stat = tierStatistics;
		stat.totalNumberOfActiveServers = getNumberOfMachines();

		resetCounters();

		return stat;
	}
	
	@Override
	public Machine next(Request request){
		arrivalCounter++;
		tierStatistics.updateInterarrivalTime(request.getArrivalTimeInMillis());
		return enableSessionAffinity? recoverSession(request.getUserID(), request.getArrivalTimeInMillis()) :getNextAvailableMachine();
	}
	
	private Machine recoverSession(int userID, long time) {
		Session session = sessions.get(userID);
		if(session == null || session.lastInteraction + sessionTimeOut < time){
			session = new Session(time, getNextAvailableMachine());
			sessions.put(userID, session);
		}else{
			session.lastInteraction = time;
		}
		return session.allocatedServer;
	}

	protected abstract Machine getNextAvailableMachine();

	@Override
	public List<Machine> getMachines() {
		return machines;
	}

}
