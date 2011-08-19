package commons.cloud;

import static commons.sim.util.SimulatorProperties.PLANNING_PERIOD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.attribute.standard.Finishings;

import commons.cloud.UtilityResult.UtilityResultEntry;
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
	
	private double previousDebt;
	private int onDemandRunningMachines;
	
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
		
		this.types = new HashMap<MachineType, TypeProvider>();
		for (TypeProvider machineType : types) {
			this.types.put(machineType.getType(), machineType);
		}
		this.onDemandRunningMachines = 0;
		this.previousDebt = 0;
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
			return onDemandRunningMachines < getOnDemandLimit();
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
		if(!types.containsKey(instanceType)){
			throw new RuntimeException("Attempt to buy a machine of type " + instanceType + " at provider: " + getName());
		}
		if(!isReserved){
			this.onDemandRunningMachines++;
		}
		return types.get(instanceType).buyMachine(isReserved);
	}

	public boolean shutdownMachine(MachineDescriptor machineDescriptor) {
		if(!types.containsKey(machineDescriptor.getType())){
			return false;
		}
		boolean removed = types.get(machineDescriptor.getType()).shutdownMachine(machineDescriptor);
		if(!machineDescriptor.isReserved()){
			onDemandRunningMachines--;
		}
		return removed;
	}
	

	public void calculateCost(UtilityResultEntry entry) {
		
		entry.addToCost(previousDebt);
		
		double nextTurnDebt = 0;
		long [] transferences = new long[4];
				
		for (TypeProvider typeProvider : types.values()) {
			typeProvider.calculateFinishedMachinesCost(entry);
			nextTurnDebt += typeProvider.calculateRunningMachinesCost(entry);
			long [] typeTransferences = typeProvider.getTotalTransferences();
			for (int i = 0; i < typeTransferences.length; i++) {
				transferences[i] += typeTransferences[i];
			}
		}
		
		entry.addToCost(calcTransferenceCost(transferences[0]+transferences[2], transferInLimits, transferInCosts));
		entry.addToCost(calcTransferenceCost(transferences[1]+transferences[3], transferOutLimits, transferOutCosts));
		
		previousDebt = nextTurnDebt;
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
		double total = 0;
		return total;
	}

	public double calculateUniqueCost() {
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
