package commons.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.config.Configuration;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;
import commons.sim.util.SimulatorProperties;
import commons.util.Triple;

public class Provider {
	
	private static int machineIDGenerator = 0;
	private int currentOnDemandMachines;
	private int currentReservedMachines;

	public final String name;
	public final double onDemandCpuCost;// in $/instance-hour
	public final int onDemandLimit;// in number of instances
	public final int reservationLimit;// in number of instances
	public final double reservedCpuCost;// in $/instance-hour
	public final double reservationOneYearFee;// in $
	public final double reservationThreeYearsFee;// in $
	public final double monitoringCost;// in $
	private final long[] transferInLimits;
	private final double[] transferInCosts;
	private final long[] transferOutLimits;
	private final double[] transferOutCosts;
	
	private Map<Long, MachineDescriptor> runningMachines;
	private List<MachineDescriptor> finishedMachines;
	
	private int totalNumberOfReservedMachinesUsed;

	public Provider(String name, double cpuCost, int onDemandLimit,
			int reservationLimit, double reservedCpuCost, double reservationOneYearFee,
			double reservationThreeYearsFee, double monitoringCost,
			long[] transferInLimits, double[] transferInCosts,
			long[] transferOutLimits, double[] transferOutCosts) {
		
		this.name = name;
		this.onDemandCpuCost = cpuCost;
		this.onDemandLimit = onDemandLimit;
		this.reservationLimit = reservationLimit;
		this.reservedCpuCost = reservedCpuCost;
		this.reservationOneYearFee = reservationOneYearFee;
		this.reservationThreeYearsFee = reservationThreeYearsFee;
		this.monitoringCost = monitoringCost;
		this.transferInLimits = transferInLimits;
		this.transferInCosts = transferInCosts;
		this.transferOutLimits = transferOutLimits;
		this.transferOutCosts = transferOutCosts;
		
		this.currentOnDemandMachines = 0;
		this.currentReservedMachines = 0;
		
		this.runningMachines = new HashMap<Long, MachineDescriptor>();
		this.finishedMachines = new ArrayList<MachineDescriptor>();
		this.totalNumberOfReservedMachinesUsed = 0;
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
	public double getOnDemandCpuCost() {
		return onDemandCpuCost;
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
	public double getReservedCpuCost() {
		return reservedCpuCost;
	}

	/**
	 * @return the reservationOneYearFee
	 */
	public double getReservationOneYearFee() {
		return reservationOneYearFee;
	}

	/**
	 * @return the reservationThreeYearsFee
	 */
	public double getReservationThreeYearsFee() {
		return reservationThreeYearsFee;
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
		return new MachineDescriptor(machineIDGenerator++, reserved);
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
		if(descriptor.isReserved()){
			return (descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis()) * reservedCpuCost;
		}else{
			return (descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis()) * onDemandCpuCost;
		}
	}
	
	private double calculateCost(MachineDescriptor descriptor, long currentTime){
		if(descriptor.isReserved()){
			return (currentTime - descriptor.getStartTimeInMillis()) * reservedCpuCost;
		}else{
			return (currentTime - descriptor.getStartTimeInMillis()) * onDemandCpuCost;
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
			int planningPeriod = Configuration.getInstance().getInt(SimulatorProperties.PLANNING_PERIOD);
			double fee = (planningPeriod == 1) ? this.reservationOneYearFee : this.reservationThreeYearsFee;
			cost += (currentNumberOfReservedResources-maximumNumberOfReservedResourcesUsed) * fee; 
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
