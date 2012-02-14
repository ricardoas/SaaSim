package saasim.sim;

import java.util.List;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.io.WorkloadParser;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.jeevent.JEAbstractEventHandler;
import saasim.sim.jeevent.JECheckpointer;
import saasim.sim.jeevent.JEEvent;
import saasim.sim.jeevent.JEEventScheduler;
import saasim.sim.jeevent.JEEventType;
import saasim.sim.jeevent.JEHandlingPoint;
import saasim.sim.util.SimulatorProperties;
import saasim.util.SimulationInfo;
import saasim.util.TimeUnit;


/**
 * This class represents a simulator of SaaSim, it features and basic operations.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleSimulator extends JEAbstractEventHandler implements Simulator, ServiceEntry{
	
	private static final long MILLIS = TimeUnit.SECOND.getMillis();

	/**
	 * 
	 */
	private static final long serialVersionUID = -8028580648054904982L;
	
	private LoadBalancer tiers [];
	
	protected transient WorkloadParser<List<Request>> workloadParser;
	protected transient Monitor monitor;

	private long monitoringInterval;

	private int numberOfRequests;

	private long lastArrival;

	private int arrivalRate;

	private int threshold;

	/**
	 * Default constructor.
	 * @param scheduler A {@link JEEventScheduler} to represent a scheduler of {@link SimpleSimulator}.
	 * @param tiers An array containing the tiers of application, see {@link LoadBalancer}.
	 */
	public SimpleSimulator(JEEventScheduler scheduler, LoadBalancer... tiers){
		super(scheduler);
		this.tiers = tiers;
		this.numberOfRequests = 0;
		this.threshold = Integer.MAX_VALUE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		prepareBeforeStart();
		getScheduler().start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkloadParser(WorkloadParser<List<Request>> workloadParser) {
		assert workloadParser != null : "WorkloadParser could not be null! Check your code.";
		this.workloadParser = workloadParser;
	}
	
	/**
	 * Prepare the simulator before it start up, starting events like 
	 * {@link JEEventType#READWORKLOAD#CHARGE_USERS#ESTIMATE_SERVERS#COLLECT_STATISTICS}. 
	 */
	protected void prepareBeforeStart() {
		send(new JEEvent(JEEventType.READWORKLOAD, this, getScheduler().now()));
		
		SimulationInfo info = Configuration.getInstance().getSimulationInfo();
		if(info.isChargeDay()){
			send(new JEEvent(JEEventType.CHARGE_USERS, this, info.getCurrentDayInMillis() + JECheckpointer.INTERVAL - 1));
		}
		
		monitoringInterval = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
		
		if(info.isFirstDay()){
			if(this.monitor.isOptimal()){ //TODO:"Change this!
				send(new JEEvent(JEEventType.ESTIMATE_SERVERS, this, getScheduler().now()));
			}else{
				send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + monitoringInterval));
			}
		}
	}
	
	@JEHandlingPoint(JEEventType.READWORKLOAD)
	public void readWorkload(){
		if(workloadParser.hasNext()) {
			List<Request> list = workloadParser.next();
			numberOfRequests += list.size(); 
			for (Request request : list) {
				if(request.getCpuDemandInMillis()[0] != 0){
					send(parseEvent(request));
				}
			}
			if(workloadParser.hasNext()){
				long newEventTime = getScheduler().now() + Configuration.getInstance().getParserPageSize().getMillis();
				send(new JEEvent(JEEventType.READWORKLOAD, this, newEventTime));
			}else{
				workloadParser.close();
			}
		}
	}
	
	@JEHandlingPoint(JEEventType.CHARGE_USERS)
	public void chargeUsers(){
		this.monitor.chargeUsers(getScheduler().now());
	}
	
	@JEHandlingPoint(JEEventType.COLLECT_STATISTICS)
	public void collectStatistics(){
		long time = getScheduler().now();
		for (LoadBalancer loadBalancer : tiers) {
			loadBalancer.collectStatistics(time, monitoringInterval, numberOfRequests);
		}
		numberOfRequests = 0;
		send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)));
	}
	
	@JEHandlingPoint(JEEventType.ESTIMATE_SERVERS)
	public void estimateServers(){
		long currentTime = getScheduler().now();
		for (LoadBalancer loadBalancer : tiers) {
			loadBalancer.estimateServers(currentTime);
		}
		send(new JEEvent(JEEventType.ESTIMATE_SERVERS, this, getScheduler().now() + 1000 * 60 * 60));//One hour later
	}
	
	@JEHandlingPoint(JEEventType.NEWREQUEST)
	public void newRequest(Request request){
		if(!isOverloaded(getScheduler().now())){
			send(new JEEvent(JEEventType.NEWREQUEST, tiers[0], getScheduler().now(), request));
		}else{
			monitor.requestQueued(getScheduler().now(), request, -1);
		}
	}
	
	@Override
	public void config(int threshold){
		if(threshold > 0){
//			this.threshold = threshold;
		}
	}

	public boolean isOverloaded(long arrivalTime) {
		if(arrivalTime - lastArrival > MILLIS){
			lastArrival = (arrivalTime/1000) * 1000;
			arrivalRate = 0;
		}
		return ++arrivalRate > threshold;
	}

	/**
	 * Parse a {@link Request} in a {@link JEEvent}.
	 * @param request {@link Request} to be parsed in a event.
	 * @return
	 */
	protected JEEvent parseEvent(Request request) {
		return new JEEvent(JEEventType.NEWREQUEST, this, request.getArrivalTimeInMillis(), request);
	}
	
	/**
	 * Gets the {@link WorkloadParser} of this {@link SimpleSimulator}.
	 * @return the {@link WorkloadParser}
	 */
	public WorkloadParser<List<Request>> getParser() {
		return this.workloadParser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMachine(int tier, MachineDescriptor machineDescriptor, boolean useStartUpDelay) {
		assert tiers.length > tier : "This tier not exists!";
		tiers[tier].addMachine(machineDescriptor, useStartUpDelay);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMachine(int tier, boolean force) {
		assert tiers.length >= tier : "This tier not exists!";
		tiers[tier].removeMachine(force);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoadBalancer[] getTiers() {
		return this.tiers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
		for (LoadBalancer loadBalancers : tiers) {
			loadBalancers.setMonitor(monitor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancelMachineRemoval(int tier, int numberOfMachines) {
		tiers[tier].cancelMachineRemoval(numberOfMachines);
	}

	@Override
	public void removeMachine(int tier, MachineDescriptor descriptor,
			boolean force) {
		assert tiers.length >= tier : "This tier not exists!";
		tiers[tier].removeMachine(descriptor, force);
	}
}