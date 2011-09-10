/* JEEventScheduler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

/**
 * TODO make doc
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class JEEventScheduler {
	
	private long now;
	private long simulationEnd;
    private Map<Integer,JEEventHandler> handlerMap;
    private TreeSet<JEEvent> eventSet;
	private Random random;

    /**
     * Default private constructor.
     */
    public JEEventScheduler() {
    	this.now = 0;
    	this.simulationEnd = JETime.INFINITY.timeMilliSeconds;
		this.handlerMap = new HashMap<Integer, JEEventHandler>();
		this.eventSet = new TreeSet<JEEvent>();
		this.random = new Random();
    }
    
    /**
     * Add a new event to the queue. Duplicates are not allowed.
     * @param event A new event.
     */
    public void queueEvent(JEEvent event) {
    	
    	long anEventTime = event.getScheduledTime();
		if (anEventTime - now() < 0 || isEventAfterEnd(anEventTime)) {
		    throw new JEException("ERROR: emulation time(" + now() + ") already ahead of event time("+anEventTime+"). Event is outdated and will not be processed.");
		}
		eventSet.add(event);
    }

	private boolean isEventAfterEnd(long anEventTime) {
		if(anEventTime > this.simulationEnd){
			if(this.simulationEnd == JETime.INFINITY.timeMilliSeconds){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
    
    /**
     * Cancel a future event by removing it from the queue.
     * @param event Event to cancel.
     */
    public void cancelEvent(JEEvent event) {

    	if (event == null) {
			throw new NullPointerException();
		}
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
    	
		if (!eventSet.isEmpty()) {
			schedule();
		}else{
			this.now = simulationEnd;
		}
    }
    
    /**
     * Emulates until there is no more event to send or end of simulation time is reached.
     */
    private void schedule() {
    	
    	while(!eventSet.isEmpty() && (shouldEventBeProcessed())){
    		JEEvent event = eventSet.pollFirst();
			
    		long scheduledTime = event.getScheduledTime();
			if (scheduledTime - now() < 0) {
			    throw new JEException("ERROR: emulation time(" + now() + ") " + "already ahead of event time(" + scheduledTime+ "). Event is outdated and will not be processed.");
			}
			
			if(scheduledTime != simulationEnd){
				now = scheduledTime;
				processEvent(event);
			}else{
				now = simulationEnd;
			}
    	}
    }

	private boolean shouldEventBeProcessed() {
		if(now() <= simulationEnd){
			return true;
		}else if(simulationEnd == JETime.INFINITY.timeMilliSeconds){
			return true;
		}
		return false;
	}
    
    /**
     * Searches for the target handler and delivers the event.
     * @param event Next event to delivers.
     */
    private void processEvent(JEEvent event) {
		
    	int targetHandlerId = event.getTargetHandlerId();
    	if(!handlerMap.containsKey(targetHandlerId)){
    		throw new JEException ("ERROR: no Handler registered with id " + (targetHandlerId));
    	}
    	handlerMap.get(targetHandlerId).handleEvent(event);
    }
    
    /**
     * @return
     */
    public long now() {
    	return now;
    }

	/**
	 * @param time
	 */
	public void setEmulationEnd(long time) {
		this.simulationEnd = time;
	}
    
	/**
	 * @param id
	 * @return
	 */
	public JEEventHandler getHandler(int id){
		return handlerMap.get(id);
	}
}