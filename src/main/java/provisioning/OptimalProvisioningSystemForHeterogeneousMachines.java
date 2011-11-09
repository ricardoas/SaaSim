package provisioning;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.io.GEISTWorkloadParser;
import commons.io.WorkloadParser;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;

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

	protected MachineType[] acceleratorTypes = {MachineType.M1_SMALL};
	
	private int tick;
	private long currentTick;
	private Request[] leftOver;
	private WorkloadParser[] parsers;
	private double[] currentRequestsCounter;
	private double[] nextRequestsCounter;
	private long SLA;

	public OptimalProvisioningSystemForHeterogeneousMachines() {
		super();
		
		String[] workloadFiles = Configuration.getInstance().getWorkloads();
		this.parsers = new WorkloadParser[workloadFiles.length];
		this.tick = 1000 * 60 * 60;
		this.currentTick = Checkpointer.loadSimulationInfo().getCurrentDayInMillis() + tick;
		this.leftOver = new Request[workloadFiles.length];
		for(int i = 0; i < workloadFiles.length; i++){
			this.parsers[i] = new GEISTWorkloadParser(workloadFiles[i]);
		}
		this.SLA = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
		this.nextRequestsCounter = new double[36000];
		
//		getParsers();
	}
	
//	private void getParsers() {
////		double error = Configuration.getInstance().getDouble(SimulatorProperties.PLANNING_ERROR);
////		
////		if(error == 0.0){
////			return;
////		}
////		
////		int totalParsers = (int)Math.round(this.parsers.length * (1+error));
////		WorkloadParser<Request>[] newParsers = new WorkloadParser[totalParsers];
////		if(totalParsers > this.parsers.length){//Adding already existed parsers
////			int difference = totalParsers - this.parsers.length;
////			for(int i = 0; i < this.parsers.length; i++){
////				newParsers[i] = this.parsers[i];
////			}
////			int index = this.parsers.length;
////			for(int i = 0; i < difference; i++){
////				newParsers[index++] = this.parsers[i].clone();
////			}
////		}else{//Removing some parsers
////			for(int i = 0; i < totalParsers; i++){
////				newParsers[i] = this.parsers[i];
////			}
////		}
//		WorkloadParser<List<Request>> workloadParser = WorkloadParserFactory.getWorkloadParser();
//		Field field;
//		try {
//			field = TimeBasedWorkloadParser.class.getDeclaredField("parsers");
//			field.setAccessible(true);
//			this.parsers = (WorkloadParser[]) field.get(workloadParser);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	@Override
	public boolean isOptimal() {
		return true;
	}
	
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		
		if(this.nextRequestsCounter != null){
			this.currentRequestsCounter = this.nextRequestsCounter;
		}
		this.nextRequestsCounter = new double[36000];
		
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
		evaluateNumberOfServersToAdd(tier, statistics.totalNumberOfServers);
	}
	
	private void countData(Request request, long currentTime) {
		int index = (int) ((request.getArrivalTimeInMillis() - currentTime) / 100);
		this.currentRequestsCounter[index]++;//Adding demand in arrival interval
		
		long totalMeanToProcess = this.SLA;
		
		long intervalsToProcess = totalMeanToProcess / 100;
		if(totalMeanToProcess == 100){
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

	private void evaluateNumberOfServersToAdd(int tier, long totalNumberOfServers) {
		
		Percentile percentile = new Percentile(95);
		double maximumDemand = percentile.evaluate(currentRequestsCounter);
		
		//FIXME: Discutir se eh isso mesmo!
		int numberOfServers = (int)Math.ceil(maximumDemand);
		long numberOfServersToAdd = numberOfServers - totalNumberOfServers;
		
		if(numberOfServersToAdd > 0){
			evaluateMachinesToBeAdded(tier, numberOfServersToAdd);
		}else if(numberOfServersToAdd < 0){
			for (int i = 0; i < -numberOfServersToAdd; i++) {
				configurable.removeServer(tier, false);
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
					configurable.addServer(tier, provider.buyMachine(true, machineType), true);
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
			for(MachineType machineType : this.acceleratorTypes){
				for (Provider provider : providers) {
					while(provider.canBuyMachine(false, machineType) && 
							serversAdded + machineType.getNumberOfCores() <= numberOfServersToAdd){
						configurable.addServer(tier, provider.buyMachine(false, machineType), true);
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
