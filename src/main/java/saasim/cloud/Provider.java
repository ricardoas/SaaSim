package saasim.cloud;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.util.CostCalculus;


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
	 * @param id an integer to represent each provider
	 * @param name a name of provider
	 * @param onDemandLimit a limit of on demand utilization 
	 * @param reservationLimit a limit of reservation utilization 
	 * @param monitoringCost a double representing the monitoring cost
	 * @param transferInLimitsInBytes an array of long containing values of transfer in limits in bytes
	 * @param transferInCostsPerByte an array of double containing values of transfer in cost per bytes
	 * @param transferOutLimitsInBytes an array of long containing values of transfer out limits in bytes
	 * @param transferOutCosof transfer intsPerByte an array of double containing values of transfer out cost in bytes
	 * @param types a list containing {@link TypeProvider} values  
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
	 * @return
	 */
	@Deprecated()
	public Map<MachineType, TypeProvider> getTypes() {
		return types;
	}

	/**
	 * Gets the name of this {@link Provider}.
	 * @return The name of provider
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the cpu cost of on demand utilization.
	 * @param type see {@link MachineType}.
	 * @return The cpu cost of on demand utilization 
	 */
	public double getOnDemandCpuCost(MachineType type) {
		return types.get(type).getOnDemandCpuCost();
	}

	/**
	 * Gets the limit of on demand utilization.
	 * @return The limit of on demand utilization 
	 */
	public int getOnDemandLimit() {
		return onDemandLimit;
	}

	/**
	 * Gets the limit of reservation utilization.
	 * @return the reservationLimit the limit of reservation utilization
	 */
	public int getReservationLimit() {
		return reservationLimit;
	}

	/**
	 * Gets the cpu cost of reserved utilization.
	 * @param type see {@link MachineType}.
	 * @return The cpu cost of reserved utilization
	 */
	public double getReservedCpuCost(MachineType type) {
		return types.get(type).getReservedCpuCost();
	}

	/**
	 * Gets the fee of reservation for one year.
	 * @param type see {@link MachineType}.
	 * @return The fee of reservation for one year
	 */
	public double getReservationOneYearFee(MachineType type) {
		return types.get(type).getReservationOneYearFee();
	}

	/**
	 * Gets the fee of reservation for three year.
	 * @param type see {@link MachineType}.
	 * @return The fee of reservation for three year
	 */
	public double getReservationThreeYearsFee(MachineType type) {
		return types.get(type).getReservationThreeYearsFee();
	}

	/**
	 * Gets the cost of monitoring.
	 * @return The cost of monitoring
	 */
	public double getMonitoringCost() {
		return monitoringCost;
	}

	/**
	 * Gets the limits of transfer in bytes.
	 * @return An array containing the limits of transfer 
	 */
	public long[] getTransferInLimits() {
		return transferInLimitsInBytes;
	}

	/**
	 * Gets the costs of transfer per byte.
	 * @return Array containing the costs of transfer
	 */
	public double[] getTransferInCosts() {
		return transferInCostsPerByte;
	}

	/**
	 * Gets the out limits of transfer in bytes. 
	 * @return An array containing the out limits of transfer
	 */
	public long[] getTransferOutLimits() {
		return transferOutLimitsInBytes;
	}

	/**
	 * Gets the out costs of transfer per byte.
	 * @return An array containing the out costs of transfer
	 */
	public double[] getTransferOutCosts() {
		return transferOutCostsPerByte;
	}

	/**
	 * Gets the available types of machine in this {@link Provider}. 
	 * @return An array containing the available types of machine in this {@link Provider}, see {@link MachineType}.
	 */
	public MachineType[] getAvailableTypes() {
		Set<MachineType> set = types.keySet();
		return set.toArray(new MachineType[set.size()]);
	}
	
	/**
	 * Determines if this {@link Provider} can buy a new machine.
	 * @param reserved <code>true</code> if the new machine is a previously reserved one, <code>false</code> otherwise.
	 * @param type the type of new machine, see {@link MachineType}.
	 * @return <code>true</code> if can buy, <code>false</code> otherwise.
	 */
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
	 * Shut a {@link MachineDescriptor} removing it the list of running machines 
	 * @param machineDescriptor The machine that has been turned off, see {@link MachineDescriptor}. 
	 * @return <code>true</code> if machine was removed with sucess, or <code>false</code> if the type of machine don't
	 * 		   exists in this {@link Provider} or the machine wasn't removed with sucess
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
	
	/**
	 * Calculate cost of this {@link Provider} in current time.
	 * @param currentTimeInMillis the current time in millis
	 * @return A {@link ProviderEntry} encapsulating the calculated cost.
	 */
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
	
	/**
	 * Calculate the unique cost of this {@link Provider} based on its types, see {@link TypeProvider}.
	 * @return A double represents the unique cost of this {@link Provider}.
	 */
	public double calculateUniqueCost() {
		double cost = 0;
		for (TypeProvider typeProvider : types.values()) {
			cost += typeProvider.calculateUniqueCost();
		}
		return cost;
	}
	
	/**
	 * Compare two {@link Provider}.
	 * <code>true</code> if them identifiers are equals, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		assert (obj != null) && (getClass() == obj.getClass()): "Can't compare with another class object.";
		
		if (this == obj)
			return true;
		Provider other = (Provider) obj;
		return (id == other.id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

}
