package saasim.ext.cloud.aws;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;

/**
 * instance descriptor as seen by AWS
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AWSInstanceDescriptor implements InstanceDescriptor {

	private AWSInstanceType type;
	private boolean on;
	private Machine machine;

	public AWSInstanceDescriptor(AWSInstanceType instanceType) {
		this.type = instanceType;
		this.on = true;
	}

	@Override
	public int getNumberOfCPUCores() {
		return type.getNumberOfCores();
	}

	@Override
	public boolean isOn() {
		return on;
	}


	@Override
	public void turnOff() {
		on = false;
		this.machine.shutdown();
	}

	@Override
	public void setMachine(Machine machine) {
		this.machine = machine;
	}

}
