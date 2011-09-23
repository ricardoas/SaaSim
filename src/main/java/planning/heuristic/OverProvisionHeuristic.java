package planning.heuristic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import provisioning.util.WorkloadParserFactory;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.io.HistoryBasedWorkloadParser;
import commons.io.TickSize;
import commons.io.WorkloadParser;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SaaSUsersProperties;
import commons.sim.util.SimulatorProperties;
import commons.util.Pair;

public class OverProvisionHeuristic extends JEAbstractEventHandler implements PlanningHeuristic {

	public static final double FACTOR = 0.2;//Utilisation factor according to Above the Clouds: ...
	private int maximumNumberOfServers;
	private WorkloadParser<List<Request>> workloadParser;
	private Map<Contract, Pair<Integer, String>> workloadsBeforePeak;
	
	private final long PARSER_PAGE_SIZE = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
	
	public OverProvisionHeuristic(){
		super(new JEEventScheduler());
		this.maximumNumberOfServers = 0;
	}

	@Override
	public void findPlan(HistoryBasedWorkloadParser parser,
			Provider[] cloudProviders, User[] cloudUsers) {
		
//		send(new JEEvent(JEEventType.READWORKLOAD, this, getScheduler().now(), false));
//		send(new JEEvent(JEEventType.ADD_USERS, this, getScheduler().now() + (TickSize.DAY.getTickInMillis() * 7)));
//		long[] longArray = Configuration.getInstance().getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD);
//		for(int i = 0; i < longArray.length; i++){
//			send(new JEEvent(JEEventType.PEAK, this, getScheduler().now() + ( TickSize.DAY.getTickInMillis() * longArray[i] ) ));
//		}
//		
//		workloadParser = WorkloadParserFactory.getWorkloadParser(PARSER_PAGE_SIZE);
//		maximumNumberOfServers = 0;
//		
//		getScheduler().start();
	}
	
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case READWORKLOAD:
				if(workloadParser.hasNext()) {
					List<Request> requests = workloadParser.next();
					Set<Integer> simultaneousUsers = new HashSet<Integer>();

					for(Request request : requests){
						simultaneousUsers.add(request.getUserID());
					}

					if(simultaneousUsers.size() > maximumNumberOfServers){
						maximumNumberOfServers = simultaneousUsers.size();
					}
					
					if(workloadParser.hasNext()){
						long newEventTime = getScheduler().now() + PARSER_PAGE_SIZE;
						send(new JEEvent(JEEventType.READWORKLOAD, this, newEventTime, true));
					}
				}
				break;
//			case ADD_USERS:
//				Configuration config = Configuration.getInstance();
//				double growthRate = config.getDouble(SaaSUsersProperties.SAAS_WEEK_GROWTH);
//				Map<Contract, Pair<Integer, String>> workloadsPerUser = config.getWorkloadsPerUser();
//				for(Contract contract : workloadsPerUser.keySet()){
//					Pair<Integer, String> pair = workloadsPerUser.get(contract);
//					int newNumberOfUsers = (int)Math.ceil((1+growthRate)*(pair.firstValue));
//					WorkloadParser<Request>[] newParsers = WorkloadParserFactory.createNewParsers(config.getParserPageSize().getTickInMillis(), pair.secondValue, 
//							newNumberOfUsers-pair.firstValue);
//					pair.firstValue = newNumberOfUsers;
//					this.workloadParser.addParsers(newParsers);
//				}
//				long newEventTime = getScheduler().now() + (TickSize.DAY.getTickInMillis() * 7);
//				if(newEventTime < config.getLong(SimulatorProperties.PLANNING_PERIOD) * TickSize.DAY.getTickInMillis()){
//					send(new JEEvent(JEEventType.ADD_USERS, this, newEventTime));
//				}
//				break;
//			case PEAK:
//				Configuration configuration = Configuration.getInstance();
//				workloadsBeforePeak = configuration.getWorkloadsPerUser();
//				double peakRate = configuration.getDouble(SaaSUsersProperties.SAAS_PEAK);
//				
//				for(Contract contract : workloadsBeforePeak.keySet()){
//					Pair<Integer, String> pair = workloadsBeforePeak.get(contract);
//					int newNumberOfUsers = (int)Math.ceil((1+peakRate)*(pair.firstValue));
//					WorkloadParser<Request>[] newParsers = WorkloadParserFactory.createNewParsers(configuration.getParserPageSize().getTickInMillis(), pair.secondValue, 
//							newNumberOfUsers-pair.firstValue);
//					pair.firstValue = newNumberOfUsers;
//					this.workloadParser.addParsers(newParsers);
//				}
//				send(new JEEvent(JEEventType.PEAK_END, this, getScheduler().now() + (TickSize.MONTH.getTickInMillis())));
//				break;
//			case PEAK_END:
//				Configuration.getInstance().setWorkloadsPerUser(workloadsBeforePeak);
//				this.workloadParser.clear();
//				for(Contract contract : workloadsBeforePeak.keySet()){
//					Pair<Integer, String> pair = workloadsBeforePeak.get(contract);
//					WorkloadParser<Request>[] newParsers = WorkloadParserFactory.createNewParsers(Configuration.getInstance().getParserPageSize().getTickInMillis(), pair.secondValue, 
//							pair.firstValue);
//					this.workloadParser.addParsers(newParsers);
//				}
//				break;
			default:
				break;
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
