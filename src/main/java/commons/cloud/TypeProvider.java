package commons.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.print.attribute.standard.Finishings;

import commons.cloud.UtilityResult.UtilityResultEntry;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;


/**
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProvider {
	
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
		if(canBuy()){
			MachineDescriptor descriptor = new MachineDescriptor(IDGenerator.GENERATOR.next(), true, getType());
			reservedRunningMachines.add(descriptor);
			return descriptor;
		}
		return null;
	}

	public boolean canBuy() {
		return reservedRunningMachines.size() < reservation;
	}

	public double calculateFinishedMachinesCost(UtilityResultEntry entry) {
		long total = 0;
		for (MachineDescriptor descriptor : onDemandFinishedMachines) {
			total += descriptor.getInTransference();
		}
		for (MachineDescriptor descriptor : reservedFinishedMachines) {
			total += descriptor.getInTransference();
		}
		return total;
	}

	public double calculateRunningMachinesCost(UtilityResultEntry entry) {
		return 0;
	}
	
	private double calculateCost(MachineDescriptor descriptor){
		double executionTime = descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis();
		executionTime = Math.ceil(1.0 * executionTime / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		
		if(executionTime < 0){
			throw new RuntimeException(this.getClass()+": Invalid machine"+ descriptor.getMachineID() +" runtime for cost: "+executionTime);
		}
		
		if(descriptor.isReserved()){
			return executionTime * types.get(descriptor.getType()).getReservedCpuCost() + executionTime * getMonitoringCost();
		}else{
			return executionTime * types.get(descriptor.getType()).getOnDemandCpuCost() + executionTime * getMonitoringCost();
		}
	}
	
	public long[] getTotalTransferences() {
		long [] transferences = new long[4];
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
			transferences[2] += descriptor.getInTransference();
			transferences[3] += descriptor.getOutTransference();
		}
		for (MachineDescriptor descriptor : reservedRunningMachines) {
			transferences[2] += descriptor.getInTransference();
			transferences[3] += descriptor.getOutTransference();
		}
		return transferences;
	}
	
}
