package commons.cloud;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.util.CostCalculus;

/**
 * IaaS {@link Machine} provider.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Provider implements Serializable{
	
	private final int id;
	private final String name;
	private final int onDemandLimit;
	private final int reservationLimit;
	private final Map<MachineType, TypeProvider> types;
	private final double monitoringCost;
	private final long[] transferInLimits;
	private final double[] transferInCosts;
	private final long[] transferOutLimits;
	private final double[] transferOutCosts;
	
	private int onDemandRunningMachines;
	
	public Provider(int id, String name,
			int onDemandLimit, int reservationLimit,
			double monitoringCost,
			long[] transferInLimits,
			double[] transferInCosts, long[] transferOutLimits,
			double[] transferOutCosts, List<TypeProvider> types) {
		this.id = id;
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
	}

	public int getOnDemandRunningMachines() {
		return onDemandRunningMachines;
	}

	public void setOnDemandRunningMachines(int onDemandRunningMachines) {
		this.onDemandRunningMachines = onDemandRunningMachines;
	}

	public int getId() {
		return id;
	}

	public Map<MachineType, TypeProvider> getTypes() {
		return types;
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


	public MachineType[] getAvailableTypes() {
		Set<MachineType> set = types.keySet();
		return set.toArray(new MachineType[set.size()]);
	}

	public boolean canBuyMachine(boolean reserved, MachineType type) {
		if(!reserved){
			return types.containsKey(type) && onDemandRunningMachines < getOnDemandLimit();
		}
		return types.containsKey(type) && types.get(type).canBuy();
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
		MachineDescriptor descriptor = types.get(instanceType).buyMachine(isReserved);
		if(!isReserved && descriptor != null){
			this.onDemandRunningMachines++;
		}
		return descriptor;
	}

	/**
	 * TODO test removed false of an on demand machine.
	 * @param machineDescriptor
	 * @return
	 */
	public boolean shutdownMachine(MachineDescriptor machineDescriptor) {
		if(!types.containsKey(machineDescriptor.getType())){
			return false;
		}
		boolean removed = types.get(machineDescriptor.getType()).shutdownMachine(machineDescriptor);
		if(removed && !machineDescriptor.isReserved()){
			onDemandRunningMachines--;
		}
		return removed;
	}
	

	public void calculateCost(UtilityResultEntry entry, long currentTimeInMillis) {
		
		long [] transferences = new long[2];
				
		for (TypeProvider typeProvider : types.values()) {
			long [] typeTransferences = typeProvider.getTotalTransferences();
			transferences[0] += typeTransferences[0];
			transferences[1] += typeTransferences[1];
			typeProvider.calculateMachinesCost(entry, currentTimeInMillis, monitoringCost);
		}
		
		double inCost = CostCalculus.calcTransferenceCost(transferences[0], transferInLimits, transferInCosts, CostCalculus.GB_IN_BYTES);
		double outCost = CostCalculus.calcTransferenceCost(transferences[1], transferOutLimits, transferOutCosts, CostCalculus.GB_IN_BYTES);
		
		entry.addTransferenceToCost(id, transferences[0], inCost, transferences[1], outCost);
	}
	
	public void calculateUniqueCost(UtilityResult result) {

		for (TypeProvider typeProvider : types.values()) {
			double cost = typeProvider.calculateUniqueCost();
			result.addProviderUniqueCost(id, typeProvider.getType(), cost);
		}
	}
}
