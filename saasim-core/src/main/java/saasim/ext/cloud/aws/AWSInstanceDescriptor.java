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
	private boolean reserved;
	private long startTime;
	private long finishTime;
	private long lastBillingTime;

	public AWSInstanceDescriptor(AWSInstanceType instanceType, boolean reserved, long startTime) {
		this.type = instanceType;
		this.reserved = reserved;
		this.startTime = startTime;
		this.lastBillingTime = startTime;
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
	public void turnOff(long now) {
		on = false;
		finishTime = now;
		machine.shutdown();
	}

	@Override
	public void setMachine(Machine machine) {
		this.machine = machine;
	}

}
