package planning.heuristic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import planning.util.PlanIOHandler;
import provisioning.DPS;
import provisioning.Monitor;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.io.WorkloadParser;
import commons.sim.SimpleSimulator;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.util.SimulatorProperties;

/**
 * This {@link PlanningHeuristic} makes capacity planning based on over provisioning, in other words, occurs the reservation
 * of more machines than can be necessary.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class OverProvisionHeuristic extends SimpleSimulator implements PlanningHeuristic {

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
	 * @param scheduler {@link JEEventScheduler} event scheduler
	 * @param monitor {@link Monitor} for reporting information
	 * @param loadBalancers a set of {@link LoadBalancer}s of the application
	 */
	public OverProvisionHeuristic(JEEventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
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
		
		this.start();
		
		//Calculating requests mean demand
		if(this.requestsMeanDemand == 0d){//first day
			this.requestsMeanDemand = this.totalProcessingTime / this.numberOfRequests;
		}else{//other days
			this.requestsMeanDemand = (this.requestsMeanDemand + (this.totalProcessingTime / this.numberOfRequests)) / 2;
		}

		if(Checkpointer.loadSimulationInfo().isFinishDay()){//Simulation finished!
			Checkpointer.clear();
			PlanIOHandler.clear();
			Map<MachineType, Integer> plan = this.getPlan(null);
			try {
				PlanIOHandler.createPlanFile(plan, Checkpointer.loadProviders());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}else{
			try {
				Checkpointer.save();
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
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case READWORKLOAD:
			if(workloadParser.hasNext()) {
				List<Request> requests = workloadParser.next();
				numberOfRequests += requests.size();

				totalProcessingTime += calcNumberOfMachines(requests, event.getScheduledTime());
				evaluateMaximumNumber();
				if(workloadParser.hasNext()){
					long newEventTime = getScheduler().now() + Configuration.getInstance().getParserPageSize().getMillis();
					send(new JEEvent(JEEventType.READWORKLOAD, this, newEventTime, true));
				}else{
					workloadParser.close();
				}
			}
			break;
		default:
			break;
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