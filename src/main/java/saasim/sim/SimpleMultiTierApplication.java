package saasim.sim;

import java.util.Arrays;
import java.util.List;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.io.WorkloadParser;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.core.AbstractEventHandler;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.Event;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.core.HandlingPoint;
import saasim.sim.util.SimulatorProperties;
import saasim.util.SimulationInfo;
import saasim.util.TimeUnit;


/**
 * This class represents a simulator of SaaSim, it features and basic operations.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleMultiTierApplication extends AbstractEventHandler implements DynamicConfigurable{
	
	private static final long MILLIS = TimeUnit.SECOND.getMillis();

	/**
	 * 
	 */
	private static final long serialVersionUID = -8028580648054904982L;
	
	private LoadBalancer tiers [];
	private int thresholds [];
	
	protected transient WorkloadParser<List<Request>> workloadParser;
	protected transient Monitor monitor;

	private long monitoringInterval;

	private int numberOfRequests;

	private long lastArrival;

	private int arrivalRate;

	private int threshold;

	private int peakArrivalRate;

	/**
	 * Default constructor.
	 * @param scheduler A {@link EventScheduler} to represent a scheduler of {@link SimpleMultiTierApplication}.
	 * @param tiers An array containing the tiers of application, see {@link LoadBalancer}.
	 */
	public SimpleMultiTierApplication(EventScheduler scheduler, LoadBalancer... tiers){
		super(scheduler);
		this.tiers = tiers;
		this.numberOfRequests = 0;
		this.threshold = Integer.MAX_VALUE;
		this.thresholds = new int[tiers.length];
		Arrays.fill(thresholds, Integer.MAX_VALUE);
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
		send(new Event(EventType.READWORKLOAD, this, getScheduler().now()));
		
		SimulationInfo info = Configuration.getInstance().getSimulationInfo();
		if(info.isChargeDay()){
			send(new Event(EventType.CHARGE_USERS, this, info.getCurrentDayInMillis() + EventCheckpointer.INTERVAL - 1));
		}
		
		monitoringInterval = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
		
		if(info.isFirstDay()){
			if(this.monitor.isOptimal()){ //TODO:"Change this!
				send(new Event(EventType.ESTIMATE_SERVERS, this, getScheduler().now()));
			}else{
				send(new Event(EventType.COLLECT_STATISTICS, this, getScheduler().now() + monitoringInterval));
			}
		}
	}
	
	@HandlingPoint(EventType.READWORKLOAD)
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
				send(new Event(EventType.READWORKLOAD, this, newEventTime));
			}else{
				workloadParser.close();
			}
		}
	}
	
	@HandlingPoint(EventType.CHARGE_USERS)
	public void chargeUsers(){
		this.monitor.chargeUsers(getScheduler().now());
	}
	
	@HandlingPoint(EventType.COLLECT_STATISTICS)
	public void collectStatistics(){
		long time = getScheduler().now();
		for (LoadBalancer loadBalancer : tiers) {
			loadBalancer.collectStatistics(time, monitoringInterval, numberOfRequests, peakArrivalRate);
		}
		peakArrivalRate = 0;
		numberOfRequests = 0;
		send(new Event(EventType.COLLECT_STATISTICS, this, getScheduler().now() + Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)));
	}
	
	@HandlingPoint(EventType.ESTIMATE_SERVERS)
	public void estimateServers(){
		long currentTime = getScheduler().now();
		for (LoadBalancer loadBalancer : tiers) {
			loadBalancer.estimateServers(currentTime);
		}
		send(new Event(EventType.ESTIMATE_SERVERS, this, getScheduler().now() + 1000 * 60 * 60));//One hour later
	}
	
	@HandlingPoint(EventType.NEWREQUEST)
	public void newRequest(Request request){
		if(!isOverloaded(getScheduler().now())){
			send(new Event(EventType.NEWREQUEST, tiers[0], getScheduler().now(), request));
		}else{
			monitor.requestQueued(getScheduler().now(), request, -1);
			tiers[0].registerDrop(request);
		}
	}
	
	@Override
	public void config(int tier, double threshold){
		tiers[tier].config(threshold);
	}

	public boolean isOverloaded(long arrivalTime) {
		
		if(arrivalTime - lastArrival > MILLIS){
			lastArrival = (arrivalTime/1000) * 1000;
			peakArrivalRate = Math.max(peakArrivalRate, arrivalRate);
			arrivalRate = 0;
		}
		return ++arrivalRate > threshold;
	}

	/**
	 * Parse a {@link Request} in a {@link Event}.
	 * @param request {@link Request} to be parsed in a event.
	 * @return
	 */
	protected Event parseEvent(Request request) {
		return new Event(EventType.NEWREQUEST, this, request.getArrivalTimeInMillis(), request);
	}
	
	/**
	 * Gets the {@link WorkloadParser} of this {@link SimpleMultiTierApplication}.
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
			loadBalancers.setMonitor(monitor, this);
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

	@Override
	public void config(int requestAcceptanceRate) {
		if(requestAcceptanceRate > 0){
			this.threshold = requestAcceptanceRate;
		}
	}
}
