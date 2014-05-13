package saasim.ext.cloud.aws;

import saasim.core.cloud.InstanceType;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AWSInstanceType implements InstanceType{

	private String typeName;
	private int processor;
	private double ecu;
	private double memory;
	private double storage;
	private double lightUpfront;
	private double lightHourly;
	private double mediumUpfront;
	private double mediumHourly;
	private double heavyUpfront;
	private double heavyHourly;
	private double hourly;

	public AWSInstanceType(String typeName, String processor, String ecu, String memory, String storage, String hourly, String 
				lightUpfront, String lightHourly, String mediumUpfront, String mediumHourly, String heavyUpfront, String heavyHourly) {
					this.typeName = typeName;
					this.processor = Integer.valueOf(processor);
					this.ecu = Double.valueOf(ecu);
					this.memory = Double.valueOf(memory);
					this.storage = Double.valueOf(storage);
					this.hourly = Double.valueOf(hourly);
					this.lightUpfront = Double.valueOf(lightUpfront);
					this.lightHourly = Double.valueOf(lightHourly);
					this.mediumUpfront = Double.valueOf(mediumUpfront);
					this.mediumHourly = Double.valueOf(mediumHourly);
					this.heavyUpfront = Double.valueOf(heavyUpfront);
					this.heavyHourly = Double.valueOf(heavyHourly);
	}

	@Override
	public int getNumberOFCPU() {
		return processor;
	}

	public double getHourly() {
		return hourly;
	}

	@Override
	public double getRelativePower() {
		return 1;
	}

	public String getTypeNames() {
		return typeName;
	}

	public double getEcus() {
		return ecu;
	}

	public double getMemories() {
		return memory;
	}

	public double getStorages() {
		return storage;
	}

	public double getLightUpfront() {
		return lightUpfront;
	}

	public double getLightHourly() {
		return lightHourly;
	}

	public double getMediumUpfront() {
		return mediumUpfront;
	}

	public double getMediumHourly() {
		return mediumHourly;
	}

	public double getHeavyUpfront() {
		return heavyUpfront;
	}

	public double getHeavyHourly() {
		return heavyHourly;
	}

	@Override
	public int hashCode() {
		return typeName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return typeName.equals(((AWSInstanceType) obj).typeName);
	}
	
	@Override
	public String toString() {
		return typeName;
	}
	
	
}