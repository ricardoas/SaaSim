package planning.heuristic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provisioning.DPS;
import provisioning.util.DPSFactory;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.io.HistoryBasedWorkloadParser;
import commons.sim.SimpleSimulator;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorFactory;

public class HistoryBasedHeuristic implements PlanningHeuristic{

	private UtilityResult utilityResult;
	private Map<MachineType, Integer> plan;
	
	private double UTILISATION_THRESHOLD = 0.5;
	private JEEventScheduler scheduler;

	public HistoryBasedHeuristic(){
		this.plan = new HashMap<MachineType, Integer>();
	}
	
	@Override
	public void findPlan(HistoryBasedWorkloadParser workloadParser,
			Provider[] cloudProviders, User[] cloudUsers) {
		
		DPS dps = DPSFactory.createDPS();
		
		SimpleSimulator simulator = (SimpleSimulator) SimulatorFactory.buildSimulator(scheduler, dps);
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		utilityResult = dps.calculateUtility();
		
		LoadBalancer[] loadBalancers = simulator.getTiers();
		for(LoadBalancer lb : loadBalancers){
			List<Machine> servers = lb.getServers();
			for(Machine server : servers){
				double utilisation = (1.0 * server.getTotalTimeUsed())/(server.getDescriptor().getUpTimeInMillis() * server.getNumberOfCores());
				if(utilisation >= UTILISATION_THRESHOLD){
					Integer numberOfServers = this.plan.get(server.getDescriptor().getType());
					if(numberOfServers == null ){
						numberOfServers = 0;
					}
					numberOfServers++;
					this.plan.put(server.getDescriptor().getType(), numberOfServers);
				}
			}
		}
	}

	@Override
	public double getEstimatedProfit(int period) {
		return this.utilityResult.getUtility();
	}

	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		return this.plan;
	}

}
