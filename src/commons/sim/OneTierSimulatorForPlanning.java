package commons.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Machine;
import commons.cloud.Request;
import commons.cloud.User;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

public class OneTierSimulatorForPlanning extends OneTierSimulator {
	
	private List<Request> workload;
	public static long UTILIZATION_EVALUATION_PERIOD = 1000 * 60 * 5;//in millis
	
	public OneTierSimulatorForPlanning(List<Request> workload){
		this.workload = workload;
		this.loadBalancer = new LoadBalancer(new RanjanScheduler());
	}
	
	@Override
	public void start() {
		//Scheduling first events
		JEEventScheduler.SCHEDULER.queueEvent(new JEEvent(JEEventType.READWORKLOAD, this, new JETime(0), null));
		JEEventScheduler.SCHEDULER.queueEvent(new JEEvent(JEEventType.EVALUATEUTILIZATION, this.loadBalancer, new JETime(UTILIZATION_EVALUATION_PERIOD), UTILIZATION_EVALUATION_PERIOD));
		this.loadBalancer.initOneMachine();
		JEEventScheduler.SCHEDULER.start();
	}
	
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case READWORKLOAD:
			if(this.workload != null & this.workload.size() > 0){
				for(Request request : this.workload){
					JEEventScheduler.SCHEDULER.queueEvent(parseEvent(request));
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
	
	public static void main(String[] args) {
		Map<User, List<Request>> val;
		List<Request> workload = new ArrayList<Request>();
		try {
			val = next();
			for(User user : val.keySet()){
				workload.addAll(val.get(user));
			}
			OneTierSimulatorForPlanning sim = new OneTierSimulatorForPlanning(workload);
			sim.start();
			System.out.println(sim.loadBalancer.servers.size());
		} catch (IOException e) {
		}
	}
	
	public static Map<User, List<Request>> next() throws IOException {
		HashMap<User, List<Request>> currentWorkload = new HashMap<User, List<Request>>();
		int nextMonth = Integer.MAX_VALUE;
		
			BufferedReader reader = new BufferedReader(new FileReader(new File("power.trc")));
			while(reader.ready()){
				String[] eventData = reader.readLine().trim().split("( +|\t+)+");//Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL, demand
				Request request = new Request(eventData[0], eventData[1], eventData[3], Math.round(Double.valueOf(eventData[4])), 
						Long.valueOf(eventData[5]), true, eventData[7], eventData[8], 1000 * 60 * 15 );
				
				//Adding new event to its corresponding user
				User user = new User(eventData[1]);//Users are identified uniquely by their ids
				List<Request> userWorkload = currentWorkload.get(user);
				if(userWorkload == null){
					userWorkload = new ArrayList<Request>();
					currentWorkload.put(user, userWorkload);
				}
				userWorkload.add(request);
			}
	    return currentWorkload;
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
}
