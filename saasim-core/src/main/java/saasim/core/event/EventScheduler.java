package saasim.core.event;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.TreeSet;

import saasim.core.config.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Event scheduler. Events are ordered in time.
 *
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 4.0.0
 */
@Singleton
public final class EventScheduler implements Serializable{
	
	public static final String EVENT_SCHEDULER_RANDOM_SEED = "random.seed";

	private static final long serialVersionUID = -5091449738303689646L;
	
	private long now;
	private long simulationEnd;
    private TreeSet<Event> eventSet;
	private Random random;

	/**
     * Default constructor.
     * @param seed 
     * @throws IOException 
     */
   @Inject public EventScheduler(Configuration globalConf){
    	this.random = new Random(globalConf.getLong(EVENT_SCHEDULER_RANDOM_SEED));
    	this.now = 0;
		this.eventSet = new TreeSet<Event>();
    }
        
    /**
     * Add event to the queue.
     * @param event new {@link Event} to queue.
     */
    public void queueEvent(Event event) {
		eventSet.add(event);
	}

    /**
     * Start the emulation.
     */
    public void start(long simulationEnd) {
    	
    	this.simulationEnd = simulationEnd;
		if (!eventSet.isEmpty()) {
			Event event = null;
	    	while((event = eventSet.pollFirst()) != null){
	    		if(!event.happensBefore(simulationEnd)){
	    			eventSet.add(event);
	    			break;
	    		}
	    		now = event.getScheduledTime();
	    		event.trigger();
	    	}
		}
		this.now = simulationEnd;
    }

    /**
     * @return current simulation time.
     */
    public long now(){
    	return this.now;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EventScheduler [now=" + now + ", simulationEnd="
				+ simulationEnd + ", random=" + random 
				+ ", eventSet="	+ eventSet +  "]";
	}

}