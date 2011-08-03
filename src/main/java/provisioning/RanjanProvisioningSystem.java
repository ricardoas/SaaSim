package provisioning;

import java.util.Iterator;

import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.provisioningheuristics.RanjanStatistics;
import commons.sim.util.MachineFactory;

public class RanjanProvisioningSystem extends DynamicProvisioningSystem {

	private double TARGET_UTILIZATION = 0.66;
	public static long UTILIZATION_EVALUATION_PERIOD_IN_MILLIS = 1000 * 60 * 5;//in millis

	public RanjanProvisioningSystem(JEEventScheduler scheduler) {
		super(scheduler);
	}
	
	@Override
	protected void handleEventEvaluateUtilization(JEEvent event) {
		RanjanStatistics statistics = (RanjanStatistics) event.getValue()[0];
		long numberOfServersToAdd = evaluateNumberOfServersForNextInterval(statistics);
		if(numberOfServersToAdd > 0){
			for(int i = 0; i < numberOfServersToAdd; i++){
				evaluateMachinesToBeAdded();
			}
		}else if(numberOfServersToAdd < 0){
			//Removing on demand machines first
			Iterator<Long> iterator = this.accountingSystem.getOnDemandMachinesData().keySet().iterator();
			while(numberOfServersToAdd < 0 && iterator.hasNext()){
				long serverID = iterator.next();
				this.configurable.removeServer(0, serverID, false);
				this.accountingSystem.reportMachineFinish(serverID, getScheduler().now().timeMilliSeconds);
				numberOfServersToAdd++;
			}
			//Removing reserved machines
			iterator = this.accountingSystem.getReservedMachinesData().keySet().iterator();
			while(numberOfServersToAdd < 0 && iterator.hasNext()){
				long serverID = iterator.next();
				this.configurable.removeServer(0, serverID, false);
				this.accountingSystem.reportMachineFinish(serverID, getScheduler().now().timeMilliSeconds);
				numberOfServersToAdd++;
			}
		}
	}

	public long evaluateNumberOfServersForNextInterval(RanjanStatistics statistics) {
		double averageUtilization = statistics.totalUtilizationInLastInterval / statistics.totalNumberOfServers;
		double d;
		if(statistics.numberOfRequestsCompletionsInLastInterval == 0){
			d = averageUtilization;
		}else{
			d = averageUtilization / statistics.numberOfRequestsCompletionsInLastInterval;
		}
		
		double u_lign = Math.max(statistics.numberOfRequestsArrivalInLastInterval, statistics.numberOfRequestsCompletionsInLastInterval) * d;
		long newNumberOfServers = (int)Math.ceil( statistics.totalNumberOfServers * u_lign / TARGET_UTILIZATION );
		
		long numberOfServersToAdd = (newNumberOfServers - statistics.totalNumberOfServers);
		if(numberOfServersToAdd != 0){
			return numberOfServersToAdd;
		}else{
			if(statistics.numberOfRequestsArrivalInLastInterval > 0 && 
					statistics.totalNumberOfServers == 0){
				return 1l;
			}
			return numberOfServersToAdd;
		}
	}
	
	private void evaluateMachinesToBeAdded() {
		boolean canAddAReservedMachine = this.accountingSystem.canAddAReservedMachine();
		boolean canAddAOnDemandMachine = this.accountingSystem.canAddAOnDemandMachine();
		MachineFactory machineFactory = MachineFactory.getInstance();
		
		if(canAddAReservedMachine){
			this.configurable.addServer(0, machineFactory.createMachine(getScheduler(), availableIDs++, canAddAReservedMachine));
			//Registering machines for accounting
			this.accountingSystem.createMachine(availableIDs-1, canAddAReservedMachine, getScheduler().now().timeMilliSeconds);
		}else if(canAddAOnDemandMachine){
			this.configurable.addServer(0, machineFactory.createMachine(getScheduler(), availableIDs++, canAddAOnDemandMachine));
			//Registering machines for accounting
			this.accountingSystem.createMachine(availableIDs-1, canAddAReservedMachine, getScheduler().now().timeMilliSeconds);
		}
	}
}
