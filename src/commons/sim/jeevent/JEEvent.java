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
public class JEEvent implements Comparable<JEEvent>{
	
	private static int eventIdSeed = 0;
	
	private final int eventId;
    private final String name;
    private final int targetHandlerId;
    private final JETime scheduledTime;
    private final JEEventType type;
    
    
    
    /**
     * Default constructor
     * @param type TODO
     * @param name
     * @param targetHandler
     * @param scheduledTime
     */
    public JEEvent(JEEventType type, String name, JEEventHandler targetHandler, JETime scheduledTime) {
	
    	this.eventId = eventIdSeed++;
    	
    	this.name = name;
		this.targetHandlerId = targetHandler.getHandlerId();
		this.scheduledTime = scheduledTime;
		this.type = type;
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
    
	/**
	 * @return the type
	 */
	public JEEventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(JEEvent o) {
		int result = getScheduledTime().compareTo(o.getScheduledTime());
		return result != 0? result: type.compareTo(o.getType());
	}
    
}