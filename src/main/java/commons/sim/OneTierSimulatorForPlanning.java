package commons.sim;


//@Deprecated
//public class OneTierSimulatorForPlanning extends SimpleSimulator implements JEEventHandler {
//	
//	private List<Request> workload;
//	public static long UTILIZATION_EVALUATION_PERIOD = 1000 * 60 * 5;//in millis
//	
//	public OneTierSimulatorForPlanning(JEEventScheduler scheduler, Monitor monitor, List<Request> workload, 
//			double sla, List<Machine> setUpMachines){
//		super(scheduler, monitor, new GEISTSimpleWorkloadParser(), setUpMachines);
//		this.workload = workload;// FIXME por que workload direto e não o parser? Vai ter como ler toda a workload antes?
////		this.loadBalancer = new LoadBalancer(scheduler, monitor, new RanjanScheduler()); TODO acho que essas linhas podem ser deletadas.
////		this.loadBalancer = new LoadBalancer(scheduler, monitor, new ProfitDrivenScheduler(sla));
//	}
//	
//	@Override
//	protected void prepareBeforeStart() {
//		super.prepareBeforeStart();
//		send(new JEEvent(JEEventType.EVALUATEUTILIZATION, this.loadBalancer, new JETime(UTILIZATION_EVALUATION_PERIOD), UTILIZATION_EVALUATION_PERIOD));
//	}
//	
//	@Override
//	public void handleEvent(JEEvent event) {
//		switch (event.getType()) {
//		case READWORKLOAD:
//			if(this.workload != null && this.workload.size() > 0){
//				for(Request request : this.workload){
//					request.totalProcessed = 0;
//					getScheduler().queueEvent(parseEvent(request));
//				}
//			}
//			break;
//		default:
//			break;
//		}
//	}
//	
//	public void setOnDemandResourcesLimit(int limit){
//		this.loadBalancer.setOnDemandResourcesLimit(limit);
//	}
//	
//	public void setNumberOfReservedResources(int amount){
//		this.loadBalancer.addReservedResources(amount);
//	}
//	
//	public List<Machine> getOnDemandResources() {
//		List<Machine> onDemandResources = new ArrayList<Machine>();
//		onDemandResources.addAll(this.loadBalancer.onDemandMachinesPool);
//		if(this.loadBalancer.getServers().size() != 0){
//			for(Machine machine : this.loadBalancer.getServers()){
//				if(!machine.isReserved()){
//					onDemandResources.add(machine);
//				}
//			}
//		}
//		return onDemandResources;
//	}
//	
//	public List<Machine> getReservedResources() {
//		List<Machine> reservedResources = new ArrayList<Machine>();
//		reservedResources.addAll(this.loadBalancer.reservedMachinesPool);
//		if(this.loadBalancer.getServers().size() != 0){
//			for(Machine machine : this.loadBalancer.getServers()){
//				if(machine.isReserved()){
//					reservedResources.add(machine);
//				}
//			}
//		}
//		return reservedResources;
//	}
//}
