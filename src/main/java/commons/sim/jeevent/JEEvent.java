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
    private final int targetHandlerId;
    private final JETime scheduledTime;
    private final JEEventType type;
	private final Object[] value;
    
    
    /**
     * Default constructor
     * @param type TODO
     * @param targetHandler
     * @param scheduledTime
     */
    public JEEvent(JEEventType type, JEEventHandler targetHandler, JETime scheduledTime, Object... value) {
	
		this.eventId = eventIdSeed++;
    	
		this.targetHandlerId = targetHandler.getHandlerId();
		this.scheduledTime = scheduledTime;
		this.type = type;
		this.value = value;
		
    }
    
    /**
     * @param event
     * @param targetHandler
     */
    public JEEvent(JEEvent event, JEEventHandler targetHandler) {
		this(event.getType(), targetHandler, event.getScheduledTime(), event.getValue());
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + eventId;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JEEvent other = (JEEvent) obj;
		if (eventId != other.eventId)
			return false;
		return true;
	}

	/**
	 * @return
	 */
	public Object[] getValue() {
		return value;
	}
}