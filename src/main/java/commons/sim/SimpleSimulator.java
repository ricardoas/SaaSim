package commons.sim;

import static commons.io.TimeBasedWorkloadParser.DAY_IN_MILLIS;
import java.io.IOException;
import java.util.List;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.io.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.sim.util.ApplicationFactory;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleSimulator extends JEAbstractEventHandler implements JEEventHandler, Simulator{
	
	private int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private int currentMonth = 0;

	private WorkloadParser<List<Request>> workloadParser;
	
	private List<LoadBalancer> tiers;

	/**
	 * Constructor
	 * @param list 
	 * @throws IOException 
	 */
	public SimpleSimulator(Monitor monitor){
		super(new JEEventScheduler());
		this.tiers = ApplicationFactory.getInstance().createNewApplication(getScheduler(), monitor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		prepareBeforeStart();
		getScheduler().start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkloadParser(WorkloadParser<List<Request>> workloadParser) {
		this.workloadParser = workloadParser;
	}
	
	protected void prepareBeforeStart() {
		send(new JEEvent(JEEventType.READWORKLOAD, this, getScheduler().now()));
		send(new JEEvent(JEEventType.CHARGE_USERS, this, getScheduler().now().plus(new JETime(DAY_IN_MILLIS * daysInMonths[currentMonth++]))));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case READWORKLOAD:
				try {
					if (workloadParser.hasNext()) {
						List<Request> list = workloadParser.next();
						for (Request request : list) {
							send(parseEvent(request));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case CHARGE_USERS:
				tiers.get(0).chargeUsers();
				send(new JEEvent(JEEventType.CHARGE_USERS, this, getScheduler().now().plus(new JETime(DAY_IN_MILLIS * daysInMonths[currentMonth++]))));
			default:
				break;
		}
	}

	/**
	 * @param request
	 * @return
	 */
	protected JEEvent parseEvent(Request request) {
		return new JEEvent(JEEventType.NEWREQUEST, tiers.get(0), new JETime(request.getTimeInMillis()), request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addServer(int tier, MachineDescriptor machineDescriptor, boolean useStartUpDelay) {
		tiers.get(tier).addServer(machineDescriptor, useStartUpDelay);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeServer(int tier, MachineDescriptor machineDescriptor,
			boolean force) {
		tiers.get(tier).removeServer(machineDescriptor, force);
	}

	@Override
	public void removeServer(int tier, boolean force) {
		tiers.get(tier).removeServer(force);
	}
}
