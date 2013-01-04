package saasim.core.event;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;


/**
 * Each timed action in the simulation environment is modeled as an {@link Event}. Events are composed by
 * an id, a target handler id, a <code>long</code> time stamp, a type and arguments to forward to the handler.
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public final class Event implements Comparable<Event>, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1977642650443631889L;

	private static int idSeed = 0;
	
	private final int id;
	private EventHandler target;
	private Class<? extends Annotation> type;
    private long timestamp;
    private EventPriority priority;
	private Object[] arguments;
	
	
	/**
	 * Constructor of Event with {@link EventPriority} set to DEFAULT.
	 * @param target
	 * @param type
	 * @param timestamp
	 * @param arguments
	 * @return
	 */
	public Event(EventHandler target, Class<? extends Annotation> type,
			long timestamp, Object... arguments) {
		this(target, type, timestamp, EventPriority.DEFAULT, arguments);
	}


	/**
	 * 
	 * @param target
	 * @param type
	 * @param timestamp
	 * @param priority
	 * @param arguments
	 */
	public Event(EventHandler target, Class<? extends Annotation> type,
			long timestamp, EventPriority priority, Object... arguments) {

		
		assert type != null;
    	assert target  != null;
    	assert timestamp >= 0;
    	assert priority != null;

		this.id = idSeed++;
		this.target = target;
		this.type = type;
		this.timestamp = timestamp;
		this.priority = priority;
		this.arguments = arguments;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the target
	 */
	public EventHandler getTarget() {
		return target;
	}

	/**
	 * @return the type
	 */
	public Class<? extends Annotation> getType() {
		return type;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the priority
	 */
	public EventPriority getPriority() {
		return priority;
	}

	/**
	 * @return the arguments
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Event o) {
		int result = (timestamp < o.timestamp ? -1 : (timestamp == o.timestamp ? 0 : 1));

		if(result != 0){
			return result;
		}
		
		result = priority.compareTo(o.priority);
		
		return result != 0? result: id - o.id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		assert (obj != null): "Can't compare with null object.";
		assert (getClass() != obj.getClass()): "Can't compare with a different class object.";
		
		if (this == obj)
			return true;
		return (id == ((Event) obj).id);
	}

	@Override
	public String toString() {
		return "[TIME=" + timestamp + ", ID="
				+ id + ", Handler=" + target
				+ ", TYPE=" + type + ", V=" + Arrays.toString(arguments) + "]";
	}
}