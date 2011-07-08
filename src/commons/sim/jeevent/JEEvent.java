/* JEEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;

/**
 * TODO make doc
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class JEEvent {
	
	private static int eventIdSeed = 0;
	
	private final int eventId;
    private final String name;
    
    private final Integer targetHandlerId;
    private final JETime scheduledTime;
    
    
    
    /**
     * Default constructor
     * 
     * @param name
     * @param targetHandler
     * @param scheduledTime
     */
    public JEEvent(String name, JEEventHandler targetHandler, JETime scheduledTime) {
	
    	this.eventId = eventIdSeed++;
    	
    	this.name = name;
		this.targetHandlerId = targetHandler.getHandlerId();
		this.scheduledTime = scheduledTime;
    }
    
    /**
     * @return
     */
    public Integer getTargetHandlerId() {
    	return targetHandlerId;
    }
    
    /**
     * @return
     */
    public String getName() {
    	return name;
    }
    
    /**
     * @return
     */
    public JETime getScheduledTime() {
    	return scheduledTime;
    }
    
    /**
     * @return
     */
    public int getEventId() {
		return eventId;
	}
    
}