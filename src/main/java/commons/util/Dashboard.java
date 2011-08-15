package commons.util;

import java.util.HashMap;
import java.util.Map;


public class Dashboard {

	public Map<Integer, SimulationData> simulationData;
	
	public Dashboard(){
		this.simulationData = new HashMap<Integer, SimulationData>();
	}
	

	public void createEntry(Integer numberOfMachinesToReserve, double numberOfOnDemandResources,
			double utility, double cost, double receipt,
			long totalInTransferred, long totalOutTransferred, double onDemandConsumption,
			double reservedConsumption) {
		SimulationData data = new SimulationData();
		data.numberOfMachinesReserved = numberOfMachinesToReserve;
		data.numberOfOnDemandMachines = numberOfOnDemandResources;
		data.estimatedUtility = utility;
		data.estimatedCost = cost;
		data.estimatedReceipt = receipt;
		data.totalInTransferred = totalInTransferred;
		data.totalOutTransferred = totalOutTransferred;
		data.totalOnDemandConsumedTime = onDemandConsumption;
		data.totalReservedConsumedTime = reservedConsumption;
		
		simulationData.put(numberOfMachinesToReserve, data);
	}
}
