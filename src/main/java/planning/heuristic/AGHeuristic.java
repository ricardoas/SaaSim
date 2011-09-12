package planning.heuristic;

import static commons.sim.util.IaaSPlanProperties.IAAS_PLAN_PROVIDER_NAME;
import static commons.sim.util.IaaSPlanProperties.IAAS_PLAN_PROVIDER_RESERVATION;
import static commons.sim.util.IaaSPlanProperties.IAAS_PLAN_PROVIDER_TYPES;

import java.util.HashMap;
import java.util.Map;

import provisioning.DPS;
import provisioning.util.DPSFactory;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.io.HistoryBasedWorkloadParser;
import commons.sim.Simulator;
import commons.sim.util.SimulatorFactory;

public class AGHeuristic implements PlanningHeuristic{
	
	private UtilityResult utilityResult;

	@Override
	public void findPlan(HistoryBasedWorkloadParser workloadParser,
			Provider[] cloudProviders, User[] cloudUsers) {
		//TODO: Read data from output file of JGAP, and simulate it in order to obtain detailed information
//		Configuration.buildInstance(args[0]);
		Configuration config = Configuration.getInstance();
		config.setProperty(IAAS_PLAN_PROVIDER_NAME, new String[]{});
		config.setProperty(IAAS_PLAN_PROVIDER_TYPES, new String[]{});
		config.setProperty(IAAS_PLAN_PROVIDER_RESERVATION, new long[]{});
		
		DPS dps = DPSFactory.createDPS();
		
		Simulator simulator = SimulatorFactory.buildSimulator(dps);
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		utilityResult = dps.calculateUtility();
	}

	@Override
	public double getEstimatedProfit(int period) {
		return utilityResult.getUtility();
	}

	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		Map<MachineType, Integer> plan = new HashMap<MachineType, Integer>();
		//TODO read from files obtained from JGAP!
		return plan;
	}
}
