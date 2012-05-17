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
import saasim.sim.core.Event;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.core.HandlingPoint;
import saasim.sim.schedulingheuristics.SchedulingHeuristic;
import saasim.sim.util.SaaSAppProperties;
import saasim.sim.util.SimulatorProperties;
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
	
	protected WorkloadParser<List<Request>> workloadParser;
	
	protected Monitor monitor;

	private long monitoringInterval;

	private int numberOfRequests;

	private long lastArrival;

	private int arrivalRate;

	private int threshold;

	private int peakArrivalRate;

	/**
	 * Default constructor.
	 * @param scheduler A {@link EventScheduler} to represent a scheduler of {@link SimpleMultiTierApplication}.
	 * @param monitor 
	 */
	public SimpleMultiTierApplication(EventScheduler scheduler, Monitor monitor){
		super(scheduler);
		this.tiers = buildApplication(scheduler, monitor);
		this.monitor = monitor;
		this.numberOfRequests = 0;
		this.threshold = Integer.MAX_VALUE;
		this.thresholds = new int[tiers.length];
		Arrays.fill(thresholds, Integer.MAX_VALUE);
		monitoringInterval = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
	}
	
	private LoadBalancer[] buildApplication(EventScheduler scheduler, Monitor monitor) {
		Configuration config = Configuration.getInstance();
		int numOfTiers = config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS);
		
		Class<?>[] heuristicClasses = config.getApplicationHeuristics();
		int [] maxServerPerTier = config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER);

		LoadBalancer [] loadBalancers = new LoadBalancer[numOfTiers];
		
		for (int i = 0; i < numOfTiers; i++) {
			loadBalancers[i] = buildLoadBalancer(scheduler, monitor, heuristicClasses[i], maxServerPerTier[i], i);
		}
		return loadBalancers;
	}

	/**
	 * Build a {@link LoadBalancer}.
	 * @param scheduler {@link EventScheduler} represent a event scheduler
	 * @param monitor 
	 * @param heuristic a {@link SchedulingHeuristic} for this {@link LoadBalancer} 
	 * @param maxServerPerTier the maximum number of servers per tier 
	 * @param tier the tier of {@link LoadBalancer}
	 * @return A builded {@link LoadBalancer}.
	 */
	private LoadBalancer buildLoadBalancer(EventScheduler scheduler, Monitor monitor, Class<?> heuristic,
			int maxServerPerTier, int tier) {
		try {
			return new LoadBalancer(scheduler, this, monitor, (SchedulingHeuristic) heuristic.newInstance(), 
					   maxServerPerTier, tier);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ heuristic, e);
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkloadParser(WorkloadParser<List<Request>> workloadParser) {
		assert workloadParser != null : "WorkloadParser could not be null! Check your code.";
		this.workloadParser = workloadParser;
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
	public LoadBalancer[] getTiers() {
		return this.tiers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
		for (LoadBalancer loadBalancers : tiers) {
			loadBalancers.setMonitor(monitor, this);
		}
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
