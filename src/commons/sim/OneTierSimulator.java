package commons.sim;

import java.io.IOException;
import java.util.List;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class OneTierSimulator extends JEAbstractEventHandler implements Simulator, JEEventHandler{

	private final WorkloadParser<List<Request>> workloadParser;
	private final Monitor monitor;
	protected LoadBalancer loadBalancer;

	/**
	 * Constructor
	 * @param scheduler TODO
	 */
	public OneTierSimulator(JEEventScheduler scheduler, Monitor monitor, WorkloadParser<List<Request>> parser) {
		super(scheduler);
		this.monitor = monitor;
		this.workloadParser = parser;//new GEISTSimpleWorkloadParser("");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {

		// try {
		// while(workloadParser.hasNext()){
		// Request request = workloadParser.next();
		// applicationServer.run(request);
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
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
						getScheduler().queueEvent(parseEvent(request));
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
		return new JEEvent(JEEventType.NEWREQUEST, loadBalancer, new JETime(request.time), request);
	}
}
