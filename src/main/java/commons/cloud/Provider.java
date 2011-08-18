package commons.cloud;

import static commons.sim.util.SimulatorProperties.PLANNING_PERIOD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import commons.config.Configuration;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;

/**
 * IaaS {@link Machine} provider.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Provider {
	
	private final String name;
	private final int onDemandLimit;
	private final int reservationLimit;
	private final Map<MachineType, TypeProvider> types;
	private final double monitoringCost;
	private final long[] transferInLimits;
	private final double[] transferInCosts;
	private final long[] transferOutLimits;
	private final double[] transferOutCosts;
	
	private List<MachineDescriptor> onDemandRunningMachines;
	private List<MachineDescriptor> onDemandFinishedMachines;
	
	public Provider(String name, int onDemandLimit,
			int reservationLimit, double monitoringCost,
			long[] transferInLimits,
			double[] transferInCosts,
			long[] transferOutLimits, double[] transferOutCosts,
			List<TypeProvider> types) {
		this.name = name;
		this.onDemandLimit = onDemandLimit;
		this.reservationLimit = reservationLimit;
		this.monitoringCost = monitoringCost;
		this.transferInLimits = transferInLimits;
		this.transferInCosts = transferInCosts;
		this.transferOutLimits = transferOutLimits;
		this.transferOutCosts = transferOutCosts;
		
		this.onDemandRunningMachines = new ArrayList<MachineDescriptor>();
		this.onDemandFinishedMachines = new ArrayList<MachineDescriptor>();
		
		this.types = new HashMap<MachineType, TypeProvider>();
		for (TypeProvider machineType : types) {
			this.types.put(machineType.getType(), machineType);
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
	public double getOnDemandCpuCost(MachineType type) {
		return types.get(type).getOnDemandCpuCost();
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
	public double getReservedCpuCost(MachineType type) {
		return types.get(type).getReservedCpuCost();
	}

	/**
	 * @return the reservationOneYearFee
	 */
	public double getReservationOneYearFee(MachineType type) {
		return types.get(type).getReservationOneYearFee();
	}

	/**
	 * @return the reservationThreeYearsFee
	 */
	public double getReservationThreeYearsFee(MachineType type) {
		return types.get(type).getReservationThreeYearsFee();
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


	public boolean canBuyMachine(boolean reserved, MachineType type) {
		if(!reserved){
			return onDemandRunningMachines.size() < getOnDemandLimit();
		}
		return types.containsKey(type)?types.get(type).canBuy():false;
	}
	
	/**
	 * Buy a new machine in this {@link Provider}.
	 * @param isReserved <code>true</code> if such machine is a previously reserved one. 
	 * @param instanceType See {@link MachineType}
	 * @return A new {@link MachineDescriptor} if succeeded in creation, or <code>null</code> otherwise.
	 */
	public MachineDescriptor buyMachine(boolean isReserved, MachineType instanceType) {
		if(!isReserved){
			MachineDescriptor descriptor = new MachineDescriptor(IDGenerator.GENERATOR.next(), isReserved, instanceType);
			this.onDemandRunningMachines.add(descriptor);
			return descriptor;
		}
		if(!types.containsKey(instanceType)){
			throw new RuntimeException("Attempt to buy a machine of type " + instanceType + " at provider: " + getName());
		}
		return types.get(instanceType).buyMachine();
	}

	public boolean shutdownMachine(MachineDescriptor machineDescriptor) {
		if(machineDescriptor.isReserved()){
			if(!types.containsKey(machineDescriptor.getType())){
				return false;
			}
			return types.get(machineDescriptor.getType()).shutdownMachine(machineDescriptor);
		}
		return onDemandRunningMachines.remove(machineDescriptor.getMachineID());
	}
	
	private double calculateCost(MachineDescriptor descriptor){
		double executionTime = descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis();
		executionTime = Math.ceil(1.0 * executionTime / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		
		if(executionTime < 0){
			throw new RuntimeException(this.getClass()+": Invalid machine"+ descriptor.getMachineID() +" runtime for cost: "+executionTime);
		}
		
		if(descriptor.isReserved()){
			return executionTime * types.get(descriptor.getType()).getReservedCpuCost() + executionTime * getMonitoringCost();
		}else{
			return executionTime * types.get(descriptor.getType()).getOnDemandCpuCost() + executionTime * getMonitoringCost();
		}
	}
	
	private double calculateCost(MachineDescriptor descriptor, long currentTime){
		double executionTime = currentTime - descriptor.getStartTimeInMillis();
		executionTime = Math.ceil(1.0 * executionTime / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		
		if(executionTime < 0){
			throw new RuntimeException(this.getClass()+": Invalid machine"+ descriptor.getMachineID() +" runtime for cost: "+executionTime);
		}
		
		if(descriptor.isReserved()){
			return executionTime * types.get(descriptor.getType()).getReservedCpuCost() + executionTime * getMonitoringCost();
		}else{
			return executionTime * types.get(descriptor.getType()).getOnDemandCpuCost() + executionTime * getMonitoringCost();
		}
	}

	public double calculateCost(long currentTimeInMillis, int maximumNumberOfReservedResourcesUsed) {
		double cost = 0;
		long inTransference = 0;
		long outTransference = 0;
		int currentNumberOfReservedResources = 0;
		
		//Finished machines
		for (MachineDescriptor descriptor : onDemandFinishedMachines) {
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
		
		for(MachineDescriptor descriptor : onDemandRunningMachines.values()){
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
			long planningPeriod = Configuration.getInstance().getLong(PLANNING_PERIOD);
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
		this.onDemandFinishedMachines.clear();
	}
	
	public double calculateUnicCost() {
		double result = this.totalNumberOfReservedMachinesUsed * this.reservationOneYearFee;
		this.totalNumberOfReservedMachinesUsed = 0;
		return result;
	}
	
	public double[] resourcesConsumption() {
		long onDemandConsumed = 0;
		long reservedConsumed = 0;
		
		int numberOfOnDemandResources = 0;
		int numberOfReservedResources = 0;
		
		for(MachineDescriptor descriptor : this.onDemandFinishedMachines){
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
}
