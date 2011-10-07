package commons.cloud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commons.sim.components.MachineDescriptor;


/**
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProvider implements Serializable{
	
	private static final long HOUR_IN_MILLIS = 3600000;
	
	private int providerID;
	private MachineType type;
	private double onDemandCpuCost;
	private double reservedCpuCost;
	private double reservationOneYearFee;
	private double reservationThreeYearsFee;
	private long reservation;

	private List<MachineDescriptor> reservedRunningMachines;
	private List<MachineDescriptor> reservedFinishedMachines;
	
	private List<MachineDescriptor> onDemandRunningMachines;
	private List<MachineDescriptor> onDemandFinishedMachines;
	
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

	public TypeProvider(int providerID, MachineType type,
			double onDemandCpuCost, double reservedCpuCost,
			double reservationOneYearFee, double reservationThreeYearsFee,
			long reservation, List<MachineDescriptor> reservedRunningMachines,
			List<MachineDescriptor> reservedFinishedMachines,
			List<MachineDescriptor> onDemandRunningMachines,
			List<MachineDescriptor> onDemandFinishedMachines) {
		this.providerID = providerID;
		this.type = type;
		this.onDemandCpuCost = onDemandCpuCost;
		this.reservedCpuCost = reservedCpuCost;
		this.reservationOneYearFee = reservationOneYearFee;
		this.reservationThreeYearsFee = reservationThreeYearsFee;
		this.reservation = reservation;
		this.reservedRunningMachines = reservedRunningMachines;
		this.reservedFinishedMachines = reservedFinishedMachines;
		this.onDemandRunningMachines = onDemandRunningMachines;
		this.onDemandFinishedMachines = onDemandFinishedMachines;
	}

	public void setProviderID(int providerID) {
		this.providerID = providerID;
	}

	public void setType(MachineType type) {
		this.type = type;
	}

	public void setOnDemandCpuCost(double onDemandCpuCost) {
		this.onDemandCpuCost = onDemandCpuCost;
	}

	public void setReservedCpuCost(double reservedCpuCost) {
		this.reservedCpuCost = reservedCpuCost;
	}

	public void setReservationOneYearFee(double reservationOneYearFee) {
		this.reservationOneYearFee = reservationOneYearFee;
	}

	public void setReservationThreeYearsFee(double reservationThreeYearsFee) {
		this.reservationThreeYearsFee = reservationThreeYearsFee;
	}

	public void setReservation(long reservation) {
		this.reservation = reservation;
	}

	public List<MachineDescriptor> getReservedRunningMachines() {
		return reservedRunningMachines;
	}

	public void setReservedRunningMachines(
			List<MachineDescriptor> reservedRunningMachines) {
		this.reservedRunningMachines = reservedRunningMachines;
	}

	public List<MachineDescriptor> getReservedFinishedMachines() {
		return reservedFinishedMachines;
	}

	public void setReservedFinishedMachines(
			List<MachineDescriptor> reservedFinishedMachines) {
		this.reservedFinishedMachines = reservedFinishedMachines;
	}

	public List<MachineDescriptor> getOnDemandRunningMachines() {
		return onDemandRunningMachines;
	}

	public void setOnDemandRunningMachines(
			List<MachineDescriptor> onDemandRunningMachines) {
		this.onDemandRunningMachines = onDemandRunningMachines;
	}

	public List<MachineDescriptor> getOnDemandFinishedMachines() {
		return onDemandFinishedMachines;
	}

	public void setOnDemandFinishedMachines(
			List<MachineDescriptor> onDemandFinishedMachines) {
		this.onDemandFinishedMachines = onDemandFinishedMachines;
	}

	public int getProviderID() {
		return providerID;
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
			onDemandUpTimeInFullHours += (long) Math.ceil(1.0*(currentTimeInMillis - descriptor.getStartTimeInMillis())/HOUR_IN_MILLIS);
			descriptor.reset(currentTimeInMillis);
		}
		for (MachineDescriptor descriptor : reservedRunningMachines) {
			reservedUpTimeInFullHours += (long) Math.ceil(1.0*(currentTimeInMillis - descriptor.getStartTimeInMillis())/HOUR_IN_MILLIS);
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
