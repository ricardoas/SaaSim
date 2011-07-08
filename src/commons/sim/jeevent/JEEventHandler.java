/* JEEventHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;

/**
 * TODO make doc
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 */
public abstract class JEEventHandler {
	
    private static Integer theUniqueHandlerId;
    private final Integer HandlerId;
    private final JEEventScheduler theUniqueEventScheduler;
    
    /**
     * @param scheduler
     */
    public JEEventHandler(JEEventScheduler scheduler) {
    	
		if (theUniqueHandlerId != null) {
		    theUniqueHandlerId = new Integer(theUniqueHandlerId.intValue() + 1);
		} else {
		    theUniqueHandlerId = 1;
		}
		HandlerId = theUniqueHandlerId;
		theUniqueEventScheduler = scheduler;
		theUniqueEventScheduler.register_handler(this);
    }
    
    /**
     * @return
     */
    protected JEEventScheduler getScheduler() {
    	return theUniqueEventScheduler;
    }
    
    /**
     * @param jeevent
     */
    public abstract void event_handler(JEEvent jeevent);
    
    /**
     * @param anEvent
     */
    public void send(JEEvent anEvent) {
    	theUniqueEventScheduler.queue_event(anEvent);
    }
    
    /**
     * @return
     */
    public Integer getHandlerId() {
    	return HandlerId;
    }
}
