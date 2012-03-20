package saasim.planning.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import org.jgap.FitnessFunction;
//import org.jgap.Gene;
//import org.jgap.IChromosome;

import saasim.cloud.Contract;
import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.cloud.UtilityResultEntry;
import saasim.config.Configuration;
import saasim.planning.util.Summary;
import saasim.sim.components.Machine;
import saasim.sim.util.SaaSAppProperties;
import saasim.sim.util.SimulatorProperties;
import saasim.util.SimulationInfo;


/**
 * This class represents a {@link FitnessFunction} adapted for the planning. One {@link FitnessFunction} is used to
 * makes algorithms based on genetic combination, and in this application, {@link PlanningFitnessFunction} is used to
 * makes the planning in the heuristics {@link AGHeuristic} and {@link OptimalHeuristic}.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class PlanningFitnessFunction{// extends FitnessFunction{

	public static final int HOUR_IN_MILLIS = 3600000;

	private long SUMMARY_LENGTH_IN_SECONDS;
	
	private final Provider[] cloudProviders;
	private final User[] cloudUsers;
	private final Map<User, List<Summary>> summaries;
	private final List<MachineType> types;

	/**
	 * Deafult constructor.
	 * @param summaries 
	 * @param cloudUsers the {@link User}s of application
	 * @param cloudProviders the {@link Provider}s of application
	 * @param types a list containing {@link MachineType}s
	 */
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

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	protected double evaluate(IChromosome arg0) {
//		//Since round-robin is used, the total arrival rate is splitted among reserved servers according to each server power
//		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
//		int index = 0;
//		double totalPower = 0;
//		for(Gene gene : arg0.getGenes()){
//			Integer numberOfMachinesReserved = (Integer)gene.getAllele();
//			MachineType type = this.types.get(index);
//			
//			currentPowerPerMachineType.put(type, (int)Math.round(1.0 * numberOfMachinesReserved * type.getNumberOfCores()));
//			totalPower += Math.round(1.0 * numberOfMachinesReserved * type.getNumberOfCores());
//			index++;
//		}
//		
//		Map<MachineType, Double> totalRequestsFinished = new HashMap<MachineType, Double>();
//		long requestsLostDueToResponseTime = 0;
//		long requestsLostDueToThroughput = 0;
//		double ratesDifference = 0d;
//		double accumulatedRequestsMeanServiceTime = 0d;
//		
//		int currentSummaryInterval = 0;
//		int totalNumberOfIntervals = calcNumberOfIntervals();
//		long maxResponseTimeInMillis = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
//		
//		while(currentSummaryInterval < totalNumberOfIntervals){
//			
//			double arrivalRate = aggregateArrivals(currentSummaryInterval);
//			double meanServiceTimeInMillis = aggregateServiceDemand(currentSummaryInterval);
//			accumulatedRequestsMeanServiceTime += meanServiceTimeInMillis;
//			
//			//Calculating arrival rates per machine type
//			Map<MachineType, Double> arrivalRatesPerMachineType = extractArrivalsPerMachineType(currentPowerPerMachineType, totalPower, arrivalRate);
//			
//			//Assuming a base computing power (e.g., EC2 unit for each core)
//			Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
//			double reservedThroughput = 0d;
//			double missingThroughput = 0d;
//			for(MachineType type : arrivalRatesPerMachineType.keySet()){
//				Double currentArrivalRate = arrivalRatesPerMachineType.get(type);
//				double maximumThroughput = (1 / (meanServiceTimeInMillis/1000)) * type.getNumberOfCores();//Using all cores
//				if(currentArrivalRate > maximumThroughput){//Requests are missed
//					throughputPerMachineType.put(type, maximumThroughput);
//					missingThroughput += (currentArrivalRate - maximumThroughput);
//					reservedThroughput += maximumThroughput;
//					
//					Double totalFinished = totalRequestsFinished.get(type);
//					if(totalFinished == null){
//						totalFinished = 0d;
//					}
//					totalFinished += Math.round(maximumThroughput * SUMMARY_LENGTH_IN_SECONDS);
//					totalRequestsFinished.put(type, totalFinished);
//					
//				}else{//All requests are attended!
//					throughputPerMachineType.put(type, currentArrivalRate);
//					reservedThroughput += currentArrivalRate;
//					
//					Double totalFinished = totalRequestsFinished.get(type);
//					if(totalFinished == null){
//						totalFinished = 0d;
//					}
//					totalFinished += Math.round(currentArrivalRate * SUMMARY_LENGTH_IN_SECONDS);
//					totalRequestsFinished.put(type, totalFinished);
//				}
//			}
//			
//			if(reservedThroughput == 0){//No arrival at reserved machines!
//				missingThroughput = arrivalRate;
//			}
//			
//			//Calculating missing requests. This value is amortized by queue size!
//			long requestsMissed = Math.round(missingThroughput * SUMMARY_LENGTH_IN_SECONDS);
//			if(ratesDifference == 0 && reservedThroughput != 0){//Queue starts at this interval, so some requests are not really missed!
//				requestsMissed -= (maxResponseTimeInMillis / meanServiceTimeInMillis) * totalPower;
//			}
//			ratesDifference = missingThroughput;
//			requestsLostDueToThroughput += requestsMissed;
//			
//			//Estimated response time
//			double totalNumberOfUsers = aggregateNumberOfUsers(currentSummaryInterval) * (reservedThroughput / arrivalRate);
//			double averageThinkTimeInSeconds = aggregateThinkTime(currentSummaryInterval);
//			if(reservedThroughput != 0){
//				double responseTimeInSeconds = totalNumberOfUsers / reservedThroughput - averageThinkTimeInSeconds;
//				double responseTimeLoss = Math.max( (responseTimeInSeconds * 1000 - maxResponseTimeInMillis)/maxResponseTimeInMillis, 0 );
//				requestsLostDueToResponseTime += calcResponseTimeLoss(responseTimeLoss, totalRequestsFinished);
//			}
//			
//			currentSummaryInterval++;
//		}
//		
//		//Estimating utility
//		double receipt = calcReceipt();
//		double cost = calcCost(totalRequestsFinished, accumulatedRequestsMeanServiceTime / totalNumberOfIntervals, currentPowerPerMachineType, requestsLostDueToResponseTime, requestsLostDueToThroughput);
//		
//		double fitness = receipt - cost;
//		
//		if(fitness < 1){
//			return (1/Math.abs(fitness))+1;
//		}
//		return fitness;
//	}
	
	/**
	 * This method distributes a certain arrival rate to existing reserved machine according to their processing power
	 * @param currentPowerPerMachineType a {@link Map} containing the {@link Machine}s and them several power
	 * @param totalPower the total power
	 * @param arrivalRate the arrival rate
	 * @return A {@link Map} containing the {@link MachineType}s and them several arrivals
	 */
	protected Map<MachineType, Double> extractArrivalsPerMachineType(Map<MachineType, Integer> currentPowerPerMachineType,
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
	 * @return the calculate response time loss 
	 */
	protected double calcResponseTimeLoss(double responseTimeLoss, Map<MachineType, Double> totalRequestsFinished) {
		double totalFinished = 0d;
		for(Double value : totalRequestsFinished.values()){
			totalFinished += value;
		}
		return totalFinished * responseTimeLoss;
	}
	
	/**
	 * This method calculates the number of intervals to be evaluated.
	 * @return The number of intervals to be evaluated.
	 */
	protected int calcNumberOfIntervals() {
		for(List<Summary> data : this.summaries.values()){
			return data.size();
		}
		return 0;
	}

	/**
	 * Calculates the penalties of the lost {@link Request}s. 
	 * @param responseTimeRequestsLost
	 * @param requestsThatCouldNotBeAttended
	 * @param totalRequestsFinished
	 * @return The penalty calculated.
	 */
	protected double calcPenalties(double responseTimeRequestsLost, double requestsThatCouldNotBeAttended, double totalRequestsFinished){
		double slaInfractionPercentage = (responseTimeRequestsLost) / (responseTimeRequestsLost + totalRequestsFinished) / this.cloudUsers.length;
		if(totalRequestsFinished == 0){
			slaInfractionPercentage = 1;
		}
		
		double penalty = 0d;
		for(User user : this.cloudUsers){
			penalty += user.calculatePenalty(slaInfractionPercentage, 0);
		}
		return penalty;
	}

	/**
	 * Calculate the costs for this application added the penalty.
	 * @param requestsFinishedPerMachineType
	 * @param meanServiceTimeInMillis
	 * @param currentPowerPerMachineType
	 * @param requestsLostDueToResponseTime
	 * @param requestsLostDueToThroughput
	 * @return The costs for this application.
	 */
	protected double calcCost(Map<MachineType, Double> requestsFinishedPerMachineType, double meanServiceTimeInMillis, 
			Map<MachineType, Integer> currentPowerPerMachineType, double requestsLostDueToResponseTime, double requestsLostDueToThroughput) {
		
		//Verifying on-demand resources that can be used
		double onDemandRisk = Configuration.getInstance().getDouble(SimulatorProperties.PLANNING_RISK);
		long onDemandResources = Math.round(cloudProviders[0].getOnDemandLimit() * (1-onDemandRisk));
		
		Provider provider = cloudProviders[0];
		double cost = 0;
		
		//Reserved Costs
		long totalRequestsFinished = 0;
		for(MachineType type : requestsFinishedPerMachineType.keySet()){
			totalRequestsFinished += requestsFinishedPerMachineType.get(type);
			double CPUHoursPerType = (requestsFinishedPerMachineType.get(type) * meanServiceTimeInMillis) / HOUR_IN_MILLIS;
			cost += provider.getReservationOneYearFee(type) * ( currentPowerPerMachineType.get(type)/type.getNumberOfCores() ) 
						+ provider.getReservedCpuCost(type) * CPUHoursPerType;
		}
		
		//On-demand costs
		double onDemandCPUHours = (requestsLostDueToThroughput * meanServiceTimeInMillis) / HOUR_IN_MILLIS;
		long requestsThatCouldNotBeAttended = 0;
		long planningPeriod = Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD);
		if(onDemandCPUHours > onDemandResources * planningPeriod * 24){//Demand is greater than what could be retrieved!
			requestsThatCouldNotBeAttended =  Math.round( (( onDemandCPUHours - 
			(onDemandResources * planningPeriod * 24) ) * HOUR_IN_MILLIS / meanServiceTimeInMillis) );
			
			onDemandCPUHours = onDemandResources * planningPeriod * 24;
			totalRequestsFinished -= requestsThatCouldNotBeAttended; 
		}
		totalRequestsFinished += requestsLostDueToThroughput;
		cost += provider.getOnDemandCpuCost(MachineType.M1_SMALL) * onDemandCPUHours;
		
		//Penalties
		double penalties = calcPenalties(requestsLostDueToResponseTime, requestsThatCouldNotBeAttended, totalRequestsFinished);
		
		return cost + penalties;
	}

	/**
	 * Calculates the receipt for this application. 
	 * @return The receipt.
	 */
	protected double calcReceipt() {
		UtilityResultEntry resultEntry = new UtilityResultEntry(0, new Contract[]{}, this.cloudUsers, this.cloudProviders);

		double oneTimeFees = 0d;
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			Contract contract = entry.getKey().getContract();
			int index = 0;
			int counter = 0;
			double totalCPUHrs = 0;
			
			for(Summary summary : entry.getValue()){
				counter++;
				totalCPUHrs += summary.getTotalCpuHrs();
				if(counter == (SimulationInfo.daysInMonths[index]+1) * 24){//Calculate receipt for a complete month!
					contract.calculateReceipt(entry.getKey().getId(), (long)Math.ceil(totalCPUHrs * 60 * 60 * 1000), 0l, 0l, 0l, 0, 0, 0);
					index++;
					totalCPUHrs = 0;
				}
			}
			oneTimeFees += contract.calculateOneTimeFees();//Setup fees
		}
		return resultEntry.getReceipt() + oneTimeFees;
	}

	/**
	 * Aggregates think time.
	 * @param currentSummaryInterval
	 * @return The value of aggregated think time.
	 */
	protected double aggregateThinkTime(int currentSummaryInterval) {
		double thinkTime = 0d;
		int totalNumberOfValues = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			thinkTime += entry.getValue().get(currentSummaryInterval).getUserThinkTimeInSeconds();
			totalNumberOfValues++;
		}
		return thinkTime / totalNumberOfValues;
	}

	/**
	 * Aggregates the number of users.
	 * @param currentSummaryInterval
	 * @return The value of aggregated number of users. 
	 */
	protected double aggregateNumberOfUsers(int currentSummaryInterval) {
		int totalNumberOfUsers = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			totalNumberOfUsers += entry.getValue().get(currentSummaryInterval).getNumberOfUsers();
		}
		return totalNumberOfUsers;
	}

	/**
	 * Aggregates the service demand.
	 * @param currentSummaryInterval
	 * @return The value of the aggregated service time. 
	 */
	protected double aggregateServiceDemand(int currentSummaryInterval) {
		double serviceTime = 0d;
		int totalNumberOfValues = 0;
		
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
			serviceTime += entry.getValue().get(currentSummaryInterval).getRequestServiceDemandInMillis();
			totalNumberOfValues++;
		}
		return serviceTime / totalNumberOfValues;
	}

	/**
	 * Aggregated the arrivals.
	 * @param currentSummaryInterval
	 * @return The value of the aggregated arrival rate.
	 */
	protected double aggregateArrivals(int currentSummaryInterval) {
		double totalArrivalRate = 0;
		for(Entry<User, List<Summary>> entry : this.summaries.entrySet()){
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
