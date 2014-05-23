package saasim.ext.iaas.aws;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.saas.Application;
import saasim.ext.iaas.aws.AWSProvider.MarketType;

/**
 * instance descriptor as seen by AWS
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AWSInstanceDescriptor implements InstanceDescriptor {

	private AWSInstanceType type;
	private boolean on;
	private Machine machine;
	private MarketType market;
	private long creationTime;
	private long finishTime;
	private long lastBillingTime;
	private boolean setup;
	private Application application;

	public AWSInstanceDescriptor(AWSInstanceType instanceType, MarketType market, long creationTime) {
		this.type = instanceType;
		this.market = market;
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
	public double reportUsage(StringBuilder report, long now) {
		
		double[] reportInfo = new double[11];
		
		switch (market) {
		case HEAVY:
			reportInfo[2] = finishTime == 0? now - lastBillingTime: finishTime - lastBillingTime;
			reportInfo[3] = setup? type.getHeavyUpfront(): 0;
			reportInfo[4] = reportInfo[0] * type.getHeavyHourly();
			break;
		case LIGHT:
			reportInfo[5] = finishTime == 0? now - lastBillingTime: finishTime - lastBillingTime;
			reportInfo[6] = setup? type.getLightUpfront(): 0;
			reportInfo[7] = reportInfo[5] * type.getLightHourly();
			break;
		case MEDIUM:
			reportInfo[8] = finishTime == 0? now - lastBillingTime: finishTime - lastBillingTime;
			reportInfo[9] = setup? type.getMediumUpfront(): 0;
			reportInfo[10] = reportInfo[8] * type.getMediumHourly();
			break;
		case ONDEMAND:
			reportInfo[0] = Math.ceil(1.0*(finishTime == 0? now - lastBillingTime: finishTime - lastBillingTime)/3600000);
			reportInfo[1] = reportInfo[0] * type.getHourly();
			break;
		case SPOT:
		default:
			break;
		}
		
		setup = false;
		
		report.append(type);
		for (double info : reportInfo) {
			report.append(',');
			report.append(info);
		}
		report.append('\n');
		
		double total = 0.0;
		total += reportInfo[1];
		total += reportInfo[2];
		total += reportInfo[4];
		total += reportInfo[5];
		total += reportInfo[7];
		total += reportInfo[8];
		total += reportInfo[10];
		return total;
	}

	@Override
	public Application getApplication() {
		return application;
	}

	@Override
	public void setApplication(Application application) {
		this.application = application;
	}
}
