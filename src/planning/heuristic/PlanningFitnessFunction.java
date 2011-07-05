package planning.heuristic;

import java.util.List;
import java.util.Map;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import commons.cloud.Contract;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityFunction;
import commons.scheduling.ProfitDrivenScheduler;

public class PlanningFitnessFunction extends FitnessFunction{
	
	private ProfitDrivenScheduler scheduler;
	private final Map<User, Contract> cloudUsers;
	private UtilityFunction utilityFunction;
	private final double sla;
	
	public PlanningFitnessFunction(Map<User, List<Request>> currentWorkload, Map<User, Contract> cloudUsers, double sla){
		this.cloudUsers = cloudUsers;
		this.sla = sla;
		this.scheduler = new ProfitDrivenScheduler(currentWorkload);
		this.utilityFunction = new UtilityFunction();
	}
	
	@Override
	protected double evaluate(IChromosome a_subject) {
		// TODO Call scheduling and evaluate achieved profit!
		double fitness = 0;
		
		if(fitness < 1){
			return 1;
		}
		return fitness;
	}

}
