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
}
