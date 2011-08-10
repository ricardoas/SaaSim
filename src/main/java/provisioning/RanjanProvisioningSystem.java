package provisioning;

import java.util.Iterator;

import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.provisioningheuristics.RanjanStatistics;

/**
 * This class represents the DPS business logic according to RANJAN. Here some statistics of current
 * available machines (i.e, utilisation) is used to verify if new machines need to be added to 
 * an application tier, or if some machines can be removed from any application tier.
 * @author davidcmm
 *
 */
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
				this.configurable.removeServer(0, new MachineDescriptor(serverID, false, 0), false);
				this.accountingSystem.reportMachineFinish(serverID, getScheduler().now().timeMilliSeconds);
				numberOfServersToAdd++;
			}
			//Removing reserved machines
			iterator = this.accountingSystem.getReservedMachinesData().keySet().iterator();
			while(numberOfServersToAdd < 0 && iterator.hasNext()){
				long serverID = iterator.next();
				this.configurable.removeServer(0, new MachineDescriptor(serverID, true, 0), false);
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
		
		if(canAddAReservedMachine){
			MachineDescriptor descriptor = new MachineDescriptor(availableIDs++, canAddAReservedMachine, getScheduler().now().timeMilliSeconds);
			this.configurable.addServer(0, descriptor);
			//Registering machines for accounting
			this.accountingSystem.createMachine(descriptor);
		}else if(canAddAOnDemandMachine){
			MachineDescriptor descriptor = new MachineDescriptor(availableIDs++, canAddAReservedMachine, getScheduler().now().timeMilliSeconds);
			this.configurable.addServer(0, descriptor);
			//Registering machines for accounting
			this.accountingSystem.createMachine(descriptor);
		}
	}
}
