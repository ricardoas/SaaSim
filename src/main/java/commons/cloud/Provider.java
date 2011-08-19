package commons.cloud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.UtilityResult.UtilityResultEntry;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;

/**
 * IaaS {@link Machine} provider.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Provider {
	
	private static final long GB_IN_BYTES = 1024 * 1024 * 1024;
	
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
	

	public void calculateCost(UtilityResultEntry entry, long currentTimeInMillis) {
		
		entry.addProvider(getName());
		
		
		long [] transferences = new long[2];
				
		for (TypeProvider typeProvider : types.values()) {
			long [] typeTransferences = typeProvider.getTotalTransferences();
			transferences[0] += typeTransferences[0];
			transferences[1] += typeTransferences[1];
			typeProvider.calculateMachinesCost(entry, currentTimeInMillis, monitoringCost);
		}
		
		double inCost = calcTransferenceCost(transferences[0], transferInLimits, transferInCosts);
		double outCost = calcTransferenceCost(transferences[1], transferOutLimits, transferOutCosts);
		
		entry.addTransferenceToCost(transferences[0], inCost, transferences[1], outCost);
	}
	
	/**
	 * TODO Code me!
	 * @param totalTransfered
	 * @param limits
	 * @param costs
	 * @return
	 */
	private static double calcTransferenceCost(long totalTransfered,
			long[] limits, double[] costs) {
		double transferenceLeft = (1.0*totalTransfered)/GB_IN_BYTES;
		int currentIndex = 0;
		double total = 0;
		while(transferenceLeft != 0 && currentIndex != limits.length){
			if(transferenceLeft <= limits[currentIndex]){
				total += transferenceLeft * costs[currentIndex];
				transferenceLeft = 0;
			}else{
				total += limits[currentIndex] * costs[currentIndex];
				transferenceLeft -= limits[currentIndex]; 
			}
			currentIndex++;
		}
		
		if(transferenceLeft != 0){
			total += limits[currentIndex] * costs[currentIndex];
			transferenceLeft = 0; 
		}
		return total;
	}

	public double calculateUniqueCost() {
		double result = 0;//this.totalNumberOfReservedMachinesUsed * this.reservationOneYearFee;
//		this.totalNumberOfReservedMachinesUsed = 0;
		return result;
	}
}
