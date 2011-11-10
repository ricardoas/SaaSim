package commons.sim.schedulingheuristics;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
	
	private class Session{
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

	private void resetCounters() {
		arrivalCounter = 0;
		finishedCounter = 0;
	}

	@Override
	public void reportRequestFinished() {
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
		resetCounters();
		
		return new MachineStatistics(averageUtilisation, requestsArrivalCounter, finishedRequestsCounter, machines.size());
	}
	
	@Override
	public Machine next(Request request){
		arrivalCounter++;
		return enableSessionAffinity? recoverSession(request.getUserID(), request.getArrivalTimeInMillis()) :getNextAvailableMachine();
	}
	
	private Machine recoverSession(int userID, long time) {
		Session session = sessions.get(userID);
		if(session == null || session.lastInteraction + sessionTimeOut > time){
			session = new Session(time, getNextAvailableMachine());
			sessions.put(userID, session);
		}else{
			session.lastInteraction = time;
		}
		return session.allocatedServer;
	}

	protected abstract Machine getNextAvailableMachine();

	@Override
	public Collection<? extends Machine> getMachines() {
		return machines;
	}

}
