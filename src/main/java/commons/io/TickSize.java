/**
 * 
 */
package commons.io;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum TickSize {
	
	SECOND(1000), 
	MINUTE(60 * SECOND.getTickInMillis()),
	HALF_HOUR(30 * MINUTE.getTickInMillis()),
	HOUR(60 * MINUTE.getTickInMillis()),
	TWO_HOURS(2 * 60 * MINUTE.getTickInMillis()),
	DAY(24 * HOUR.getTickInMillis()),
	MONTH(31 * DAY.getTickInMillis()),
	YEAR(365 * DAY.getTickInMillis());
	
	private final long tickInMillis;
	
	/**
	 * Default constructor.
	 */
	private TickSize(long tickInMillis) {
		this.tickInMillis = tickInMillis;
	}

	/**
	 * @return the tickInMillis
	 */
	public long getTickInMillis() {
		return tickInMillis;
	}
}
