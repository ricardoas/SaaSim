package commons.util;

import java.util.HashMap;
import java.util.Map;


public class Dashboard {

	public Map<Integer, SimulationData> simulationData;
	
	public Dashboard(){
		this.simulationData = new HashMap<Integer, SimulationData>();
	}
	
	public void createEntry(Integer numberOfMachinesToReserve, int numberOfOnDemandResources,
			double utility, double cost, double receipt, double totalTransferred,
			double onDemandConsumption, double reservedConsumption) {
		SimulationData data = new SimulationData();
		data.numberOfMachinesReserved = numberOfMachinesToReserve;
		data.numberOfOnDemandMachines = numberOfOnDemandResources;
		data.estimatedUtility = utility;
		data.estimatedCost = cost;
		data.estimatedReceipt = receipt;
		data.totalTransferred = totalTransferred;
		data.totalOnDemandConsumedTime = onDemandConsumption;
		data.totalReservedConsumedTime = reservedConsumption;
		
		simulationData.put(numberOfMachinesToReserve, data);
	}
}
