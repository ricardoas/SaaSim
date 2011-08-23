/**
 * 
 */
package commons.util;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class CostCalculus {

	public static final long GB_IN_BYTES = 1024 * 1024 * 1024;
	public static final long MB_IN_BYTES = 1024 * 1024;

	/**
	 * @param totalTransferedInBytes
	 * @param limitsInConvertedUnit
	 * @param costsInConvertedUnit
	 * @return
	 */
	public static double calcTransferenceCost(long totalTransferedInBytes,
			long[] limitsInConvertedUnit, double[] costsInConvertedUnit, long conversion) {
		
		double transference = (1.0*totalTransferedInBytes)/conversion;
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
