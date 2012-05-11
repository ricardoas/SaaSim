package saasim.util;

/**
 * An abstraction to calculate cost of operations in the application.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class CostCalculus {

	/**
	 * Calculates cost of transference in the application.
	 * @param totalTransferedInBytes a double represents the total consumed transference in bytes
	 * @param limitsInConvertedUnit an array containing the limits of transference in bytes
	 * @param costsInConvertedUnit an array containing the costs of transference per byte
	 * @return A value of transference cost.
	 */
	public static double calcTransferenceCost(double totalTransferedInBytes,
			long[] limitsInConvertedUnit, double[] costsInConvertedUnit) {
		
		double transference = totalTransferedInBytes;
		double total = Math.min(transference, limitsInConvertedUnit[0]) * costsInConvertedUnit[0];
		
		for (int i = 1; i < limitsInConvertedUnit.length; i++) {
			if(transference >= limitsInConvertedUnit[i]){
				total += (limitsInConvertedUnit[i]-limitsInConvertedUnit[i-1]) * costsInConvertedUnit[i];
			}else{
				total += Math.max(0, (transference-limitsInConvertedUnit[i-1])) * costsInConvertedUnit[i];
			}
		}
		
		if(transference > limitsInConvertedUnit[limitsInConvertedUnit.length-1]){
			total += (transference - limitsInConvertedUnit[limitsInConvertedUnit.length-1]) * costsInConvertedUnit[costsInConvertedUnit.length-1];
		}
		
		return total;
	}
}
