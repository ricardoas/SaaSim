package saasim.core.event;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

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
public final class HashEventScheduler implements Serializable{
	
	public static final String EVENT_SCHEDULER_RANDOM_SEED = "random.seed";

	private static final long serialVersionUID = -5091449738303689646L;
	
	private long now;
	private long simulationEnd;
    private HashMap<Long, TreeSet<Event>> eventSet;
	private Random random;
	private SummaryStatistics stat;
    /**
     * Default constructor.
     * @param seed 
     * @throws IOException 
     */
   @Inject public HashEventScheduler(Configuration globalConf){
    	this.random = new Random(globalConf.getLong(EVENT_SCHEDULER_RANDOM_SEED));
    	this.now = 0;
		this.eventSet = new HashMap<>();
		this.stat = new SummaryStatistics();
    }
        
    /**
     * Add event to the queue.
     * @param event new {@link Event} to queue.
     */
    public void queueEvent(Event event) {
    	TreeSet<Event> list = eventSet.get(event.getScheduledTime());
    	if(list == null){
    		list = new TreeSet<>();
    	}
    	list.add(event);
		eventSet.put(event.getScheduledTime(), list);
	}

    /**
     * Start the emulation.
     */
    public void start(long simulationEnd) {
    	
    	this.simulationEnd = simulationEnd;
    	for (long i = 0; i < simulationEnd; i++) {
			TreeSet<Event> list = eventSet.get(i);
			if(list == null){
				continue;
			}
			stat.addValue(eventSet.size());
			
			while(!list.isEmpty()){
				Event event = list.pollFirst();
				now = event.getScheduledTime();
	    		event.trigger();
			}
		}
			
		System.out.println(stat);
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