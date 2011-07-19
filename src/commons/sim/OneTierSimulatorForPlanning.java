package commons.sim;

import java.util.ArrayList;
import java.util.List;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import config.GEISTSimpleWorkloadParser;

public class OneTierSimulatorForPlanning extends OneTierSimulator implements JEEventHandler {
	
	private List<Request> workload;
	public static long UTILIZATION_EVALUATION_PERIOD = 1000 * 60 * 5;//in millis
	
	public OneTierSimulatorForPlanning(JEEventScheduler scheduler, Monitor monitor, List<Request> workload, double sla){
		super(scheduler, monitor, new GEISTSimpleWorkloadParser(""));
		this.workload = workload;
//		this.loadBalancer = new LoadBalancer(scheduler, monitor, new RanjanScheduler());
		this.loadBalancer = new LoadBalancer(scheduler, monitor, new ProfitDrivenScheduler(sla));
	}
	
	@Override
	public void start() {
		//Scheduling first events
		getScheduler().queueEvent(new JEEvent(JEEventType.READWORKLOAD, this, new JETime(0)));
		getScheduler().queueEvent(new JEEvent(JEEventType.EVALUATEUTILIZATION, this.loadBalancer, new JETime(UTILIZATION_EVALUATION_PERIOD), UTILIZATION_EVALUATION_PERIOD));
		this.loadBalancer.initOneMachine();
		getScheduler().start();
	}
	
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case READWORKLOAD:
			if(this.workload != null && this.workload.size() > 0){
				for(Request request : this.workload){
					request.totalProcessed = 0;
					getScheduler().queueEvent(parseEvent(request));
				}
			}
			break;
		default:
			break;
		}
	}
	
	public void setOnDemandResourcesLimit(int limit){
		this.loadBalancer.setOnDemandResourcesLimit(limit);
	}
	
	public void setNumberOfReservedResources(int amount){
		this.loadBalancer.addReservedResources(amount);
	}
	
	public List<Machine> getOnDemandResources() {
		List<Machine> onDemandResources = new ArrayList<Machine>();
		onDemandResources.addAll(this.loadBalancer.onDemandMachinesPool);
		if(this.loadBalancer.servers.size() != 0){
			for(Machine machine : this.loadBalancer.servers){
				if(!machine.isReserved()){
					onDemandResources.add(machine);
				}
			}
		}
		return onDemandResources;
	}
	
	public List<Machine> getReservedResources() {
		List<Machine> reservedResources = new ArrayList<Machine>();
		reservedResources.addAll(this.loadBalancer.reservedMachinesPool);
		if(this.loadBalancer.servers.size() != 0){
			for(Machine machine : this.loadBalancer.servers){
				if(machine.isReserved()){
					reservedResources.add(machine);
				}
			}
		}
		return reservedResources;
	}
	
	//TODO: Remove from here
//	public static void main(String[] args) {
//		Map<User, List<Request>> val;
//		List<Request> workload = new ArrayList<Request>();
//		try {
//			val = next();
//			for(User user : val.keySet()){
//				workload.addAll(val.get(user));
//			}
//			OneTierSimulatorForPlanning sim = new OneTierSimulatorForPlanning(workload);
//			sim.start();
//			System.out.println(sim.loadBalancer.servers.size());
//		} catch (IOException e) {
//		}
//	}
//	
//	public static Map<User, List<Request>> next() throws IOException {
//		HashMap<User, List<Request>> currentWorkload = new HashMap<User, List<Request>>();
//		int nextMonth = Integer.MAX_VALUE;
//		
//			BufferedReader reader = new BufferedReader(new FileReader(new File("power.trc")));
//			while(reader.ready()){
//				String[] eventData = reader.readLine().trim().split("( +|\t+)+");//Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL, demand
//				Request request = new Request(eventData[0], eventData[1], eventData[3], Math.round(Double.valueOf(eventData[4])), 
//						Long.valueOf(eventData[5]), true, eventData[7], eventData[8], 1000 * 60 * 15 );
//				
//				//Adding new event to its corresponding user
//				User user = new User(eventData[1]);//Users are identified uniquely by their ids
//				List<Request> userWorkload = currentWorkload.get(user);
//				if(userWorkload == null){
//					userWorkload = new ArrayList<Request>();
//					currentWorkload.put(user, userWorkload);
//				}
//				userWorkload.add(request);
//			}
//	    return currentWorkload;
//	}
//

}
