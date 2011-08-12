package commons.sim.schedulingheuristics;

import java.util.List;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.components.Machine;
import commons.sim.components.ProcessorSharedMachine;
import commons.util.Triple;

public class ProfitDrivenHeuristic implements SchedulingHeuristic{
	
	private double sla;
	
	public ProfitDrivenHeuristic(){
		Configuration config = Configuration.getInstance();
		this.sla = config.getSLA();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Machine getNextServer(Request request, List<Machine> servers) {
		double max_diff = 0d;
		Machine machineToSchedule = null;
		
		for(Machine machine : servers){//Evaluating if it is profitable to reuse existing machines
			double pi = 0d;
			double new_pi = 0d;
			List<Triple<Long, Long, Long>> executionTimes = machine.estimateFinishTime(request);
			boolean continueToOtherMachine = false;
			
			for(Triple<Long, Long, Long> triple : executionTimes){//Evaluating requests times
				double aad = Math.max(this.sla - triple.thirdValue, 0);
				double asad = aad * (triple.thirdValue)/(triple.thirdValue);
				double clft = triple.firstValue + asad;
				
				if(triple.secondValue > clft){//New request will delay previous requests
					continueToOtherMachine = true;
					break;
				}
				pi += (clft - triple.firstValue);
				new_pi += (clft - triple.secondValue);
			}
			
			if(continueToOtherMachine){//New request will delay previous requests, continue to next machine
				continue;
			}
			
			double aad = this.sla - request.getDemand();
			double asad = aad * (request.getDemand())/(request.getDemand());
			double clft = asad;//Simplification
			new_pi += (clft);
			
			if(new_pi > pi){//Scheduling new request is profitable
				double difference = new_pi - pi;
				if(difference > max_diff){
					max_diff = difference;
					machineToSchedule = machine;
				}
			}
		}
		
		return machineToSchedule;
	}

	@Override
	public long getRequestsArrivalCounter() {
		return 0;
	}

	@Override
	public long getFinishedRequestsCounter() {
		return 0;
	}

	@Override
	public void resetCounters() {
	}

	@Override
	public void reportRequestFinished() {
	}
}
