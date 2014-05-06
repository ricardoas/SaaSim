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

	/**
	 * Default constructor
	 * 
	 * @param scheduledTime time to trigger this {@link Event}
	 */
	public Event(long scheduledTime) {
		this.id = idSeed++;
		this.scheduledTime = scheduledTime;
	}
	
	/**
	 * @return time to trigger this {@link Event}
	 */
	public long getScheduledTime() {
		return scheduledTime;
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
		return Long.compare(scheduledTime, o.scheduledTime);
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

	/**
	 * @return <code>true</code> when this {@link Event} is scheduled to trigger before time given as parameter.
	 */
	public boolean happensBefore(long time) {
		return scheduledTime <= time;
	}

}
