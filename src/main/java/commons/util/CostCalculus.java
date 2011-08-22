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
		double transferenceLeft = (1.0*totalTransferedInBytes)/conversion;
		int currentIndex = 0;
		double total = 0;
		while(transferenceLeft != 0 && currentIndex != limitsInConvertedUnit.length){
			if(transferenceLeft <= limitsInConvertedUnit[currentIndex]){
				total += transferenceLeft * costsInConvertedUnit[currentIndex];
				transferenceLeft = 0;
			}else{
				total += limitsInConvertedUnit[currentIndex] * costsInConvertedUnit[currentIndex];
				transferenceLeft -= limitsInConvertedUnit[currentIndex]; 
			}
			currentIndex++;
		}
		
		if(transferenceLeft != 0){
			total += transferenceLeft * costsInConvertedUnit[currentIndex];
			transferenceLeft = 0; 
		}
		return total;
	}

}
