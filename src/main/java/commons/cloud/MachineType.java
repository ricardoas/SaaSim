package commons.cloud;

import java.util.ArrayList;
import java.util.List;

import commons.sim.components.MachineDescriptor;


/**
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MachineType {
	
	private final MachineTypeValue type;
	private final double onDemandCpuCost;
	private final double reservedCpuCost;
	private final double reservationOneYearFee;
	private final double reservationThreeYearsFee;
	private final long reservation;

	private List<MachineDescriptor> runningMachines;
	private List<MachineDescriptor> finishedMachines;
	
	/**
	 * Default constructor.
	 * @param value
	 * @param onDemandCpuCost
	 * @param reservedCpuCost
	 * @param reservationOneYearFee
	 * @param reservationThreeYearsFee
	 * @param reservation TODO
	 */
	public MachineType(MachineTypeValue value, double onDemandCpuCost,
			double reservedCpuCost, double reservationOneYearFee,
			double reservationThreeYearsFee, long reservation) {
		this.type = value;
		this.onDemandCpuCost = onDemandCpuCost;
		this.reservedCpuCost = reservedCpuCost;
		this.reservationOneYearFee = reservationOneYearFee;
		this.reservationThreeYearsFee = reservationThreeYearsFee;
		this.reservation = reservation;
		
		this.runningMachines = new ArrayList<MachineDescriptor>();
		this.finishedMachines = new ArrayList<MachineDescriptor>();
	}

	public MachineTypeValue getType() {
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
		if( runningMachines.remove(machineDescriptor) ){
			return finishedMachines.add(machineDescriptor);
		}
		return false;
	}

	public MachineDescriptor buyMachine() {
		if(canBuy()){
			MachineDescriptor descriptor = new MachineDescriptor(IDGenerator.GENERATOR.next(), true, getType());
			runningMachines.add(descriptor);
			return descriptor;
		}
		return null;
	}

	public boolean canBuy() {
		return runningMachines.size() < reservation;
	}
}
