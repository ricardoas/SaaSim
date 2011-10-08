package planning.heuristic;

import java.util.ArrayList;
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
import commons.sim.SimpleSimulator;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;

public class PlanningFitnessFunction extends FitnessFunction{

	private long SUMMARY_LENGTH_IN_SECONDS;
	
	private final Provider[] cloudProviders;
	private final User[] cloudUsers;
	private final Map<User, List<Summary>> summaries;
	private final List<MachineType> types;

	public PlanningFitnessFunction(Map<User, List<Summary>> summaries, User[] cloudUsers, Provider[] cloudProviders, List<MachineType> types) {
		this.summaries = summaries;
		this.cloudProviders = cloudProviders;
		this.cloudUsers = cloudUsers;
		this.types = types;
		
		try{
			this.SUMMARY_LENGTH_IN_SECONDS = Configuration.getInstance().getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE);
		}catch(Exception e){
			this.SUMMARY_LENGTH_IN_SECONDS = 60 * 60;
		}
	}

	@Override
	protected double evaluate(IChromosome arg0) {
		
//		double arrivalRate = aggregateArrivals();
//		double meanServiceTimeInMillis = aggregateServiceTime();
//		
//		//Since round-robin is used, the total arrival rate is splitted among reserved servers
//		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
//		int index = 0;
//		double totalPower = 0;
//		
//		for(Gene gene : arg0.getGenes()){
//			Integer numberOfMachinesReserved = (Integer)gene.getAllele();
//			MachineType type = this.types.get(index);
//			
//			double relativePower = Configuration.getInstance().getRelativePower(type);
//			currentPowerPerMachineType.put(this.types.get(index), (int)Math.round(numberOfMachinesReserved * relativePower));
//			totalPower += Math.round(numberOfMachinesReserved * relativePower);
//			index++;
//		}
//		
//		//Calculating arrival rates per machine type
//		Map<MachineType, Double> arrivalRatesPerMachineType = new HashMap<MachineType, Double>();
//		for(MachineType type : currentPowerPerMachineType.keySet()){
//			if(totalPower == 0){
//				arrivalRatesPerMachineType.put(type, 0d);
//			}else{
//				arrivalRatesPerMachineType.put(type, (currentPowerPerMachineType.get(type) / totalPower) * arrivalRate);
//			}
//		}
//		
//		//Assuming a base computing power (e.g., EC2 unit for each core)
//		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
//		double totalThroughput = 0d;
//		double onDemandThroughput = 0d;
//		for(MachineType type : arrivalRatesPerMachineType.keySet()){
//			Double currentArrivalRate = arrivalRatesPerMachineType.get(type);
//			double maximumThroughput = (1 / (meanServiceTimeInMillis/1000)) * Configuration.getInstance().getRelativePower(type);//Using all cores
//			if(currentArrivalRate > maximumThroughput){//Requests are missed
//				throughputPerMachineType.put(type, maximumThroughput);
//				onDemandThroughput += currentArrivalRate - maximumThroughput;
//			}else{
//				throughputPerMachineType.put(type, currentArrivalRate);
//			}
//			totalThroughput += currentArrivalRate;
//		}
//		
//		if(totalThroughput == 0){//No arrival at reserved machines!
//			totalThroughput = arrivalRate;
//			onDemandThroughput = arrivalRate;
//		}
//		
//		double totalNumberOfUsers = aggregateNumberOfUsers();
//		double averageThinkTimeInSeconds = aggregateThinkTime();
//		
//		//Estimated response time
//		double responseTimeInSeconds = totalNumberOfUsers / totalThroughput - averageThinkTimeInSeconds;
//		
//		//Estimating utility
//		double receipt = calcReceipt();
//		double cost = calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, onDemandThroughput);
//		double penalties = calcPenalties(responseTimeInSeconds, arrivalRate, arrivalRate);
//		
//		double fitness = receipt - cost - penalties;
//		
//		if(fitness < 1){
//			return (1/Math.abs(fitness))+1;
//		}
//		
//		return fitness;
		
		//Since round-robin is used, the total arrival rate is splitted among reserved servers according to each server power
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		int index = 0;
		double totalPower = 0;
		for(Gene gene : arg0.getGenes()){
			Integer numberOfMachinesReserved = (Integer)gene.getAllele();
			MachineType type = this.types.get(index);
			
			double relativePower = Configuration.getInstance().getRelativePower(type);
			currentPowerPerMachineType.put(type, (int)Math.round(numberOfMachinesReserved * relativePower));
			totalPower += Math.round(numberOfMachinesReserved * relativePower);
			index++;
		}
		
		Map<MachineType, Double> totalRequestsFinished = new HashMap<MachineType, Double>();
		long responseTimeRequestsLost = 0;
		long throughputRequestsLost = 0;
		double ratesDifference = 0d;
		double accumulatedRequestsMeanServiceTime = 0d;
		
		int currentSummaryInterval = 0;
		int totalNumberOfIntervals = calcNumberOfIntervals();
		long maxRt = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
		
		while(currentSummaryInterval < totalNumberOfIntervals){
			
			double arrivalRate = aggregateArrivals(currentSummaryInterval);
			double meanServiceTimeInMillis = aggregateServiceTime(currentSummaryInterval);
			accumulatedRequestsMeanServiceTime += meanServiceTimeInMillis;
			
			//Calculating arrival rates per machine type
			Map<MachineType, Double> arrivalRatesPerMachineType = extractArrivalsPerMachineType(currentPowerPerMachineType, totalPower, arrivalRate);
			
			//Assuming a base computing power (e.g., EC2 unit for each core)
			Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
			double totalThroughput = 0d;
			double missingThroughput = 0d;
			for(MachineType type : arrivalRatesPerMachineType.keySet()){
				Double currentArrivalRate = arrivalRatesPerMachineType.get(type);
				double maximumThroughput = (1 / (meanServiceTimeInMillis/1000)) * Configuration.getInstance().getRelativePower(type);//Using all cores
				if(currentArrivalRate > maximumThroughput){//Requests are missed
					throughputPerMachineType.put(type, maximumThroughput);
					missingThroughput += currentArrivalRate - maximumThroughput;
					totalThroughput += maximumThroughput;
					
					Double totalFinished = totalRequestsFinished.get(type);
					if(totalFinished == null){
						totalFinished = 0d;
					}
					totalFinished += Math.round(maximumThroughput * SUMMARY_LENGTH_IN_SECONDS);
					totalRequestsFinished.put(type, totalFinished);
					
				}else{//All requests are attended!
					throughputPerMachineType.put(type, currentArrivalRate);
					totalThroughput += currentArrivalRate;
					
					Double totalFinished = totalRequestsFinished.get(type);
					if(totalFinished == null){
						totalFinished = 0d;
					}
					totalFinished += Math.round(currentArrivalRate * SUMMARY_LENGTH_IN_SECONDS);
					totalRequestsFinished.put(type, totalFinished);
				}
			}
			
			if(totalThroughput == 0){//No arrival at reserved machines!
//				totalThroughput = arrivalRate;
				missingThroughput = arrivalRate;
			}
			
			//Calculating missing requests. This value is amortized by queue size!
			long requestsMissed = Math.round(missingThroughput * SUMMARY_LENGTH_IN_SECONDS);
			if(ratesDifference == 0){//Queue starts at this interval, so some requests are not really missed!
				requestsMissed -= maxRt / meanServiceTimeInMillis;
			}
			ratesDifference = missingThroughput;
			throughputRequestsLost += requestsMissed;
			
			//Estimated response time
			double totalNumberOfUsers = aggregateNumberOfUsers(currentSummaryInterval);
			double averageThinkTimeInSeconds = aggregateThinkTime(currentSummaryInterval);
			if(totalThroughput != 0){
				double responseTimeInSeconds = totalNumberOfUsers / totalThroughput - averageThinkTimeInSeconds;
				double responseTimeLoss = Math.max((responseTimeInSeconds * 1000 - maxRt)/maxRt, 0);
				responseTimeRequestsLost += calcResponseTimeLoss(responseTimeLoss, totalRequestsFinished);
			}
			
			currentSummaryInterval++;
		}
		
		//Estimating utility
		double receipt = calcReceipt(totalNumberOfIntervals);
		double cost = calcCost(totalRequestsFinished, accumulatedRequestsMeanServiceTime / totalNumberOfIntervals, currentPowerPerMachineType, responseTimeRequestsLost, throughputRequestsLost);
		
		double fitness = receipt - cost;
		
		if(fitness < 1){
			return (1/Math.abs(fitness))+1;
		}
		
		return fitness;
		
	}
	
	/**
	 * This method distributes a certain arrival rate to existing reserved machine according to their processing power
	 * @param currentPowerPerMachineType
	 * @param totalPower
	 * @param arrivalRate
	 * @return
	 */
	protected Map<MachineType, Double> extractArrivalsPerMachineType(	Map<MachineType, Integer> currentPowerPerMachineType,
			double totalPower, double arrivalRate) {
		Map<MachineType, Double> arrivalRatesPerMachineType = new HashMap<MachineType, Double>();
		for(MachineType type : currentPowerPerMachineType.keySet()){
			if(totalPower == 0){
				arrivalRatesPerMachineType.put(type, 0d);
			}else{
				arrivalRatesPerMachineType.put(type, (currentPowerPerMachineType.get(type) / totalPower) * arrivalRate);
			}
		}
		return arrivalRatesPerMachineType;
	}
	
	/**
	 * This method gets the total amount of requests finished at a certain interval and applies a certain loss according to a percent
	 * of requests that finish after the SLA.
	 * @param responseTimeLoss
	 * @param totalRequestsFinished
	 * @return
	 */
	protected double calcResponseTimeLoss(double responseTimeLoss, Map<MachineType, Double> totalRequestsFinished) {
		double totalFinished = 0d;
		for(Double value : totalRequestsFinished.values()){
			totalFinished += value;
		}
		
		return totalFinished * responseTimeLoss;
	}
	
	/**
	 * This method calculates the number of intervals to be evaluated
	 * @return
	 */
	protected int calcNumberOfIntervals() {
		for(List<Summary> data : this.summaries.values()){
			return data.size();
		}
		
		return 0;
	}

	protected double calcPenalties(double responseTimeRequestsLost, double requestsThatCouldNotBeAttended, double totalRequestsFinished){
		
		double lossPerUser = (responseTimeRequestsLost + requestsThatCouldNotBeAttended) / (requestsThatCouldNotBeAttended + responseTimeRequestsLost +
				totalRequestsFinished) / this.cloudUsers.length;
		if(totalRequestsFinished == 0){
			lossPerUser = 1;
		}
		
		double penalty = 0d;
		for(User user : this.cloudUsers){
			penalty += user.calculatePenalty(lossPerUser);
		}
		return penalty;
	}

	protected double calcCost(Map<MachineType, Double> requestsFinishedPerMachineType, double meanServiceTimeInMillis, 
			Map<MachineType, Integer> currentPowerPerMachineType, double responseTimeRequestsLost, double throughputRequestsLost) {
		
		//Verifying on-demand resources that can be used
		double onDemandRisk = Configuration.getInstance().getDouble(SimulatorProperties.PLANNING_RISK);
		long onDemandResources = Math.round(cloudProviders[0].getOnDemandLimit() * (1-onDemandRisk));
		
//		long totalTimeInSeconds = Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD) * 24 * 60 * 60;
		Provider provider = cloudProviders[0];
		double cost = 0;
		
		//Reserved Costs
		long totalRequestsFinished = 0;
		for(MachineType type : requestsFinishedPerMachineType.keySet()){
//			Double throughput = requestsFinishedPerMachineType.get(type) / Configuration.getInstance().getRelativePower(type);
//			double CPUHoursPerType = (throughput * totalTimeInSeconds * meanServiceTimeInMillis) / 3600000;
//			
//			cost += provider.getReservationOneYearFee(type) * (currentPowerPerMachineType.get(type)/Configuration.getInstance().getRelativePower(type)) 
//							+ provider.getReservedCpuCost(type) * CPUHoursPerType;
			totalRequestsFinished += requestsFinishedPerMachineType.get(type);
			double CPUHoursPerType = (requestsFinishedPerMachineType.get(type) * meanServiceTimeInMillis) / 3600000;
			cost += provider.getReservationOneYearFee(type) * (currentPowerPerMachineType.get(type)/Configuration.getInstance().getRelativePower(type)) 
						+ provider.getReservedCpuCost(type) * CPUHoursPerType;
		}
		
		//On-demand costs
//		double onDemandCPUHours = (totalRequestsLost * totalTimeInSeconds * meanServiceTimeInMillis) / 3600000;
//		cost +=  provider.getOnDemandCpuCost(MachineType.SMALL) * onDemandCPUHours;
		double onDemandCPUHours = (throughputRequestsLost * meanServiceTimeInMillis) / 3600000;
		long requestsThatCouldNotBeAttended = 0;
		long planningPeriod = Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD);
		if(onDemandCPUHours > onDemandResources * planningPeriod * 24){//Demand is greater than what could be retrieved!
			requestsThatCouldNotBeAttended =  Math.round( (( onDemandCPUHours - 
			(onDemandResources * planningPeriod * 24) ) * 3600000 / meanServiceTimeInMillis) );
			
			onDemandCPUHours = onDemandResources * planningPeriod * 24;
		}
		cost += provider.getOnDemandCpuCost(MachineType.SMALL) * onDemandCPUHours;
		
		//Penalties
		double penalties = calcPenalties(responseTimeRequestsLost, requestsThatCouldNotBeAttended, totalRequestsFinished);
		
		return cost + penalties;
	}

	protected double calcReceipt(long numberOfIntervals) {
		UtilityResultEntry resultEntry = new UtilityResultEntry(0, this.cloudUsers, this.cloudProviders);

		double oneTimeFees = 0d;
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			Contract contract = entry.getKey().getContract();
			int index = 0;
			int counter = 0;
			long totalCPUHrs = 0;
			
			for(Summary summary : entry.getValue()){
				counter++;
				totalCPUHrs += (long) summary.getTotalCpuHrs();
				if(counter == SimpleSimulator.daysInMonths[index]){//Calculate receipt for a complete month!
					contract.calculateReceipt(resultEntry, entry.getKey().getId(), totalCPUHrs * 60 * 60 * 1000, 0l, 0l, 0l);
					index++;
					totalCPUHrs = 0;
					counter = 0;
				}
			}
			
			oneTimeFees += contract.calculateOneTimeFees();//Setup fees
		}
		
		return resultEntry.getReceipt() + oneTimeFees;
	}

	protected double aggregateThinkTime(int currentSummaryInterval) {
		double thinkTime = 0d;
		int totalNumberOfValues = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
//			double currentPeriodMean = 0;
//			for(Summary summary : entry.getValue()){
//				currentPeriodMean += summary.getUserThinkTimeInSeconds();
//			}
//			currentPeriodMean /= entry.getValue().size();
//			thinkTime += currentPeriodMean;
			thinkTime += entry.getValue().get(currentSummaryInterval).getUserThinkTimeInSeconds();
			totalNumberOfValues++;
		}
		
		return thinkTime / totalNumberOfValues;
	}

	protected double aggregateNumberOfUsers(int currentSummaryInterval) {
		int totalNumberOfUsers = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
//			int currentNumberOfUsers = 0;
//			for(Summary summary : entry.getValue()){
//				currentNumberOfUsers += summary.getNumberOfUsers();
//			}
//			currentNumberOfUsers /= entry.getValue().size();
//			totalNumberOfUsers += currentNumberOfUsers;
			totalNumberOfUsers += entry.getValue().get(currentSummaryInterval).getNumberOfUsers();
		}
		
		return totalNumberOfUsers;
	}

	protected double aggregateServiceTime(int currentSummaryInterval) {
		double serviceTime = 0d;
		int totalNumberOfValues = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
//			double currentPeriodMeanInMillis = 0;
//			for(Summary summary : entry.getValue()){
//				currentPeriodMeanInMillis += summary.getRequestServiceDemandInMillis();
//			}
//			currentPeriodMeanInMillis /= entry.getValue().size();
//			serviceTime += currentPeriodMeanInMillis;
			serviceTime += entry.getValue().get(currentSummaryInterval).getRequestServiceDemandInMillis();
			totalNumberOfValues++;
		}
		
		return serviceTime / totalNumberOfValues;
	}

	protected double aggregateArrivals(int currentSummaryInterval) {
		double totalArrivalRate = 0;
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
//			double meanArrivalRate = 0;
//			for(Summary summary : entry.getValue()){
//				meanArrivalRate += summary.getArrivalRate();
//			}
//			meanArrivalRate /= entry.getValue().size();
//			totalArrivalRate += meanArrivalRate;
			totalArrivalRate += entry.getValue().get(currentSummaryInterval).getArrivalRate();
		}
		
		return totalArrivalRate;
	}
	
	public static void main(String[] args) {
		int arrivaRate = 2;
		int completeRate = 1;
		List<Integer> queue = new ArrayList<Integer>();
		
		int lostCounter = 0;
		
		for(int i = 0; i < 100; i++){
			//Requests being completed
			for(int j = 0; j < completeRate; j++){
				if(queue.size() > 0){
					queue.remove(0);
				}
			}
			
			//Requests arriving
			for(int j = 0; j < arrivaRate; j++){
				if(queue.size() >= 5){
					System.out.println("Lost "+i);
					lostCounter++;
				}else{
					queue.add(i);
				}
			}
		}
		
		System.out.println(lostCounter);
	}

}
