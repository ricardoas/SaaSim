/* JEEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;

import java.util.Arrays;


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
    private long scheduledTime;
    private final JEEventType type;
	private final Object[] value;
    
    
    /**
     * Default constructor
     * @param type TODO
     * @param targetHandler
     * @param scheduledTime
     */
    public JEEvent(JEEventType type, JEEventHandler targetHandler, long scheduledTime, Object... value) {
    	
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
    public int getTargetHandlerId() {
    	return targetHandlerId;
    }
    
    /**
     * @return
     */
    public long getScheduledTime() {
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
		long diff = (this.scheduledTime - o.scheduledTime);

		if(diff != 0){
			if(diff < 0){
				return -1;
			}else{
				return 1;
			}
		}
		
		int result = type.compareTo(o.type);
		if(result != 0){
			return result;
		}
		return eventId - o.eventId;
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
	

	@Override
	public String toString() {
		return "JEEvent [eventId=" + eventId + ", targetHandlerId="
				+ targetHandlerId + ", scheduledTime=" + scheduledTime
				+ ", type=" + type + ", value=" + Arrays.toString(value) + "]";
	}
}