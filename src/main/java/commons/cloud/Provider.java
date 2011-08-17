package commons.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Provider.MACHINE_TYPES;
import commons.config.Configuration;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;

public class Provider {
	
	
	protected static int machineIDGenerator = 0;
	
	private int currentOnDemandMachines;
	private int currentReservedMachines;

	public final String name;
	public final int onDemandLimit;// in number of instances
	public final int reservationLimit;// in number of instances
	private final Map<MachineTypeValue, MachineType> types;
	public final double monitoringCost;// in $
	private final long[] transferInLimits;
	private final double[] transferInCosts;
	private final long[] transferOutLimits;
	private final double[] transferOutCosts;
	
	private Map<Long, MachineDescriptor> runningMachines;
	private List<MachineDescriptor> finishedMachines;
	
	private int totalNumberOfReservedMachinesUsed;

	public Provider(String name, int onDemandLimit,
			int reservationLimit, double monitoringCost,
			long[] transferInLimits,
			double[] transferInCosts,
			long[] transferOutLimits, double[] transferOutCosts,
			List<MachineType> types) {
		this.name = name;
		this.onDemandLimit = onDemandLimit;
		this.reservationLimit = reservationLimit;
		this.monitoringCost = monitoringCost;
		this.transferInLimits = transferInLimits;
		this.transferInCosts = transferInCosts;
		this.transferOutLimits = transferOutLimits;
		this.transferOutCosts = transferOutCosts;
		this.verifyProperties();
		
		this.currentOnDemandMachines = 0;
		this.currentReservedMachines = 0;
		
		this.runningMachines = new HashMap<Long, MachineDescriptor>();
		this.finishedMachines = new ArrayList<MachineDescriptor>();
		this.totalNumberOfReservedMachinesUsed = 0;
		
		this.types = new HashMap<MachineTypeValue, MachineType>();
		for (MachineType machineType : types) {
			this.types.put(machineType.getValue(), machineType);
		}
	}

	private void verifyProperties() {
		Double[] onDemandCosts = new Double[]{};
		Double[] reservedCosts = new Double[]{};
		
		this.onDemandCpuCost.values().toArray(onDemandCosts);
		this.reservedCpuCost.values().toArray(reservedCosts);
		
		for(int i = 0; i < onDemandCosts.length; i++){
			if(onDemandCosts[i] < 0 || reservedCosts[i] < 0){
				throw new RuntimeException(this.getClass()+": Invalid cpu/hour cost!");
			}
		}
		
		if(this.reservationLimit <= 0 || this.onDemandLimit <= 0){
			throw new RuntimeException(this.getClass()+": Invalid on-demand/reserved limit!");
		}
		
		Double[] oneYearFees = new Double[]{};
		Double[] threeYearsFees = new Double[]{};
		
		this.reservationOneYearFee.values().toArray(oneYearFees);
		this.reservationThreeYearsFee.values().toArray(threeYearsFees);
		
		for(int i = 0; i < oneYearFees.length; i++){
			if(oneYearFees[i] < 0 || threeYearsFees[i] < 0){
				throw new RuntimeException(this.getClass()+": Invalid reservation fees!");
			}
		}
		
		if(this.monitoringCost < 0){
			throw new RuntimeException(this.getClass()+": Invalid monitoring cost!");
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the onDemandCpuCost
	 */
	public double getOnDemandCpuCost(MachineTypeValue type) {
		return onDemandCpuCost.get(type);
	}

	/**
	 * @return the onDemandLimit
	 */
	public int getOnDemandLimit() {
		return onDemandLimit;
	}

	/**
	 * @return the reservationLimit
	 */
	public int getReservationLimit() {
		return reservationLimit;
	}

	/**
	 * @return the reservedCpuCost
	 */
	public double getReservedCpuCost(MachineTypeValue type) {
		return reservedCpuCost.get(type);
	}

	/**
	 * @return the reservationOneYearFee
	 */
	public double getReservationOneYearFee(MachineTypeValue type) {
		return reservationOneYearFee.get(type);
	}

	/**
	 * @return the reservationThreeYearsFee
	 */
	public double getReservationThreeYearsFee(MachineTypeValue type) {
		return reservationThreeYearsFee.get(type);
	}

	/**
	 * @return the monitoringCost
	 */
	public double getMonitoringCost() {
		return monitoringCost;
	}

	/**
	 * @return the transferInLimits
	 */
	public long[] getTransferInLimits() {
		return transferInLimits;
	}

	/**
	 * @return the transferInCosts
	 */
	public double[] getTransferInCosts() {
		return transferInCosts;
	}

	/**
	 * @return the transferOutLimits
	 */
	public long[] getTransferOutLimits() {
		return transferOutLimits;
	}

	/**
	 * @return the transferOutCosts
	 */
	public double[] getTransferOutCosts() {
		return transferOutCosts;
	}


	public boolean canBuyMachine(boolean reserved) {
		return reserved ? currentReservedMachines < reservationLimit: currentOnDemandMachines < onDemandLimit;
	}
	
	public MachineDescriptor buyMachine(boolean reserved) {
		if(reserved){
			currentReservedMachines++;
		}else{
			currentOnDemandMachines++;
		}
		MachineDescriptor descriptor = new MachineDescriptor(machineIDGenerator++, reserved, MachineTypeValue.SMALL);
		this.runningMachines.put(descriptor.getMachineID(), descriptor);
		
		return descriptor;
	}

	public boolean shutdownMachine(MachineDescriptor machineDescriptor) {
		if(runningMachines.remove(machineDescriptor.getMachineID()) == null){
			return false;
		}
		finishedMachines.add(machineDescriptor);
		if(machineDescriptor.isReserved()){
			currentReservedMachines--;
		}else{
			currentOnDemandMachines--;
		}
		return true;
	}
	
	private double calculateCost(MachineDescriptor descriptor){
		double executionTime = descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis();
		executionTime = Math.ceil(1.0 * executionTime / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		
		if(executionTime < 0){
			throw new RuntimeException(this.getClass()+": Invalid machine"+ descriptor.getMachineID() +" runtime for cost: "+executionTime);
		}
		
		if(descriptor.isReserved()){
			return executionTime * this.reservedCpuCost.get(descriptor.getType()) + executionTime * monitoringCost;
		}else{
			return executionTime * this.onDemandCpuCost.get(descriptor.getType()) + executionTime * monitoringCost;
		}
	}
	
	private double calculateCost(MachineDescriptor descriptor, long currentTime){
		double executionTime = currentTime - descriptor.getStartTimeInMillis();
		executionTime = Math.ceil(1.0 * executionTime / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		
		if(executionTime < 0){
			throw new RuntimeException(this.getClass()+": Invalid machine"+ descriptor.getMachineID() +" runtime for cost: "+executionTime);
		}
		
		if(descriptor.isReserved()){
			return executionTime * this.reservedCpuCost.get(descriptor.getType()) + executionTime * monitoringCost;
		}else{
			return executionTime * this.onDemandCpuCost.get(descriptor.getType()) + executionTime * monitoringCost;
		}
	}

	public double calculateCost(long currentTimeInMillis, int maximumNumberOfReservedResourcesUsed) {
		double cost = 0;
		long inTransference = 0;
		long outTransference = 0;
		int currentNumberOfReservedResources = 0;
		
		//Finished machines
		for (MachineDescriptor descriptor : finishedMachines) {
			cost += calculateCost(descriptor);
			inTransference += descriptor.getInTransference();
			outTransference += descriptor.getOutTransference();
			if(descriptor.isReserved()){
				this.totalNumberOfReservedMachinesUsed++;
				currentNumberOfReservedResources++;
			}
		}
		cost += calcTransferenceCost(inTransference, transferInLimits, transferInCosts);
		cost += calcTransferenceCost(outTransference, transferOutLimits, transferOutCosts);
		
		//Current running machines
		long runningIn = 0;
		long runningOut = 0;
		
		for(MachineDescriptor descriptor : runningMachines.values()){
			double currentCost = calculateCost(descriptor, currentTimeInMillis);
			double alreadyPayed = descriptor.getCostAlreadyPayed();
			descriptor.setCostAlreadyPayed(currentCost);
			cost += currentCost - alreadyPayed;
			
			long currentIn = descriptor.getInTransference();
			long inPayed = descriptor.getInTransferencePayed();
			descriptor.setInTransferencePayed(currentIn);
			runningIn += currentIn - inPayed;
			
			long currentOut = descriptor.getOutTransference();
			long outPayed = descriptor.getOutTransferencePayed();
			descriptor.setOutTransferencePayed(currentOut);
			runningOut += currentOut - outPayed;
			
			if(descriptor.isReserved()){
				this.totalNumberOfReservedMachinesUsed++;
				currentNumberOfReservedResources++;
			}
		}
		
		cost += calcTransferenceCost(runningIn, transferInLimits, transferInCosts);
		cost += calcTransferenceCost(runningOut, transferOutLimits, transferOutCosts);
		
		if(currentNumberOfReservedResources > maximumNumberOfReservedResourcesUsed){//Charging reservation fees
			long planningPeriod = Configuration.getInstance().getPlanningPeriod();
			double fee = (planningPeriod == 1) ? this.reservationOneYearFee : this.reservationThreeYearsFee;
			cost += (currentNumberOfReservedResources - maximumNumberOfReservedResourcesUsed) * fee; 
		}
		
		this.resetCostCounters();
		
		return cost;
	}
	
	/**
	 * TODO Code me!
	 * @param totalTransfered
	 * @param limits
	 * @param costs
	 * @return
	 */
	private double calcTransferenceCost(long totalTransfered,
			long[] limits, double[] costs) {
		return 0;
	}

	public void resetCostCounters(){
		this.finishedMachines.clear();
	}
	
	public double calculateUnicCost() {
		double result = this.totalNumberOfReservedMachinesUsed * this.reservationOneYearFee;
		this.totalNumberOfReservedMachinesUsed = 0;
		return result;
	}
	
	
//	//Deprecated!
//	public double calculateCost(double consumedTransference) {
//		return this.calculateReservationCosts() + this.calculateOnDemandCosts() + this.calculateTransferenceCosts(consumedTransference);
//	}
//	
//	//TODO
//	private double calculateTransferenceCosts(double consumedTransference) {
//		return 0;
//	}
//	
//	private double calculateOnDemandCosts() {
//		double totalConsumed = this.onDemandConsumption();
//		return totalConsumed * this.onDemandCpuCost + totalConsumed * monitoringCost;
//	}
//
//	private double calculateReservationCosts() {
//		double totalConsumed = this.reservedConsumption();
//		return this.reservedResources.size() * this.reservationOneYearFee + 
//		totalConsumed * this.reservedCpuCost + totalConsumed * monitoringCost;
//	}

	public double[] resourcesConsumption() {
		long onDemandConsumed = 0;
		long reservedConsumed = 0;
		
		int numberOfOnDemandResources = 0;
		int numberOfReservedResources = 0;
		
		for(MachineDescriptor descriptor : this.finishedMachines){
			long executionTime = descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis();
			if(executionTime < 0){
				throw new RuntimeException("Invalid cpu usage in machine "+descriptor.getMachineID()+" : "+executionTime);
			}
			if(!descriptor.isReserved()){
				onDemandConsumed += Math.ceil(1.0 * executionTime / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
				numberOfOnDemandResources++;
			}else{
				reservedConsumed += Math.ceil(1.0 * executionTime / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
				numberOfReservedResources++;
			}
		}
		
		double[] result = new double[4];
		result[0] = onDemandConsumed;
		result[1] = numberOfOnDemandResources;
		result[2] = reservedConsumed; 
		result[3] = numberOfReservedResources;
		return result;
	}

//	public double reservedConsumption() {
//		double totalConsumed = 0;
//		for(Long machineID : this.reservedResources.keySet()){
//			Triple<Long, Long, Double> triple = this.reservedResources.get(machineID);
//			
//			long executionTime;
//			if(triple.secondValue != null){
//				executionTime = triple.secondValue - triple.firstValue;
//			}else{
//				executionTime = 0;
//			}
//			
//			if(executionTime < 0){
//				throw new RuntimeException("Invalid cpu usage in machine "+machineID.toString()+" : "+executionTime);
//			}
//			totalConsumed += Math.ceil(1.0 * executionTime / UtilityFunction.HOUR_IN_MILLIS);
//		}
//		return totalConsumed;
//	}
}
