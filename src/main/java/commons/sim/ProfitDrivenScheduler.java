package commons.sim;

import java.util.List;

import commons.cloud.Request;
import commons.sim.components.Machine;
import commons.sim.schedulingheuristics.SchedulingHeuristic;
import commons.util.Triple;

public class ProfitDrivenScheduler implements SchedulingHeuristic {

	private final double sla;

	public ProfitDrivenScheduler(double sla){
		this.sla = sla;
	}
	
	@Override
	public Machine getNextServer(Request request, List<Machine> servers) {
		double max_diff = 0d;
		Machine machineToSchedule = null;
		
		for(Machine machine : servers){//Evaluating if it is profitable to reuse existing machines
			double pi = 0d;
			double new_pi = 0d;
			List<Triple<Long, Long, Long>> executionTimes = machine.calcExecutionTimesWithNewRequest(request, this.sla);
			boolean continueToOtherMachine = false;
			
			for(Triple<Long, Long, Long> pair : executionTimes){//Evaluating requests times
				double aad = Math.max(this.sla - pair.thirdValue, 0);
				double asad = aad * (pair.thirdValue)/(pair.thirdValue);
				double clft = pair.firstValue + asad;
				
				if(pair.secondValue > clft){//New request will delay previous requests
					continueToOtherMachine = true;
					break;
				}
				pi += (clft - pair.firstValue);
				new_pi += (clft - pair.secondValue);
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
	public double evaluateUtilization(List<Machine> servers, Long eventTime) {
		return 0;
	}
}
