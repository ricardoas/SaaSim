/* JEEventScheduler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * TODO make doc
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 */
public enum JEEventScheduler {
	
	SCHEDULER;
	
	private JETime now;
    private Vector<JEEvent> EventList = new Vector<JEEvent>();
    private Vector<JEEventHandler> HandlerList;
    private Boolean isActive;
    private JETime simulationEnd;
    
    private Map<Integer,JEEventHandler> handlerMap;
    private Set<JEEvent> eventSet;

    /**
     * 
     */
    private JEEventScheduler() {
    	clear();
    }
    
    /**
     * @param aNewEvent
     */
    public void queue_event(JEEvent aNewEvent) {
    	
		JETime anEventTime = aNewEvent.getScheduledTime();
		
		if (anEventTime.isEarlierThan(now())) {
		    throw new RuntimeException("ERROR: emulation time(" + now() + ") already ahead of event time("+anEventTime+"). Event is outdated and will not be processed.");
		}
		
		int queue_size = EventList.size();
		
		if (queue_size == 0) {
			EventList.addElement(aNewEvent);
		} else if ( EventList.lastElement().getScheduledTime().isEarlierThan(anEventTime)) {
			EventList.addElement(aNewEvent);
		} else {
		    
			int queue_pos;
		    
		    for (queue_pos = queue_size - 1; ( (queue_pos > 0) & anEventTime.isEarlierThan(EventList.elementAt(queue_pos).getScheduledTime())); queue_pos--) {
			/* empty */
		    }
		    
		    if (++queue_pos == 1 & anEventTime.isEarlierThan(EventList.elementAt(0).getScheduledTime())) {
		    	queue_pos--;
		    }
		    
		    EventList.insertElementAt(aNewEvent, queue_pos);
		}
    }
    
    /**
     * @param anObsoleteEvent
     */
    public void cancel_event(JEEvent anObsoleteEvent) {

    	if (anObsoleteEvent == null) {
			throw new NullPointerException();
		}
		EventList.remove(anObsoleteEvent);
    }
    
    /**
     * @param handler
     * @return
     */
    public int registerHandler(JEEventHandler handler) {
    	
    	
		Integer id = handler.getHandlerId();
		id = 1;
		
		while((id = new Random().nextInt(100)) <= 0 || handlerMap.containsKey(id)){}//FIXME remove 100

		if (HandlerList.size() < id.intValue() - 1) {
			HandlerList.setSize(id.intValue() - 1);
		}
		
		if (HandlerList.size() > id.intValue() - 1) {
			HandlerList.removeElementAt(id.intValue() - 1);
		}
		
		HandlerList.insertElementAt(handler,id.intValue() - 1);
		
		handlerMap.put(id, handler);
		return id;
    }
    
    /**
     * 
     */
    public void start() {
    	
		if (!EventList.isEmpty()) {
			schedule();
		}else{
			this.now = simulationEnd;
		}
    }
    
    /**
     * @return
     */
    private JEEvent peek() {
    	
		if (!EventList.isEmpty()) {
		    JEEvent aNextEvent = EventList.elementAt(0);
		    EventList.removeElementAt(0);
		    return aNextEvent;
		}
		
		return null;
    }
    
    /**
     * 
     */
    private void schedule() {
    	
		isActive = true;
		
		while (!EventList.isEmpty() & isActive.booleanValue() & isEarlierThanEmulationEnd(now()) ) {
		    
			JEEvent aNextEvent = peek();
		    
			if (aNextEvent != null) {
				
				JETime anEventTime = aNextEvent.getScheduledTime();
		
				if (anEventTime.isEarlierThan(now())) {
				    throw new RuntimeException("ERROR: emulation time(" + now() + ") " + "already ahead of event time(" + anEventTime+ "). Event is outdated and will not be processed.");
				}
				
				if (isEarlierThanEmulationEnd(anEventTime)) {
				    now = anEventTime;
				    processEvent(aNextEvent);
				} else {
				    now = simulationEnd;
				}
		    }
		}
		
		isActive = Boolean.valueOf(false);
    }
    
    private boolean isEarlierThanEmulationEnd(JETime time) {
    	return (simulationEnd != null) ? time.isEarlierThan(simulationEnd) : true;
    }
    
    /**
     * @param aNextEvent
     */
    private void processEvent(JEEvent aNextEvent) {
		
    	Integer aTargetHandlerId = aNextEvent.getTargetHandlerId();
    	
		if (HandlerList.elementAt(aTargetHandlerId.intValue() - 1) == null) {
		    throw new RuntimeException ("ERROR: no Handler at vector position "+ (aTargetHandlerId.intValue() - 1)+". Something's wrong here, dude.");
		}
		
		HandlerList.elementAt(aTargetHandlerId.intValue() - 1).handleEvent(aNextEvent);
    }
    
    /**
     * @return
     */
    public JETime now() {
    	return now;
    }

	public void setEmulationEnd(JETime time) {
		this.simulationEnd = time;
	}
	
	public JEEventHandler getHandler(int id){
		return handlerMap.get(id);
	}

	public void clear() {
	   	EventList.setSize(10000);
		EventList.clear();
		HandlerList = new Vector<JEEventHandler>();
		HandlerList.setSize(100);
		HandlerList.clear();
		isActive = false;
		
		this.now = new JETime(0L);
		this.simulationEnd = JETime.INFINITY;
		this.handlerMap = new HashMap<Integer, JEEventHandler>();
		this.eventSet = new TreeSet<JEEvent>();
	}
}