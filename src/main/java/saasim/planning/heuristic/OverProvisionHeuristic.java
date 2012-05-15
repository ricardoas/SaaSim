package saasim.planning.heuristic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.io.WorkloadParser;
import saasim.planning.util.PlanIOHandler;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.Event;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.util.SimulatorProperties;


/**
 * This {@link PlanningHeuristic} makes capacity planning based on over provisioning, in other words, occurs the reservation
 * of more machines than can be necessary.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class OverProvisionHeuristic extends SimpleMultiTierApplication implements PlanningHeuristic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5387121938928308579L;
	public static final double FACTOR = 0.2;//Utilisation factor according to Above the Clouds: ...
	private final long COUNTING_PAGE_SIZE = 100;

	private int maximumNumberOfServers;
	private double requestsMeanDemand;
	
	private int[] currentRequestsCounter;
	private int[] nextRequestsCounter;
	private double totalProcessingTime;
	private long numberOfRequests;
	
	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler} event scheduler
	 * @param monitor {@link Monitor} for reporting information
	 * @param loadBalancers a set of {@link LoadBalancer}s of the application
	 */
	public OverProvisionHeuristic(EventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
		super(scheduler, loadBalancers);
		try{
			this.maximumNumberOfServers = PlanIOHandler.getNumberOfMachinesFromFile();
		}catch(Exception e){
			this.maximumNumberOfServers = 0;
		}
		try{
			this.currentRequestsCounter = PlanIOHandler.getNumberOfMachinesArray();
		}catch(Exception e){
			this.currentRequestsCounter = new int[600];
		}
		try{
			this.requestsMeanDemand = PlanIOHandler.getRequestsMeanDemandFromFile();
		}catch(Exception e){
			this.requestsMeanDemand = 0d;
		}
		
		this.totalProcessingTime = 0d;
		this.numberOfRequests = 0l;
		this.setMonitor(monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findPlan(Provider[] cloudProviders, User[] cloudUsers) {
		//Simulating ...
		DPS dps = (DPS) this.monitor;
		dps.registerConfigurable(this);
		WorkloadParser<List<Request>> parser = this.getParser();
		
		try{
			double error = Configuration.getInstance().getDouble(SimulatorProperties.PLANNING_ERROR);
//			parser.applyError(error);
		}catch(NoSuchElementException e){
		}
		
//		this.start();
		
		//Calculating requests mean demand
		if(this.requestsMeanDemand == 0d){//first day
			this.requestsMeanDemand = this.totalProcessingTime / this.numberOfRequests;
		}else{//other days
			this.requestsMeanDemand = (this.requestsMeanDemand + (this.totalProcessingTime / this.numberOfRequests)) / 2;
		}

		if(Configuration.getInstance().getSimulationInfo().isFinishDay()){//Simulation finished!
			EventCheckpointer.clear();
			PlanIOHandler.clear();
			Map<MachineType, Integer> plan = this.getPlan(null);
			try {
				PlanIOHandler.createPlanFile(plan, Configuration.getInstance().getProviders());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}else{
			try {
				EventCheckpointer.save();
				PlanIOHandler.createNumberOfMachinesFile(this.maximumNumberOfServers, this.nextRequestsCounter, this.requestsMeanDemand);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readWorkload() {
		if(workloadParser.hasNext()) {
			List<Request> requests = workloadParser.next();
			numberOfRequests += requests.size();
			
			totalProcessingTime += calcNumberOfMachines(requests, now());
			evaluateMaximumNumber();
			if(workloadParser.hasNext()){
				long newEventTime = now() + Configuration.getInstance().getParserPageSize().getMillis();
				send(new Event(EventType.READWORKLOAD, this, newEventTime, true));
			}else{
				workloadParser.close();
			}
		}
		
	}
	
	/**
	 * Estimates the maximum number of servers using this {@link OverProvisionHeuristic}.
	 */
	private void evaluateMaximumNumber() {
		for(int value : this.currentRequestsCounter){
			if(value > this.maximumNumberOfServers){
				this.maximumNumberOfServers = value;
			}
		}
		
		for(int value : this.nextRequestsCounter){
			if(value > this.maximumNumberOfServers){
				this.maximumNumberOfServers = value;
			}
		}
	}

	/**
	 * Calculate the number of machines should be reserved using this {@link OverProvisionHeuristic}.
	 * @param requests a list of {@link Request}s used to adding demand in the processing of them. 
	 * @param currentTime the actual time
	 * @return The total processing time of the {@link Request}s.
	 */
	private double calcNumberOfMachines(List<Request> requests, long currentTime) {
		double totalProcessingTime = 0d;
		
		if(this.nextRequestsCounter != null){
			this.currentRequestsCounter = this.nextRequestsCounter;
		}
		this.nextRequestsCounter = new int[600];
		
		for(Request request : requests){
			int index = (int) ((request.getArrivalTimeInMillis() - currentTime) / this.COUNTING_PAGE_SIZE);
			this.currentRequestsCounter[index]++;//Adding demand in arrival interval
			
			long totalMeanToProcess = request.getTotalMeanToProcess();
			totalProcessingTime += totalMeanToProcess;
			
			long intervalsToProcess = totalMeanToProcess / this.COUNTING_PAGE_SIZE;
			if(totalMeanToProcess == this.COUNTING_PAGE_SIZE){
				intervalsToProcess = 0;
			}
			
			for(int i = index+1; i < index + intervalsToProcess; i++){//Adding demand to subsequent intervals
				if(i >= this.currentRequestsCounter.length){
					this.nextRequestsCounter[i - this.currentRequestsCounter.length]++;
				}else{
					this.currentRequestsCounter[i]++;
				}
			}
		}
		return totalProcessingTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getEstimatedProfit(int period) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		Configuration config = Configuration.getInstance();
		//int machinesToReserve = (int)Math.ceil( ( maximumNumberOfServers / 
		//(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME) / this.requestsMeanDemand ) ) * FACTOR );
		int machinesToReserve = (int)Math.ceil( ( maximumNumberOfServers ) * FACTOR );
		
		Map<MachineType, Integer> plan = new HashMap<MachineType, Integer>();
		MachineType machineType = MachineType.valueOf(config.getString(SimulatorProperties.PLANNING_TYPE).toUpperCase().replace('.', '_'));
		plan.put(machineType, machinesToReserve);
		return plan;
	}

}