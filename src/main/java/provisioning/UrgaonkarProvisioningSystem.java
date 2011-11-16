package provisioning;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import commons.config.Configuration;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;
import commons.util.TimeUnit;

public class UrgaonkarProvisioningSystem extends DynamicProvisioningSystem {
	
	private class UrgaonkarStatistics implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -3168788545374488566L;
		private double[] averageST;
		private double[] varRT;
		private double[] varIAT;
		private double[] arrivalRate;
		
		private Percentile percentile;
		
		private int index;
		private double lambda_pred;
		
		public UrgaonkarStatistics() {
			index = 0;
			averageST = new double[7];
			varRT = new double[7];
			varIAT = new double[7];
			arrivalRate = new double[7];
			percentile = new Percentile(95);
		}
		
		/**
		 * reactive tick statistics
		 * @param statistics
		 */
		public void update(MachineStatistics statistics) {
			averageST[index] += statistics.averageST;
			varRT[index] += statistics.varRT;
			varIAT[index] += statistics.varIAT;
			arrivalRate[index] += statistics.arrivalRate;
		}
		
		/**
		 * Predictive tick statistics
		 * @param statistics
		 */
		public void add(MachineStatistics statistics) {
			update(statistics);
			
			lambda_pred = 1.0/(getAverageST() + (getVarRT() + getVarIAT())/(2 * (averageRT - getAverageST())));

			index = ++index % averageST.length;
		}

		public double getAverageST() {
			return percentile.evaluate(averageST);
		}

		public double getVarRT() {
			return percentile.evaluate(varRT);
		}

		public double getVarIAT() {
			return percentile.evaluate(varIAT);
		}

		public double getArrivalRate() {
			return percentile.evaluate(arrivalRate);
		}
		
		public double getCurrentArrivalRate() {
			return arrivalRate[index - 1];
		}
		
		public double calcLambdaPred() {
			return lambda_pred;
		}
	}
	
	private class UrgaonkarHistory implements Serializable{

		private static final int HISTORY_SIZE = 5;
		
		private double [] predLambda;
		private double [] readLambda;
		private int index;
		
		public UrgaonkarHistory() {
			predLambda = new double[HISTORY_SIZE];
			readLambda = new double[HISTORY_SIZE];
			index = 0;
		}
		
		public double applyError(double lambdaPred){
			double error = 0;
			for (int i = 0; i < predLambda.length; i++) {
				error += Math.max(0, readLambda[i]-predLambda[i])/HISTORY_SIZE;
			}
			return lambdaPred * (1+error);
		}

		public void update(double arrivalRate) {
			readLambda[index] = arrivalRate;
		}
		
	}
	
	private static final String PROP_ENABLE_PREDICTIVE = "dps.urgaonkar.predictive";
	private static final String PROP_ENABLE_REACTIVE = "dps.urgaonkar.reactive";
	
	private boolean enablePredictive;
	private boolean enableReactive;
	private long averageRT;
	
	private UrgaonkarStatistics [] stat;
	private UrgaonkarHistory last;

	public UrgaonkarProvisioningSystem() {
		super();
		enablePredictive = Configuration.getInstance().getBoolean(PROP_ENABLE_PREDICTIVE, true);
		enableReactive = Configuration.getInstance().getBoolean(PROP_ENABLE_REACTIVE, true);
		averageRT = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
		stat = new UrgaonkarStatistics[24];
		for (int i = 0; i < stat.length; i++) {
			stat[i] = new UrgaonkarStatistics();
		}
		last = new UrgaonkarHistory();
	}
	
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		boolean predictiveRound = now % TimeUnit.HOUR.getMillis() == 0;
		
		int numberOfServersToAdd = 0;
		
		if(predictiveRound){
			
			int index = (int)((now%TimeUnit.DAY.getMillis())/TimeUnit.HOUR.getMillis());
			UrgaonkarStatistics currentStat = stat[index];
			currentStat.add(statistics);
			
			last.update(currentStat.getCurrentArrivalRate());
			
			double lambda_pred = currentStat.calcLambdaPred();
			lambda_pred = last.applyError(lambda_pred);
			
			numberOfServersToAdd = (int) Math.ceil(currentStat.getArrivalRate()/lambda_pred) - statistics.totalNumberOfServers;
			
			log.info(String.format("STAT-URGAONKAR PRED %d %d %d %s", now, tier, numberOfServersToAdd, statistics));
		}else{

			int index = (int)((now%TimeUnit.DAY.getMillis())/TimeUnit.HOUR.getMillis());
			UrgaonkarStatistics currentStat = stat[index];
			currentStat.update(statistics);
			
			//FIXME complete with peak handling

			log.info(String.format("STAT-URGAONKAR REAC %d %d %d %s", now, tier, numberOfServersToAdd, statistics));
		}
		
		if(numberOfServersToAdd > 0){
			
			if(numberOfServersToAdd > statistics.warmingDownMachines){
				numberOfServersToAdd -= statistics.warmingDownMachines;
				List<MachineDescriptor> machines = buyMachines(numberOfServersToAdd);
				for (MachineDescriptor machineDescriptor : machines) {
					configurable.addMachine(tier, machineDescriptor, true);
				}
				
				configurable.cancelMachineRemoval(tier, statistics.warmingDownMachines);
			}else{
				configurable.cancelMachineRemoval(tier, numberOfServersToAdd);
			}
			
		}else if(numberOfServersToAdd < 0){
			for (int i = 0; i < -numberOfServersToAdd; i++) {
				configurable.removeMachine(tier, false);
			}
		}

		
	}
	
	private Calendar getTime(long now){
		Calendar instance = new GregorianCalendar(2009, 0, 0, (int)((now%TimeUnit.DAY.getMillis())/TimeUnit.HOUR.getMillis()), 0, 0);
		instance.add(Calendar.DAY_OF_YEAR, (int)(now/TimeUnit.DAY.getMillis()));
		
		return instance;
	}
	


}
