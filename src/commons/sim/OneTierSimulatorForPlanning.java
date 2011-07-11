package commons.sim;

import java.util.List;

import commons.cloud.Request;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

public class OneTierSimulatorForPlanning extends OneTierSimulator {
	
	private List<Request> workload;
	public static long UTILIZATION_EVALUATION_PERIOD = 1000 * 60 * 5;//in millis
	
	public OneTierSimulatorForPlanning(List<Request> workload){
		this.workload = workload;
		this.loadBalancer = new LoadBalancer(new RanjanScheduler());
	}
	
	@Override
	public void start() {
		//Scheduling first events
		JEEventScheduler.SCHEDULER.queueEvent(new JEEvent(JEEventType.READWORKLOAD, this, new JETime(0), null));
		JEEventScheduler.SCHEDULER.queueEvent(new JEEvent(JEEventType.EVALUATEUTILIZATION, this.loadBalancer, new JETime(UTILIZATION_EVALUATION_PERIOD), UTILIZATION_EVALUATION_PERIOD));
	}
	
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case READWORKLOAD:
			if(this.workload != null & this.workload.size() > 0){
				for(Request request : this.workload){
					JEEventScheduler.SCHEDULER.queueEvent(parseEvent(request));
				}
			}
			break;
		default:
			break;
		}
	}
}
