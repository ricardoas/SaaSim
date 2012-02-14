package saasim.cloud;

import java.io.Serializable;

/**
 * Abstraction used to represent a entry of one {@link TypeProvider}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProviderEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5699832152421322269L;
	final MachineType type;
	long onDemandCPUHours;
	double onDemandCost;
	long reservedCPUHours;
	double reservedCost;
	double monitoringCost;
	double cost;

	/**
	 * Default constructor.
	 * @param type a {@link MachineType} value of specific {@link TypeProvider}.
	 * @param onDemandCPUHours the time in hours spent by the cpu in on demand utilization
	 * @param onDemandCost the cost for on demand utilization
	 * @param reservedCPUHours the time in hours spent by the cpu in reserved utilization
	 * @param reservedCost the cost for reserved utilization
	 * @param monitoringCost a double representing the monitoring cost
	 */
	public TypeProviderEntry(MachineType type, long onDemandCPUHours,
			double onDemandCost, long reservedCPUHours, double reservedCost, double monitoringCost) {
		this.type = type;
		this.onDemandCPUHours = onDemandCPUHours;
		this.onDemandCost = onDemandCost;
		this.reservedCPUHours = reservedCPUHours;
		this.reservedCost = reservedCost;
		this.monitoringCost = monitoringCost;
		this.cost = onDemandCost + reservedCost + monitoringCost;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return type.toString() 
				+ UtilityResultEntry.STRING + onDemandCPUHours 
				+ UtilityResultEntry.STRING + onDemandCost
				+ UtilityResultEntry.STRING + reservedCPUHours
				+ UtilityResultEntry.STRING + reservedCost;
	}
}