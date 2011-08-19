package commons.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commons.cloud.UtilityResult.UtilityResultEntry;
import commons.sim.components.MachineDescriptor;


/**
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProvider {
	
	private static final long HOUR_IN_MILLIS = 3600000;
	
	private final MachineType type;
	private final double onDemandCpuCost;
	private final double reservedCpuCost;
	private final double reservationOneYearFee;
	private final double reservationThreeYearsFee;
	private final long reservation;

	private List<MachineDescriptor> reservedRunningMachines;
	private List<MachineDescriptor> reservedFinishedMachines;
	
	private List<MachineDescriptor> onDemandRunningMachines;
	private List<MachineDescriptor> onDemandFinishedMachines;

	
	/**
	 * Default constructor.
	 * @param value
	 * @param onDemandCpuCost
	 * @param reservedCpuCost
	 * @param reservationOneYearFee
	 * @param reservationThreeYearsFee
	 * @param reservation TODO
	 */
	public TypeProvider(MachineType value, double onDemandCpuCost,
			double reservedCpuCost, double reservationOneYearFee,
			double reservationThreeYearsFee, long reservation) {
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

	public MachineDescriptor buyMachine(boolean isReserved) {
		if(isReserved){
			if(canBuy()){
				MachineDescriptor descriptor = new MachineDescriptor(IDGenerator.GENERATOR.next(), isReserved, getType());
				reservedRunningMachines.add(descriptor);
				return descriptor;
			}
		}else{
			MachineDescriptor descriptor = new MachineDescriptor(IDGenerator.GENERATOR.next(), isReserved, getType());
			onDemandRunningMachines.add(descriptor);
			return descriptor;
		}
		return null;
	}

	public boolean canBuy() {
		return reservedRunningMachines.size() < reservation;
	}

	public void calculateMachinesCost(UtilityResultEntry entry, long currentTimeInMillis, double monitoringCostPerHour) {
		long onDemandUpTimeInFullHours = 0;
		long reservedUpTimeInFullHours = 0;
		
		for (MachineDescriptor descriptor : onDemandFinishedMachines) {
			onDemandUpTimeInFullHours += (long) Math.ceil(1.0*descriptor.getUpTimeInMillis()/HOUR_IN_MILLIS);
		}
		for (MachineDescriptor descriptor : reservedFinishedMachines) {
			reservedUpTimeInFullHours += (long) Math.ceil(1.0*descriptor.getUpTimeInMillis()/HOUR_IN_MILLIS);
		}
		for (MachineDescriptor descriptor : onDemandRunningMachines) {
			onDemandUpTimeInFullHours += (long) Math.ceil(1.0*descriptor.getUpTimeInMillis()/HOUR_IN_MILLIS);
			descriptor.reset(currentTimeInMillis);
		}
		for (MachineDescriptor descriptor : reservedRunningMachines) {
			reservedUpTimeInFullHours += (long) Math.ceil(1.0*descriptor.getUpTimeInMillis()/HOUR_IN_MILLIS);
			descriptor.reset(currentTimeInMillis);
		}
		
		double onDemandCost = onDemandUpTimeInFullHours * onDemandCpuCost;
		double reservedCost = reservedUpTimeInFullHours * reservedCpuCost;
		double monitoringCost = (onDemandUpTimeInFullHours + reservedUpTimeInFullHours) * monitoringCostPerHour;
		
		entry.addUsageToCost(type, onDemandUpTimeInFullHours, onDemandCost, reservedUpTimeInFullHours, reservedCost, monitoringCost);
		
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
	
}
