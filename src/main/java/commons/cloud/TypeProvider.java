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
	 * @param providerID TODO
	 * @param value
	 * @param onDemandCpuCost
	 * @param reservedCpuCost
	 * @param reservationOneYearFee
	 * @param reservationThreeYearsFee
	 * @param reservation TODO
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

	public MachineType getType() {
		return type;
	}

	public double getOnDemandCpuCost() {
		return onDemandCpuCost;
	}

	public double getReservedCpuCost() {
		return reservedCpuCost;
	}

	public double getReservationOneYearFee() {
		return reservationOneYearFee;
	}

	public double getReservationThreeYearsFee() {
		return reservationThreeYearsFee;
	}

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
	 * @param isReserved
	 * @return
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
	 * @return <code>true</code> if there are RESERVED machines available to be bought, 
	 * and <code>false</code> otherwise..
	 */
	public boolean canBuy() {
		return reservedRunningMachines.size() < reservation;
	}

	public void calculateMachinesCost(UtilityResultEntry entry, long currentTimeInMillis, double monitoringCostPerHour) {
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
		
		entry.addUsageToCost(providerID, type, onDemandUpTimeInFullHours, onDemandCost, reservedUpTimeInFullHours, reservedCost, monitoringCost);
		
		onDemandFinishedMachines.clear();
		reservedFinishedMachines.clear();
	}

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

	/**
	 * @return
	 */
	public double calculateUniqueCost() {
		return reservation * reservationOneYearFee;
	}
	
}
