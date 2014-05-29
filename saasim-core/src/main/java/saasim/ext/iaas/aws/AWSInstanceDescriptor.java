package saasim.ext.iaas.aws;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.saas.Application;

/**
 * instance descriptor as seen by AWS
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AWSInstanceDescriptor implements InstanceDescriptor {

	private AWSInstanceType type;
	private boolean on;
	private Machine machine;
	private long creationTime;
	private long finishTime;
	private long lastBillingTime;
	private boolean setup;
	private Application application;
	private static int SEED = 0;
	private int id;

	public AWSInstanceDescriptor(AWSInstanceType instanceType, long creationTime) {
		this.id = SEED++;
		this.type = instanceType;
		this.creationTime = creationTime;
		this.on = true;
		this.setup = true;
	}

	@Override
	public int getNumberOfCPUCores() {
		return type.getNumberOFCPU();
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
	public void turnOn(long now) {
		on = true;
		lastBillingTime = now;
	}

	@Override
	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	@Override
	public Application getApplication() {
		return application;
	}

	@Override
	public void setApplication(Application application) {
		this.application = application;
	}

	@Override
	public Machine getMachine() {
		return machine;
	}

	public AWSInstanceType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return Long.toString(id);
	}

	@Override
	public long getFinishTime() {
		return finishTime;
	}
}
