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
 * IaaS {@link Machine} provider. Based on Amazon EC2 market model.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.1
 */
public class Provider implements Serializable{
	
	/**
	 * Version 1.1
	 */
	private static final long serialVersionUID = -746266289404954541L;
	private final int id;
	private final String name;
	private final int onDemandLimit;
	private final int reservationLimit;
	private final Map<MachineType, TypeProvider> types;
	private final double monitoringCost;
	private final long[] transferInLimitsInBytes;
	private final double[] transferInCostsPerByte;
	private final long[] transferOutLimitsInBytes;
	private final double[] transferOutCostsPerByte;
	
	private int onDemandRunningMachines;
	
	/**
	 * Default constructor.
	 * @param id
	 * @param name
	 * @param onDemandLimit
	 * @param reservationLimit
	 * @param monitoringCost
	 * @param transferInLimitsInBytes
	 * @param transferInCostsPerByte
	 * @param transferOutLimitsInBytes
	 * @param transferOutCostsPerByte
	 * @param types
	 */
	public Provider(int id, String name,
			int onDemandLimit, int reservationLimit,
			double monitoringCost,
			long[] transferInLimitsInBytes,
			double[] transferInCostsPerByte, long[] transferOutLimitsInBytes,
			double[] transferOutCostsPerByte, List<TypeProvider> types) {
		this.id = id;
		this.name = name;
		this.onDemandLimit = onDemandLimit;
		this.reservationLimit = reservationLimit;
		this.monitoringCost = monitoringCost;
		this.transferInLimitsInBytes = transferInLimitsInBytes;
		this.transferInCostsPerByte = transferInCostsPerByte;
		this.transferOutLimitsInBytes = transferOutLimitsInBytes;
		this.transferOutCostsPerByte = transferOutCostsPerByte;
		
		this.types = new HashMap<MachineType, TypeProvider>();
		for (TypeProvider machineType : types) {
			this.types.put(machineType.getType(), machineType);
		}
		this.onDemandRunningMachines = 0;
	}

	/**
	 * Avoid using it. It breaks type encapsulation.
	 * 
	 * @return
	 */
	@Deprecated()
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
		return transferInLimitsInBytes;
	}

	/**
	 * @return the transferInCosts
	 */
	public double[] getTransferInCosts() {
		return transferInCostsPerByte;
	}

	/**
	 * @return the transferOutLimits
	 */
	public long[] getTransferOutLimits() {
		return transferOutLimitsInBytes;
	}

	/**
	 * @return the transferOutCosts
	 */
	public double[] getTransferOutCosts() {
		return transferOutCostsPerByte;
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
	

	public ProviderEntry calculateCost(long currentTimeInMillis) {
		
		long inputTransferences = 0;
		long outputTransferences = 0;
				
		for (TypeProvider typeProvider : types.values()) {
			long [] typeTransferences = typeProvider.getTotalTransferences();
			inputTransferences += typeTransferences[0];
			outputTransferences += typeTransferences[1];
		}
		
		double inCost = CostCalculus.calcTransferenceCost(inputTransferences, transferInLimitsInBytes, transferInCostsPerByte);
		double outCost = CostCalculus.calcTransferenceCost(outputTransferences, transferOutLimitsInBytes, transferOutCostsPerByte);
		
		ProviderEntry providerEntry = new ProviderEntry(name, inputTransferences, inCost, outputTransferences, outCost);
		
		for (TypeProvider type : types.values()) {
			providerEntry.account(type.calculateMachinesCost(currentTimeInMillis, monitoringCost));
		}
		
		return providerEntry;
	}
	
	public double calculateUniqueCost() {

		double cost = 0;
		for (TypeProvider typeProvider : types.values()) {
			cost += typeProvider.calculateUniqueCost();
		}
		return cost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		assert (obj != null) && (getClass() == obj.getClass()): "Can't compare with another class object.";
		
		if (this == obj)
			return true;
		Provider other = (Provider) obj;
		return (id == other.id);
	}
	
}
