package saasim.provisioning;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.io.GEISTWorkloadParser;
import saasim.io.WorkloadParser;
import saasim.sim.schedulingheuristics.Statistics;
import saasim.sim.util.SaaSAppProperties;
import saasim.sim.util.SimulatorProperties;


/**
 * This class represents the DPS business logic modified from original RANJAN. Here some statistics of current
 * available machines (i.e, utilisation) is used to verify if new machines need to be added to 
 * an application tier, or if some machines can be removed from any application tier. After the number of servers needed
 * is calculated, the DPS verifies if any powerful reserved machine is available to be added and, if not, accelerator nodes
 * are purchased from the cloud provider.
 * 
 * @author David candeia
 *
 */
public class OptimalProvisioningSystemForHeterogeneousMachines extends DynamicProvisioningSystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4511114280512130824L;

	private static final int QUANTUM_SIZE = 100;

	protected MachineType[] acceleratorTypes = {MachineType.M1_SMALL};
	
	private int tick;
	private long currentTick;
	private Request[] leftOver;
	private WorkloadParser[] parsers;
	private double[] currentRequestsCounter;
	private double[] nextRequestsCounter;
	private long SLA;
	
	private long totalMeanToProcess;
	private int numberOfRequests;

	public OptimalProvisioningSystemForHeterogeneousMachines(User[] users, Provider[] providers) {
		super(users, providers);
		
		String[] workloadFiles = Configuration.getInstance().getWorkloads();
		this.parsers = new WorkloadParser[workloadFiles.length];
		this.tick = 1000 * 60 * 60;
		this.currentTick = Configuration.getInstance().getSimulationInfo().getCurrentDayInMillis() + tick;
		this.leftOver = new Request[workloadFiles.length];
		for(int i = 0; i < workloadFiles.length; i++){
			this.parsers[i] = new GEISTWorkloadParser(workloadFiles[i], 0);
		}
		this.SLA = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
		this.nextRequestsCounter = new double[36000];
		
	}
	
	@Override
	public boolean isOptimal() {
		return true;
	}
	
	@Override
	public void sendStatistics(long now, Statistics statistics, int tier) {
		
		if(this.nextRequestsCounter != null){
			this.currentRequestsCounter = this.nextRequestsCounter;
		}
		this.nextRequestsCounter = new double[36000];
		this.totalMeanToProcess = 0;
		this.numberOfRequests = 0;
		
		for (int i = 0; i < leftOver.length; i++) {
			Request left = leftOver[i];
			if(left != null){
				if(left.getArrivalTimeInMillis() < currentTick){
					countData(left, currentTick - tick);
					leftOver[i] = null;
				}
			}
		}
		
		for (int i = 0; i < parsers.length; i++) {
			if(leftOver[i] == null){
				WorkloadParser<Request> parser = parsers[i];
				while(parser.hasNext()){
					Request next = parser.next();
					if(next.getArrivalTimeInMillis() < currentTick){
						countData(next, currentTick - tick);
					}else{
						leftOver[i] = next;
						break;
					}
				}
			}
		}
		
		this.currentTick += tick;
		
		//Calculating number of machines!
		evaluateNumberOfServersToAdd(tier, statistics.totalNumberOfActiveServers);
	}
	
	private void countData(Request request, long currentTime) {
		this.numberOfRequests++;
		
		int index = (int) ((request.getArrivalTimeInMillis() - currentTime) / QUANTUM_SIZE);
		this.currentRequestsCounter[index]++;//Adding demand in arrival interval
		
		long intervalsToProcess = request.getTotalMeanToProcess() / QUANTUM_SIZE;
		
		for(int i = index+1; i < index + intervalsToProcess; i++){//Adding demand to subsequent intervals
			if(i >= this.currentRequestsCounter.length){
				this.nextRequestsCounter[i - this.currentRequestsCounter.length]++;
			}else{
				this.currentRequestsCounter[i]++;
			}
		}
	}

	private void evaluateNumberOfServersToAdd(int tier, long totalNumberOfServers) {
		
		Percentile percentile = new Percentile(95);
		double maximumDemand = percentile.evaluate(currentRequestsCounter);
		
		//FIXME: Discutir se eh isso mesmo!
		int numberOfServers = (int)Math.ceil(maximumDemand);
//		if(this.numberOfRequests != 0){
//			double requestsMeanDemand = (this.totalMeanToProcess / this.numberOfRequests);
//			numberOfServers = (int)Math.ceil(maximumDemand / (SLA /  requestsMeanDemand));
//		}else{
//			numberOfServers = 0;
//		}
		
		long numberOfServersToAdd = numberOfServers - totalNumberOfServers;
		if(numberOfServersToAdd > 0){
			evaluateMachinesToBeAdded(tier, numberOfServersToAdd);
		}else if(numberOfServersToAdd < 0){
			for (int i = 0; i < -numberOfServersToAdd; i++) {
				configurable.removeMachine(tier, null, false);//FIXME machine descriptor
			}
		}
		
	}
	
	private void evaluateMachinesToBeAdded(int tier, long numberOfServersToAdd) {
		int serversAdded = 0;
		
		List<MachineType> typeList = Arrays.asList(MachineType.values());
		Collections.reverse(typeList);
		for(MachineType machineType: typeList){//TODO test which order is the best
			for (Provider provider : providers) {
				while(provider.canBuyMachine(true, machineType) && 
						serversAdded + machineType.getNumberOfCores() <= numberOfServersToAdd){
					configurable.addMachine(tier, provider.buyMachine(true, machineType), true);
					serversAdded += machineType.getNumberOfCores();
				}
				if(serversAdded == numberOfServersToAdd){
					break;
				}
			}
			if(serversAdded == numberOfServersToAdd){
				break;
			}
		}
		
		//If servers are still needed ...
		if(serversAdded < numberOfServersToAdd){
			
			//Applying on-demand market risk ...
			numberOfServersToAdd = (numberOfServersToAdd - serversAdded);
			serversAdded = 0;
			double onDemandRisk = Configuration.getInstance().getDouble(SimulatorProperties.PLANNING_RISK);
			numberOfServersToAdd = (int) Math.ceil(numberOfServersToAdd * (1-onDemandRisk));
			
			for(MachineType machineType : this.acceleratorTypes){
				for (Provider provider : providers) {
					while(provider.canBuyMachine(false, machineType) && 
							serversAdded + machineType.getNumberOfCores() <= numberOfServersToAdd){
						configurable.addMachine(tier, provider.buyMachine(false, machineType), true);
						serversAdded += machineType.getNumberOfCores();
					}
					if(serversAdded == numberOfServersToAdd){
						break;
					}
				}
				if(serversAdded == numberOfServersToAdd){
					break;
				}
			}
		}
	}
}
