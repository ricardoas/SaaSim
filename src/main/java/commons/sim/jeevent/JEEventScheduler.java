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
     * Default empty constructor.
     */
    public JEEventScheduler() {
    	this(Long.MAX_VALUE);
    }
    
    /**
     * Default empty constructor.
     */
    public JEEventScheduler(long simulationEnd) {
    	this.now = 0;
    	this.simulationEnd = simulationEnd;
		this.handlerMap = new HashMap<Integer, JEEventHandler>();
		this.eventSet = new TreeSet<JEEvent>();
		this.random = new Random();
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
    	
		if (!eventSet.isEmpty()) {
			schedule();
		}else{
			this.now = simulationEnd;
		}
		dumpPostMortemEvents();
    }

	private void dumpPostMortemEvents() {
		if(!eventSet.isEmpty()){
			System.err.println("There were " + eventSet.size() + " events scheduled for times after simulation end (" + simulationEnd + ").");
			while(!eventSet.isEmpty()){
				System.err.println(eventSet.pollFirst());
			}
		}
	}
    
    /**
     * Emulates until there is no more event to send or end of simulation time is reached.
     */
    private void schedule() {
    	
    	while(!eventSet.isEmpty() && now <= simulationEnd){
    		JEEvent event = eventSet.pollFirst();
    		now = event.getScheduledTime();
    		processEvent(event);
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
}