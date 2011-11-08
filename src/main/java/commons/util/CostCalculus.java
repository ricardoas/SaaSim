/**
 * 
 */
package commons.util;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class CostCalculus {

	/**
	 * @param totalTransferedInBytes
	 * @param limitsInConvertedUnit
	 * @param costsInConvertedUnit
	 * @return
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
