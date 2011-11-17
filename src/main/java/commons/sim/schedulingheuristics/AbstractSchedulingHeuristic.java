package commons.sim.schedulingheuristics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.components.Machine;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SimulatorProperties;

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
	
	protected MachineStatistics tierStatistics;

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
		tierStatistics = new MachineStatistics();
	}

	@Override
	public void reportFinishedRequest(Request finishedRequest) {
		tierStatistics.numberOfRequestsCompletionInLastIntervalInTier++;
		tierStatistics.updateServiceTime(finishedRequest.getTotalProcessed());
		finishedCounter++;
	}

	@Override
	public Machine removeMachine() {
		return machines.size() == 1? null: machines.removeLast();
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
	public MachineStatistics getStatistics(long eventTime) {
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
		tierStatistics.numberOfRequestsArrivalInLastInterval = requestsArrivalCounter;
		tierStatistics.numberOfRequestsCompletionsInLastInterval = finishedRequestsCounter;
		tierStatistics.totalNumberOfServers = machines.size();
		
		MachineStatistics stat = tierStatistics;

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
