/* JEEventScheduler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import commons.io.Checkpointer;

/**
 * Event scheduler. Events are ordered in time.
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 2.0.0
 */
public class JEEventScheduler implements Serializable{
	
	/**
	 * Version 2.0.0
	 */
	private static final long serialVersionUID = -5091449738303689646L;
	
	private long now;
	private long simulationEnd;
    private HashMap<Integer,JEEventHandler> handlerMap;
    public TreeSet<JEEvent> eventSet;
	private Random random;
	
    /**
     * Default empty constructor.
     * @param simulationEnd TODO
     * @throws IOException 
     */
    public JEEventScheduler(long simulationEnd){
		reset(0, simulationEnd);
		this.handlerMap = new HashMap<Integer, JEEventHandler>();
		this.eventSet = new TreeSet<JEEvent>();
		this.random = new Random();
    }
    
    /**
	 * @param l
	 */
	public void reset(long simulationStart, long simulationEnd) {
		this.now = simulationStart;
		this.simulationEnd = simulationEnd;
	}

	/**
     * Add a new event to the queue. Duplicates are not allowed.
     * @param event A new event.
     */
    public void queueEvent(JEEvent event) {
    	
    	assert event.getScheduledTime() >= 0: "Possible miscalculation or long overflow.";
    	
    	long anEventTime = event.getScheduledTime();
    	if (anEventTime < now) {
		    throw new JEException("ERROR: emulation time(" + now + ") already ahead of event time("+anEventTime+"). Event is outdated and will not be processed.");
		}
		eventSet.add(event);
    }

    /**
     * Cancel a future event by removing it from the queue.
     * @param event Event to cancel.
     */
    public void cancelEvent(JEEvent event) {

    	assert event != null: "Null event not allowed.";
    	eventSet.remove(event);
    }
    
    /**
     * Register a new handler so that {@link JEEvent} queued to them can be
     * delivered.
     * @param handler A new handler
     * @return The handler unique ID.
     */
    public int registerHandler(JEEventHandler handler) {
		int id;
		while((id = random.nextInt()) <= 0 || handlerMap.containsKey(id)){
			// Loop until there's an available ID
		}
		handlerMap.put(id, handler);
		return id;
    }
    
    /**
     * Start the emulation.
     */
    public void start() {
    	
    	this.now = Checkpointer.loadSimulationInfo().getCurrentDayInMillis();
    	
		if (!eventSet.isEmpty()) {
			schedule();
		}else{
			this.now = simulationEnd;
		}
    }

	public String dumpPostMortemEvents() {
		StringBuilder sb = new StringBuilder();
		if(!eventSet.isEmpty()){
			while(!eventSet.isEmpty()){
				sb.append(eventSet.pollFirst());
				sb.append('\n');
			}
		}
		return sb.toString();
	}
    
    /**
     * Emulates until there is no more event to send or end of simulation time is reached.
     */
    private void schedule() {
    	
    	while(!eventSet.isEmpty() && eventSet.first().getScheduledTime() < simulationEnd){
    		JEEvent event = eventSet.pollFirst();
    		now = event.getScheduledTime();
    		processEvent(event);
    		event = null;
    	}
    }

	/**
     * Searches for the target handler and delivers the event.
     * @param event Next event to delivers.
     */
    private void processEvent(JEEvent event) {
		
    	assert handlerMap.containsKey(event.getTargetHandlerId()): "ERROR: no Handler registered with id " + (event.getTargetHandlerId());

		handlerMap.get(event.getTargetHandlerId()).handleEvent(event);
    }
    
    /**
     * @return
     */
    public long now() {
    	return now;
    }

	/**
	 * @param id
	 * @return
	 */
	@Deprecated
	public JEEventHandler getHandler(int id){
		return handlerMap.get(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JEEventScheduler [now=" + now + ", simulationEnd="
				+ simulationEnd + ", handlerMap=" + handlerMap + ", eventSet="
				+ eventSet + ", random=" + random + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		JEEventScheduler other = (JEEventScheduler)obj;
		return eventSet.equals(other.eventSet) && handlerMap.equals(other.handlerMap);
	}
	
	public void printStatus(){
    	for (JEEvent event : eventSet) {
    		System.out.println(event);
		}
	}
}