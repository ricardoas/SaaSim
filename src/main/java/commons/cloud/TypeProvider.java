package commons.cloud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commons.sim.components.MachineDescriptor;
import commons.util.TimeUnit;


/**
 * Abstraction for encapsulate a specific {@link MachineType} market of a {@link Provider}.
 * An IaaS provider renting machines of three different types works like three providers renting
 * each one a single type.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class TypeProvider implements Serializable{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = -2384651815453738053L;

	private final int providerID;
	private final MachineType type;
	private final double onDemandCpuCost;
	private final double reservedCpuCost;
	private final double reservationOneYearFee;
	private final double reservationThreeYearsFee;
	private final long reservation;

	private final List<MachineDescriptor> reservedRunningMachines;
	private final List<MachineDescriptor> reservedFinishedMachines;
	
	private final List<MachineDescriptor> onDemandRunningMachines;
	private final List<MachineDescriptor> onDemandFinishedMachines;
	
	/**
	 * Default constructor.
	 * @param providerID TODO an integer to represent a provider
	 * @param value a value represents the type of provider, see {@link MachineType} 
	 * @param onDemandCpuCost the cost of on demand utilization
	 * @param reservedCpuCost the cost of reserved utilization
	 * @param reservationOneYearFee the value of fee reservation for one year
	 * @param reservationThreeYearsFee the value of fee reservation for three year
	 * @param reservation TODO the reservation of this {@link TypeProvider}
	 */
	public TypeProvider(int providerID, MachineType value,
			double onDemandCpuCost, double reservedCpuCost,
			double reservationOneYearFee, double reservationThreeYearsFee, long reservation) {
		this.providerID = providerID;
		this.type = value;
		this.onDemandCpuCost = onDemandCpuCost;
		this.reservedCpuCost = reservedCpuCost;
		this.reservationOneYearFee = reservationOneYearFee;
		this.reservationThreeYearsFee = reservationThreeYearsFee;
		this.reservation = reservation;
		
		this.reservedRunningMachines = new ArrayList<MachineDescriptor>();
		this.reservedFinishedMachines = new ArrayList<MachineDescriptor>();

		this.onDemandRunningMachines = new ArrayList<MachineDescriptor>();
		this.onDemandFinishedMachines = new ArrayList<MachineDescriptor>();
	}

	/**
	 * Gets the type of this {@link TypeProvider}.
	 * @return the type see {@link MachineType}
	 */
	public MachineType getType() {
		return type;
	}

	/**
	 * Gets the cpu cost of on demand utilization.
	 * @return the onDemandCpuCost the cpu cost of on demand utilization
	 */
	public double getOnDemandCpuCost() {
		return onDemandCpuCost;
	}

	/**
	 * Gets the cpu cost of on reserved utilization.
	 * @return the reservedCpuCost the cpu cost of reserved utilization 
	 */
	public double getReservedCpuCost() {
		return reservedCpuCost;
	}

	/**
	 * Gets the fee of reservation for one year.
	 * @return the reservationOneYearFee the fee of reservation for one year
	 */
	public double getReservationOneYearFee() {
		return reservationOneYearFee;
	}

	/**
	 * Gets the fee of reservation for three year.
	 * @return the reservationThreeYearsFee the fee of reservation for three year.
	 */
	public double getReservationThreeYearsFee() {
		return reservationThreeYearsFee;
	}

	/**
	 * Gets the reservation of this {@link TypeProvider}.
	 * @return the reservation 
	 */
	public long getReservation() {
		return reservation;
	}

	/**
	 * Reports that the machine represented by such {@link MachineDescriptor} has been turned off.
	 * @param machineDescriptor The machine that has been turned off.
	 * @return <code>true</code> if this provider is responsible for this machine and the report 
	 * has been successfully done, and <code>false</code> otherwise.
	 */
	public boolean shutdownMachine(MachineDescriptor machineDescriptor) {
		if(machineDescriptor.isReserved()){
			if( reservedRunningMachines.remove(machineDescriptor) ){
				return reservedFinishedMachines.add(machineDescriptor);
			}
		}else{
			if( onDemandRunningMachines.remove(machineDescriptor) ){
				return onDemandFinishedMachines.add(machineDescriptor);
			}
		}
		return false;
	}

	/**
	 * Buy a new machine with this {@link TypeProvider}.
	 * @param isReserved <code>true</code> if the new machine is a previously reserved one, <code>false</code> otherwise.
	 * @return A new {@link MachineDescriptor} if the machine was created with sucess, or <code>null</code> otherwise
	 */
	public MachineDescriptor buyMachine(boolean isReserved) {
		if(isReserved){
			if(canBuy()){
				MachineDescriptor descriptor = new MachineDescriptor(IDGenerator.GENERATOR.next(), isReserved, getType(), providerID);
				reservedRunningMachines.add(descriptor);
				return descriptor;
			}
		}else{
			MachineDescriptor descriptor = new MachineDescriptor(IDGenerator.GENERATOR.next(), isReserved, getType(), providerID);
			onDemandRunningMachines.add(descriptor);
			return descriptor;
		}
		return null;
	}

	/**
	 * Returns a value about the possibility of buy a new machine.
	 * @return <code>true</code> if there are RESERVED machines available to be bought, 
	 * and <code>false</code> otherwise
	 */
	public boolean canBuy() {
		return reservedRunningMachines.size() < reservation;
	}
	
	/**
	 * Calculate the cost of machines in this {@link TypeProvider}.
	 * @param currentTimeInMillis a long represents the current time in millis
	 * @param monitoringCostPerHour a double represents the monitoring cost per hour
	 * @return A {@link TypeProviderEntry} encapsulating the calculated machines cost.
	 */
	public TypeProviderEntry calculateMachinesCost(long currentTimeInMillis, double monitoringCostPerHour) {
		long onDemandUpTimeInFullHours = 0;
		long reservedUpTimeInFullHours = 0;
		
		for (MachineDescriptor descriptor : onDemandFinishedMachines) {
			onDemandUpTimeInFullHours += (long) Math.ceil(Math.max(1.0,descriptor.getUpTimeInMillis())/TimeUnit.HOUR.getMillis());
		}
		for (MachineDescriptor descriptor : reservedFinishedMachines) {
			reservedUpTimeInFullHours += (long) Math.ceil(Math.max(1.0,descriptor.getUpTimeInMillis())/TimeUnit.HOUR.getMillis());
		}
		for (MachineDescriptor descriptor : onDemandRunningMachines) {
			onDemandUpTimeInFullHours += (long) Math.ceil(1.0*(currentTimeInMillis - descriptor.getStartTimeInMillis())/TimeUnit.HOUR.getMillis());
			descriptor.reset(currentTimeInMillis);
		}
		for (MachineDescriptor descriptor : reservedRunningMachines) {
			reservedUpTimeInFullHours += (long) Math.ceil(1.0*(currentTimeInMillis - descriptor.getStartTimeInMillis())/TimeUnit.HOUR.getMillis());
			descriptor.reset(currentTimeInMillis);
		}
		
		double onDemandCost = onDemandUpTimeInFullHours * onDemandCpuCost;
		double reservedCost = reservedUpTimeInFullHours * reservedCpuCost;
		double monitoringCost = (onDemandUpTimeInFullHours + reservedUpTimeInFullHours) * monitoringCostPerHour;
		
		TypeProviderEntry typeEntry = new TypeProviderEntry(type, onDemandUpTimeInFullHours, onDemandCost, reservedUpTimeInFullHours, reservedCost, monitoringCost);
		onDemandFinishedMachines.clear();
		reservedFinishedMachines.clear();
		return typeEntry;
	}
	
	/**
	 * Calculate the cost based on the value of reservation and the value of the fee of reservation 
	 * for one year.
	 * @return The cost.
	 */
	public double calculateUniqueCost() {
		return reservation * reservationOneYearFee;
	}

	/**
	 * Gets the total transferences in this {@link TypeProvider} based on reserved and on demand, finished and running machines.
	 * @return An array containing two values: total input transference and total output transference, in this order.
	 */
	public long[] getTotalTransferences() {
		long [] transferences = new long[2];
		Arrays.fill(transferences, 0);
		for (MachineDescriptor descriptor : onDemandFinishedMachines) {
			transferences[0] += descriptor.getInTransference();
			transferences[1] += descriptor.getOutTransference();
		}
		for (MachineDescriptor descriptor : reservedFinishedMachines) {
			transferences[0] += descriptor.getInTransference();
			transferences[1] += descriptor.getOutTransference();
		}
		for (MachineDescriptor descriptor : onDemandRunningMachines) {
			transferences[0] += descriptor.getInTransference();
			transferences[1] += descriptor.getOutTransference();
		}
		for (MachineDescriptor descriptor : reservedRunningMachines) {
			transferences[0] += descriptor.getInTransference();
			transferences[1] += descriptor.getOutTransference();
		}
		return transferences;
	}
	
}
