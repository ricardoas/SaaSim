package commons.io;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.util.SaaSUsersProperties;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeBasedWorkloadParser extends JEAbstractEventHandler implements WorkloadParser<List<Request>>{
	
	private static final long TRANS_PERIOD = 1;
	private final long tick;
	private long currentTick;

	private List<Request> leftOver;
	private WorkloadParser<Request>[] parsers;
	
	/**
	 * @param tick
	 * @param parser
	 */
	public TimeBasedWorkloadParser(JEEventScheduler scheduler, long tick, WorkloadParser<Request>... parser) {
		super(scheduler); 
		this.parsers = parser;
		this.tick = tick;
		this.currentTick = 0;
		this.leftOver = new ArrayList<Request>();
		
		prepareBeforeStart();
	}
	
	private void prepareBeforeStart() {
		long[] peakDays = Configuration.getInstance().getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD);
		int simulatedDays = Configuration.getInstance().getSimulationInfo().getSimulatedDays();
		for(long peakDay : peakDays){
			if(simulatedDays + 1 == peakDay - TRANS_PERIOD){
				send(new JEEvent(JEEventType.TRANSITION, this, getScheduler().now() ));
			}else if(simulatedDays + 1 == peakDay){
				send(new JEEvent(JEEventType.PEAK, this, getScheduler().now() ) );
			}else if(simulatedDays + 1 == peakDay + TRANS_PERIOD){
				send(new JEEvent(JEEventType.PEAK_END, this, getScheduler().now() ) );
			}
		}
	}

	@Override
	public void clear() {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Request> next(){
		List<Request> requests = new ArrayList<Request>(leftOver);
		
		long time = (currentTick + 1) * tick;
		leftOver.clear();
		for (Request request : requests) {
			if(request.getArrivalTimeInMillis() >= time){
				leftOver.add(request);
			}
		}
		requests.removeAll(leftOver);
		
		for (WorkloadParser<Request> parser : parsers) {
			while(parser.hasNext()){
				Request request = parser.next();
				if(request.getArrivalTimeInMillis() < time){
					requests.add(request);
				}else{
					leftOver.add(request);
					break;
				}
			}
		}
		
		this.currentTick++;
		return requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		for (WorkloadParser<Request> parser: parsers) {
			if(parser.hasNext()){
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case TRANSITION:
				System.gc();
				for(WorkloadParser<Request> parser : this.parsers){
					parser.changeToTransition();
				}
//				System.out.println("TRANS: "+currentTick);
				break;
			case PEAK:
				System.gc();
				for(WorkloadParser<Request> parser : this.parsers){
					 parser.changeToPeak();
				}
//				System.out.println("PEAK: "+currentTick);
				break;
			case PEAK_END:
				System.gc();
				for(WorkloadParser<Request> parser : this.parsers){
					parser.changeToNormal();
				}
//				System.out.println("END: "+currentTick);
				break;
		}		
	}

	@Override
	public int changeToPeak() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public int changeToNormal() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public int changeToTransition() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void setDaysAlreadyRead(int simulatedDays) {
		throw new RuntimeException("Not yet implemented");
	}
}
