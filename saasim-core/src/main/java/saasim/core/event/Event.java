package saasim.core.event;


/**
 * Scheduled event.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class Event implements Comparable<Event>{
	
	
	private static int idSeed = 0;

	private final int id;
	private final long scheduledTime;
	private EventPriority priority;

	/**
	 * Builds a {@link Event} with {@link EventPriority#DEFAULT} as event priority. 
	 * 
	 * @param scheduledTime time to trigger this {@link Event}
	 */
	public Event(long scheduledTime) {
		this(scheduledTime, EventPriority.DEFAULT);
	}
	
	/**
	 * Default constructor.
	 * @param scheduledTime time to trigger this {@link Event}
	 * @param priority {@link EventPriority}
	 */
	public Event(long scheduledTime, EventPriority priority) {
		this.id = idSeed++;
		this.scheduledTime = scheduledTime;
		this.priority = priority;
	}

	/**
	 * @return time to trigger this {@link Event}
	 */
	public long getScheduledTime() {
		return scheduledTime;
	}
	
	/**
	 * @return <code>true</code> when this {@link Event} is scheduled to trigger before time given as parameter.
	 */
	public boolean happensBefore(long time) {
		return scheduledTime <= time;
	}

	/**
	 * Perform an action
	 */
	public abstract void trigger();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Event o) {
		int result = Long.compare(scheduledTime, o.scheduledTime);
		
		if(result != 0){
			return result;
		}
		
		result = priority.compareTo(o.priority);
		return result != 0? result: Integer.compare(id, o.id);
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
	
}
