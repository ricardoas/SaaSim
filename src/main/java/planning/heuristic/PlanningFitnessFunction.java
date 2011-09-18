package planning.heuristic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

import planning.util.Summary;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResultEntry;
import commons.config.Configuration;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;

public class PlanningFitnessFunction extends FitnessFunction{

	private final Provider[] cloudProviders;
	private final User[] cloudUsers;
	private final Map<User, List<Summary>> summaries;
	private final List<MachineType> types;

	public PlanningFitnessFunction(Map<User, List<Summary>> summaries, User[] cloudUsers, Provider[] cloudProviders, List<MachineType> types) {
		this.summaries = summaries;
		this.cloudProviders = cloudProviders;
		this.cloudUsers = cloudUsers;
		this.types = types;
	}

	@Override
	protected double evaluate(IChromosome arg0) {
		
		double arrivalRate = aggregateArrivals();
		double meanServiceTime = aggregateServiceTime();
		
		//Since round-robin is used, the total arrival rate is splitted among reserved servers
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		int index = 0;
		double totalPower = 0;
		
		for(Gene gene : arg0.getGenes()){
			Integer numberOfMachinesReserved = (Integer)gene.getAllele();
			MachineType type = this.types.get(index);
			
			double relativePower = Configuration.getInstance().getRelativePower(type);
			currentPowerPerMachineType.put(this.types.get(index), (int)Math.round(numberOfMachinesReserved * relativePower));
			totalPower += Math.round(numberOfMachinesReserved * relativePower);
			index++;
		}
		
		//Calculating arrival rates per machine type
		Map<MachineType, Double> arrivalRatesPerMachineType = new HashMap<MachineType, Double>();
		for(MachineType type : currentPowerPerMachineType.keySet()){
			arrivalRatesPerMachineType.put(type, (currentPowerPerMachineType.get(type) / totalPower) * arrivalRate);
		}
		
		//Assuming a base computing power (e.g., EC2 unit for each core)
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		double totalThroughput = 0d;
		for(MachineType type : arrivalRatesPerMachineType.keySet()){
			Double currentArrivalRate = arrivalRatesPerMachineType.get(type);
			double maximumThroughput = (1 / meanServiceTime) * Configuration.getInstance().getRelativePower(type);
			if(currentArrivalRate > maximumThroughput){//Requests are missed
				throughputPerMachineType.put(type, maximumThroughput);
				totalThroughput += maximumThroughput;
			}else{
				throughputPerMachineType.put(type, currentArrivalRate);
				totalThroughput += currentArrivalRate;
			}
		}
		
		double totalNumberOfUsers = aggregateNumberOfUsers();
		double averageThinkTime = aggregateThinkTime();
		
		//Estimated response time
		double responseTimeInSeconds = totalNumberOfUsers / totalThroughput - averageThinkTime;
		
		//Estimating utility
		double receipt = calcReceipt();
		double cost = calcCost(throughputPerMachineType, meanServiceTime, currentPowerPerMachineType);
		double penalties = calcPenalties(responseTimeInSeconds, arrivalRate, totalThroughput);
		
		double fitness = receipt - cost - penalties;
		
		if(fitness < 1){
			return (1/Math.abs(fitness))+1;
		}
		
		return fitness;
	}

	protected double calcPenalties(double responseTimeInSeconds, double arrivalRate, double totalThroughput) {
		long maxRt = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
		double responseTimeLoss = (responseTimeInSeconds * 1000 - maxRt)/maxRt;
		
		//TODO: Check if this makes sense ...
		double rejectedLoss = (arrivalRate - totalThroughput) / arrivalRate;
		double totalLoss = (responseTimeLoss < 0 ? rejectedLoss : responseTimeLoss + rejectedLoss);
		
		double penalty = 0d;
		for(User user : this.cloudUsers){
			penalty += user.calculatePenalty(totalLoss);
		}
		return penalty;
	}

	protected double calcCost(Map<MachineType, Double> throughputPerMachineType, double meanServiceTimeInMillis, Map<MachineType, Integer> currentPowerPerMachineType) {
		long totalTimeInSeconds = Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD) * 24 * 60 * 60;
		Provider provider = cloudProviders[0];
		double cost = 0;
		
		for(MachineType type : throughputPerMachineType.keySet()){
			Double throughput = throughputPerMachineType.get(type);
			double CPUHoursPerType = (throughput * totalTimeInSeconds * meanServiceTimeInMillis) / 3600000;
			
			cost += provider.getReservationOneYearFee(type) * (currentPowerPerMachineType.get(type)/Configuration.getInstance().getRelativePower(type)) 
							+ provider.getReservedCpuCost(type) * CPUHoursPerType;
		}
		
		return cost;
	}

	protected double calcReceipt() {
		UtilityResultEntry resultEntry = new UtilityResultEntry(0, this.cloudUsers, this.cloudProviders);

		double oneTimeFees = 0d;
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			Contract contract = entry.getKey().getContract();
			long totalCPUHrs = 0l;
			for(Summary summary : entry.getValue()){
				totalCPUHrs += summary.getTotalCpuHrs();
			}
			
			contract.calculateReceipt(resultEntry, entry.getKey().getId(), totalCPUHrs * 60 * 60 * 1000, 0l, 0l, 0l);
			oneTimeFees += contract.calculateOneTimeFees();
		}
		
		return resultEntry.getReceipt() + oneTimeFees;
	}

	protected double aggregateThinkTime() {
		double thinkTime = 0d;
		int totalNumberOfValues = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			double currentPeriodMean = 0;
			for(Summary summary : entry.getValue()){
				currentPeriodMean += summary.getUserThinkTime();
			}
			currentPeriodMean /= entry.getValue().size();
			thinkTime += currentPeriodMean;
			totalNumberOfValues++;
		}
		
		return thinkTime / totalNumberOfValues;
	}

	protected double aggregateNumberOfUsers() {
		int totalNumberOfUsers = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			int currentNumberOfUsers = 0;
			for(Summary summary : entry.getValue()){
				currentNumberOfUsers += summary.getNumberOfUsers();
			}
			currentNumberOfUsers /= entry.getValue().size();
			totalNumberOfUsers += currentNumberOfUsers;
		}
		
		return totalNumberOfUsers;
	}

	protected double aggregateServiceTime() {
		double serviceTime = 0d;
		int totalNumberOfValues = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			double currentPeriodMean = 0;
			for(Summary summary : entry.getValue()){
				currentPeriodMean += summary.getRequestServiceDemand();
			}
			currentPeriodMean /= entry.getValue().size();
			serviceTime += currentPeriodMean;
			totalNumberOfValues++;
		}
		
		return serviceTime / totalNumberOfValues;
	}

	protected double aggregateArrivals() {
		double totalArrivalRate = 0;
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			double meanArrivalRate = 0;
			for(Summary summary : entry.getValue()){
				meanArrivalRate += summary.getArrivalRate();
			}
			meanArrivalRate /= entry.getValue().size();
			totalArrivalRate += meanArrivalRate;
		}
		
		return totalArrivalRate;
	}

}
