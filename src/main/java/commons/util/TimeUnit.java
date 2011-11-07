/**
 * 
 */
package commons.util;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum TimeUnit {
	
	SECOND(1000), 
	MINUTE(60 * SECOND.getMillis()),
	TEN_MINUTES(10 * MINUTE.getMillis()),
	HALF_HOUR(30 * MINUTE.getMillis()),
	HOUR(60 * MINUTE.getMillis()),
	TWO_HOURS(2 * 60 * MINUTE.getMillis()),
	DAY(24 * HOUR.getMillis()),
	MONTH(31 * DAY.getMillis()),
	YEAR(365 * DAY.getMillis());
	
	private final long timeInMillis;
	
	/**
	 * Default constructor.
	 */
	private TimeUnit(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

	/**
	 * @return the tickInMillis
	 */
	public long getMillis() {
		return timeInMillis;
	}
}
