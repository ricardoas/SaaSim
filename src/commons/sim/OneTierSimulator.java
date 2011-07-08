package commons.sim;

import java.io.IOException;
import java.util.List;

import commons.cloud.Request;
import commons.config.WorkloadParser;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import config.GEISTSimpleWorkloadParser;

import provisioning.Monitor;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class OneTierSimulator extends JEEventHandler implements Simulator {

	private WorkloadParser<List<Request>> workloadParser;
	private Monitor monitor;

	private LoadBalancer applicationServer;

	/**
	 * Constructor
	 */
	public OneTierSimulator() {
		this.workloadParser = new GEISTSimpleWorkloadParser("");

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
	 * {@inheritDoc}
	 */
	@Override
	public boolean setMonitor(Monitor monitor) {
		if (this.monitor != null) {
			return false;
		}
		this.monitor = monitor;
		return false;
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
						JEEventScheduler.SCHEDULER.queueEvent(parseEvent(request));
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
	private JEEvent parseEvent(Request request) {
		return new JEEvent(JEEventType.NEWREQUEST, applicationServer, new JETime(request.time), request);
	}
}
