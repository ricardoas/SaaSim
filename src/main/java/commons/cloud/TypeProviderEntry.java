package commons.cloud;

import java.io.Serializable;

/**
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
	 * @param type
	 * @param onDemandCPUHours
	 * @param onDemandCost
	 * @param reservedCPUHours
	 * @param reservedCost
	 * @param monitoringCost
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