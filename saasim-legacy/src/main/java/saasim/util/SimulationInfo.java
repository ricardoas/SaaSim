package saasim.util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import saasim.config.Configuration;
import saasim.sim.util.SimulatorProperties;


/**
 * Information used by checkpoint service.
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class SimulationInfo implements Serializable{

	public static int[] daysInMonths = {30, 58, 89, 119, 150, 180, 211, 242, 272, 303, 333, 364};
	
	private static final long DAY_IN_MILLIS = 86400000;
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = 1062519431287118188L;
	private long currentDay;
	@Deprecated
	private int currentMonth;
	private final long finishDay;
	
	/**
	 * Default constructor.
	 */
	public SimulationInfo() {
		this.currentDay = -DAY_IN_MILLIS;
		this.finishDay = (Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)-1) * DAY_IN_MILLIS;
	}
	
	@Deprecated
	public SimulationInfo(int simulatedDays, int currentMonth) {
		this.currentDay = simulatedDays * DAY_IN_MILLIS;
		this.currentMonth = currentMonth;
		finishDay = Long.MAX_VALUE;
	}

	@Deprecated
	public SimulationInfo(int i, int j, int k) {
		currentDay = i;
		finishDay = k;
	}

	/**
	 * Gets the current day in the simulation.
	 * @return the current day
	 */
	public int getCurrentDay() {
		return (int)(currentDay/DAY_IN_MILLIS);
	}

	@Deprecated
	public int getCurrentMonth() {
		return currentMonth;
	}

	@Deprecated
	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	/**
	 * Adds a one day to current day in the simulation.
	 */
	public void addDay() {
		this.currentDay += DAY_IN_MILLIS;
	}
	
	/**
	 * @return <code>true</code> if this is the last simulation day.
	 */
	public boolean isFinishDay(){
		return currentDay == finishDay;
	}
	
	/**
	 * @return <code>true</code> if this is the first day in the simulation.
	 */
	public boolean isFirstDay() {
		return currentDay == 0;
	}

	/**
	 * Gets the value of current day in milliseconds.
	 * @return the value of current day in milliseconds.
	 */
	public long getCurrentDayInMillis() {
		return currentDay;
	}

	/**
	 * @return <code>true</code> if is day of charge in the simulation. 
	 */
	public boolean isChargeDay() {
		Calendar instance = GregorianCalendar.getInstance();
		instance.set(Calendar.YEAR, 2009);
		instance.set(Calendar.DAY_OF_YEAR, getCurrentDay()+2);
		return instance.get(Calendar.DAY_OF_MONTH) == 1;// && getCurrentDay() != 0;
	}

	/**
	 * Compare two {@link SimulationInfo}.
	 * <code>true</code> if them current day are equals.
	 */
	@Override
	public boolean equals(Object obj) {
		assert (obj != null) && (getClass() == obj.getClass()): "Can't compare with different class object.";
		
		if (this == obj)
			return true;
		return currentDay == ((SimulationInfo) obj).currentDay;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%d %b %b %b", getCurrentDay(), isFirstDay(), isFinishDay(), isChargeDay());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentMonth;
		result = prime * result + (int)currentDay;
		return result;
	}
}
