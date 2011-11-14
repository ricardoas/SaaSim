package commons.cloud;

import java.io.Serializable;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class ProviderEntry implements Serializable{
	
	private static final String STRING = "\t";
	/**
	 * 
	 */
	private static final long serialVersionUID = -3831288060251941356L;
	private final String name;
	private double cost;
	private double inCost;
	private double outCost;
	private double onDemandCost;
	private double reservedCost;
	
	private long inTransference;
	private long outTransference;
	private long onDemandCPUHours;
	private long reservedCPUHours;
	
	private double monitoringCost;
	
	private StringBuilder builder;
	
	private int numberOfTypes;
	
	public ProviderEntry(String name, long inTransference, double inCost,
			long outTransference, double outCost) {
		this.name = name;
		this.inTransference = inTransference;
		this.inCost = inCost;
		this.outTransference = outTransference;
		this.outCost = outCost;
		
		this.cost = inCost + outCost;
		this.onDemandCost = 0;
		this.reservedCost = 0;
		this.onDemandCPUHours = 0;
		this.reservedCPUHours = 0;
		this.monitoringCost = 0;
		this.builder = new StringBuilder();
		this.numberOfTypes = 0;
	}

	public void account(TypeProviderEntry typeEntry) {
		this.onDemandCPUHours += typeEntry.onDemandCPUHours;
		this.onDemandCost += typeEntry.onDemandCost;
		this.reservedCPUHours += typeEntry.reservedCPUHours;
		this.reservedCost += typeEntry.reservedCost;
		this.monitoringCost += typeEntry.monitoringCost;
		this.cost += typeEntry.cost;
		this.builder.append(typeEntry);
		this.builder.append(STRING);
		this.numberOfTypes++;
	}

	public String getDescriptor() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numberOfTypes; i++) {
			sb.append("type\tondCPU\tondCost\tresCPU\tresCost\t");
		}
		return sb.toString();
	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name 
				+ STRING + cost
				+ STRING + onDemandCPUHours
				+ STRING + onDemandCost 
				+ STRING + reservedCPUHours 
				+ STRING + reservedCost 
				+ STRING + inTransference
				+ STRING + inCost 
				+ STRING + outTransference
				+ STRING + outCost
				+ STRING + monitoringCost
				+ STRING + builder.toString();
	}

	public double getCost() {
		return cost;
	}
}