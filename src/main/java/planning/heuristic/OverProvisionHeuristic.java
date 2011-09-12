package planning.heuristic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import provisioning.util.WorkloadParserFactory;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.io.HistoryBasedWorkloadParser;
import commons.io.WorkloadParser;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;

public class OverProvisionHeuristic implements PlanningHeuristic{

	public static final double FACTOR = 0.2;//Utilisation factor according to Above the Clouds: ...
	private int maximumNumberOfServers;
	
	public OverProvisionHeuristic(){
		this.maximumNumberOfServers = 0;
	}

	@Override
	public void findPlan(HistoryBasedWorkloadParser workloadParser,
			Provider[] cloudProviders, User[] cloudUsers) {

		WorkloadParser<List<Request>> currentParser = WorkloadParserFactory.getWorkloadParser(Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME));
		maximumNumberOfServers = 0;
		
		while(currentParser.hasNext()){
			List<Request> requests = currentParser.next();
			Set<Integer> simultaneousUsers = new HashSet<Integer>();

			for(Request request : requests){
				simultaneousUsers.add(request.getUserID());
			}

			if(simultaneousUsers.size() > maximumNumberOfServers){
				maximumNumberOfServers = simultaneousUsers.size();
			}
		}
	}

	@Override
	public double getEstimatedProfit(int period) {
		return 0;
	}

	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		Map<MachineType, Integer> plan = new HashMap<MachineType, Integer>();
		MachineType machineType = MachineType.valueOf(Configuration.getInstance().getString(SimulatorProperties.PLANNING_TYPE).toUpperCase());
		plan.put(machineType, (int)Math.ceil(maximumNumberOfServers * FACTOR));
		return plan;
	}
}
