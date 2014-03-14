package saasim.cloud.utility;

import java.io.Serializable;

import saasim.cloud.Provider;
import saasim.cloud.TypeProvider;


/**
 * Abstraction used to represent a entry of one {@link Provider}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class ProviderEntry implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3831288060251941356L;
	private static final String STRING = "\t";
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
	
	/**
	 * Default constructor.
	 * @param name the name of specific {@link Provider}.
	 * @param inTransference a long represent the input transference.
	 * @param inCost a double represent the input cost.
	 * @param outTransference a long represent the output transference.
	 * @param outCost a double represent the output cost.
	 */
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

	/**
	 * Update the values of this {@link ProviderEntry} based on the values of the {@link TypeProviderEntry} in parameter,
	 * and increment the number of types.
	 * @param typeEntry the entry represents a {@link TypeProvider}, see {@link TypeProviderEntry}.
	 */
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

	/**
	 * Gets the descriptor's values of this {@link ProviderEntry}. 
	 * @return A String containing the descriptor's values. 
	 */
	public String getDescriptor() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numberOfTypes; i++) {
			sb.append("type\tondCPU\tondCost\tresCPU\tresCost\t");
		}
		return sb.toString();
	
	}
	
	/**
	 * Gets the cost of {@link Provider} represents for this {@link ProviderEntry}.
	 * @return The calculated cost, represented for the input cost added to output cost.
	 */
	public double getCost() {
		return cost;
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
}