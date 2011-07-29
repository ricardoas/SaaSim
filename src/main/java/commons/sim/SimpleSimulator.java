package commons.sim;

import java.io.IOException;
import java.util.List;

import provisioning.DPS;
import provisioning.Monitor;
import provisioning.util.DPSFactory;

import commons.cloud.Request;
import commons.config.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.sim.util.ApplicationFactory;

import config.GEISTSimpleWorkloadParser;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleSimulator extends JEAbstractEventHandler implements Simulator, JEEventHandler{

	private final WorkloadParser<List<Request>> workloadParser;
	private final DPS monitor;
	protected LoadBalancer loadBalancer;

	/**
	 * Constructor
	 * @param scheduler TODO
	 * @param list 
	 */
	public SimpleSimulator(JEEventScheduler scheduler, DPS dps, WorkloadParser<List<Request>> parser) {
		super(scheduler);
		this.monitor = dps;
		this.monitor.setConfigurable(this);
		this.workloadParser = parser;
		this.loadBalancer = ApplicationFactory.getInstance().createNewApplication(scheduler, getMonitor(), dps.getSetupMachines());
	}
	
	/**
	 * Constructor
	 * @param scheduler TODO
	 * @param list 
	 */
	public SimpleSimulator() {
		this(new JEEventScheduler(), DPSFactory.INSTANCE.createDPS(), new GEISTSimpleWorkloadParser());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {

		prepareBeforeStart();
		getScheduler().start();
	}

	protected void prepareBeforeStart() {
		send(new JEEvent(JEEventType.READWORKLOAD, this, getScheduler().now()));
	}

	/**
	 * @return
	 */
	public Monitor getMonitor() {
		
		return monitor;
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
		default:
			break;
		}
	}

	/**
	 * @param request
	 * @return
	 */
	protected JEEvent parseEvent(Request request) {
		return new JEEvent(JEEventType.NEWREQUEST, loadBalancer, new JETime(request.getTimeInMillis()), request);
	}
}
