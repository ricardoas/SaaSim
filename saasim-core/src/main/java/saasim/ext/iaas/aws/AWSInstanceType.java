package saasim.ext.iaas.aws;

import saasim.core.iaas.InstanceType;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AWSInstanceType implements InstanceType{

	private String name;
	private int processor;
	private double ecu;
	private double memory;
	private double storage;

	public AWSInstanceType(String name, String processor, String ecu, String memory, String storage) {
					this.name = name;
					this.processor = Integer.valueOf(processor);
					this.ecu = Double.valueOf(ecu);
					this.memory = Double.valueOf(memory);
					this.storage = Double.valueOf(storage);
	}

	@Override
	public int getNumberOFCPU() {
		return processor;
	}

	@Override
	public double getRelativePower() {
		return ecu;
	}

	public String getName() {
		return name;
	}

	public double getMemory() {
		return memory;
	}

	public double getStorage() {
		return storage;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return name.equals(((AWSInstanceType) obj).name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
}