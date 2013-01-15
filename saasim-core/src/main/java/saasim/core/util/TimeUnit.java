package saasim.core.util;

/**
 * This class represents units of time in the application simulation domain. 
 * 
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
	MONTH(30 * DAY.getMillis()),
	YEAR(365 * DAY.getMillis()),
	THREE_YEARS(3 * YEAR.getMillis());
	
	private final long timeInMillis;
	
	/**
	 * Default private constructor. 
	 * @param timeInMillis value of time in milliseconds
	 */
	private TimeUnit(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

	/**
	 * Gets the value of time in milliseconds
	 * @return the time in milliseconds. 
	 */
	public long getMillis() {
		return timeInMillis;
	}
}
