package provisioning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import commons.sim.provisioningheuristics.MachineStatistics;

public class UrgaonkarStatistics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168788545374488566L;
	private double averageST;
	private double varST;
	private double varIAT;
	private double[] arrivalRate;
	private static int PREDICTION_WINDOW_SIZE = 100;
	
	private Percentile percentile;
	
	private int index;
	private double lambdaPeak;
	private double maxRT;
	private long predictionTick;
	
	public UrgaonkarStatistics(double maxRT, long predictionTick) {
		this.maxRT = maxRT;
		this.predictionTick = predictionTick;
		index = 0;
		averageST = 0;//new double[PREDICTION_WINDOW_SIZE];
		varST = 0;//new double[PREDICTION_WINDOW_SIZE];
		varIAT = 0;//new double[PREDICTION_WINDOW_SIZE];
		arrivalRate = new double[PREDICTION_WINDOW_SIZE];
		percentile = new Percentile(60);
	}
	
	/**
	 * Predictive tick statistics
	 * @param statistics
	 */
	public void add(MachineStatistics statistics) {
		averageST = statistics.averageST;
		varST = statistics.calcVarST();
		varIAT = statistics.calcVarIAT();
//		averageST[index] += statistics.averageST;
//		varST[index] += statistics.calcVarST();
//		varIAT[index] += statistics.calcVarIAT();
		arrivalRate[index] += statistics.getArrivalRateInTier(predictionTick);
		index = ++index % arrivalRate.length;
		
		lambdaPeak = (getAverageST() + (getVarST() + getVarIAT())/(2 * (1.0*maxRT - getAverageST())));
		
		lambdaPeak = 1.0/lambdaPeak;

	}

	public double getAverageST() {
		return averageST;//percentile.evaluate(averageST);
	}

	public double getVarST() {
		return varST;// percentile.evaluate(varST);
	}

	public double getVarIAT() {
		return varIAT;//percentile.evaluate(varIAT);
	}

	public double getPredArrivalRate() {
		return percentile.evaluate(arrivalRate);
	}
	
	public double getCurrentArrivalRate() {
		return arrivalRate[(arrivalRate.length + index - 1) % arrivalRate.length];
	}
	
	public double calcLambdaPeak() {
		return lambdaPeak;
	}
}