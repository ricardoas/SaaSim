package commons.util;

import java.io.Serializable;

public class SimulationInfo implements Serializable{
	
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimulationInfo other = (SimulationInfo) obj;
		if (currentMonth != other.currentMonth)
			return false;
		if (simulatedDays != other.simulatedDays)
			return false;
		return true;
	}
	
	
}
