package saasim.provisioning;

import java.io.Serializable;

import saasim.sim.util.FastSemaphore;


/**
 * 
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0 - with a setup delay.
 */
public class UrgaonkarHistory implements Serializable{

	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = -6305463166825960562L;
	
	private static final int SETUP_TICKS = 29;
	private static final int HISTORY_SIZE = 5;
	
	private double [] predLambda;
	private double [] realLambda;
	private int index;
	
	private FastSemaphore setUp;
	
	public UrgaonkarHistory() {
		predLambda = new double[HISTORY_SIZE];
		realLambda = new double[HISTORY_SIZE];
		index = 0;
		setUp = new FastSemaphore(SETUP_TICKS);
	}
	
	public double applyError(double lambdaPred){
		double error = 0;
		for (int i = 0; i < predLambda.length; i++) {
			error += Math.max(0, realLambda[i]-predLambda[i])/HISTORY_SIZE;
		}
		if(setUp.tryAcquire()){
			return lambdaPred;
		}
		lambdaPred *= (1+error);
		predLambda[index] = lambdaPred;
		index = ++index % predLambda.length;
		return lambdaPred;
	}

	public void update(double arrivalRate) {
		realLambda[(index + HISTORY_SIZE - 1) % HISTORY_SIZE] = arrivalRate;
	}
	
}