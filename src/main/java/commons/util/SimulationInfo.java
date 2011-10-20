package commons.util;

import java.io.Serializable;

/**
 * Information used by checkpoint service.
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class SimulationInfo implements Serializable{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = 1062519431287118188L;
	private int simulatedDays;
	private int currentMonth;
	
	public SimulationInfo(int simulatedDays, int currentMonth) {
		super();
		this.simulatedDays = simulatedDays;
		this.currentMonth = currentMonth;
	}

	public int getSimulatedDays() {
		return simulatedDays;
	}

	public void setSimulatedDays(int simulatedDays) {
		this.simulatedDays = simulatedDays;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public void addSimulatedDay() {
		this.simulatedDays++;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentMonth;
		result = prime * result + simulatedDays;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		assert (obj != null) && (getClass() == obj.getClass()): "Can't compare with different class object.";
		
		if (this == obj)
			return true;
		SimulationInfo other = (SimulationInfo) obj;
		if (currentMonth != other.currentMonth)
			return false;
		return simulatedDays == other.simulatedDays;
	}
	
	
}
