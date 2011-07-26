package commons.sim;

import java.io.IOException;
import java.util.List;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
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
public class SimpleSimulator extends JEAbstractEventHandler implements Simulator, JEEventHandler{

	private final WorkloadParser<List<Request>> workloadParser;
	private final Monitor monitor;
	protected LoadBalancer loadBalancer;

	/**
	 * Constructor
	 * @param scheduler TODO
	 * @param list 
	 */
	public SimpleSimulator(JEEventScheduler scheduler, Monitor dps, WorkloadParser<List<Request>> parser, List<Machine> setupMachines) {
		super(scheduler);
		this.monitor = dps;
		this.monitor.setConfigurable(this);
		this.workloadParser = parser;
		this.loadBalancer = ApplicationFactory.getInstance().createNewApplication(scheduler, dps, setupMachines);
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
				// TODO Auto-generated catch block
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
