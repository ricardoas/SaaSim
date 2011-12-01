package provisioning;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import commons.sim.provisioningheuristics.MachineStatistics;

public class UrgaonkarStatistics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168788545374488566L;

	
	private double[] arrivalRate;
	
	private Percentile percentile;
	
	private int index;
	private double lambdaPeak;
	private double maxRT;
	private long predictionTick;
	
	/**
	 * Default constructor
	 * @param maxRT
	 * @param predictionTick
	 * @param windowSize 
	 */
	public UrgaonkarStatistics(double maxRT, long predictionTick, double p, int windowSize) {
		this.maxRT = maxRT;
		this.predictionTick = predictionTick;
		index = 0;
		arrivalRate = new double[windowSize];
		percentile = new Percentile(p);
	}
	
	/**
	 * Predictive tick statistics
	 * @param statistics
	 */
	public void add(MachineStatistics statistics) {
		
		arrivalRate[index++ % arrivalRate.length] = statistics.getArrivalRateInTier(predictionTick);
		
		lambdaPeak = (statistics.averageST + (statistics.calcVarST() + statistics.calcVarIAT())/(2 * (1.0*maxRT - statistics.averageST)));
		
		lambdaPeak = 1.0/lambdaPeak;

	}

	public double getPredArrivalRate() {
		if(index == 0){
			return 0;
		}
		return percentile.evaluate(index < arrivalRate.length? Arrays.copyOf(arrivalRate, index): arrivalRate);
	}
	
	public double getCurrentArrivalRate() {
		return arrivalRate[(arrivalRate.length + (index % arrivalRate.length) - 1) % arrivalRate.length];
	}
	
	public double getPeakArrivalRatePerServer() {
		return lambdaPeak;
	}
}