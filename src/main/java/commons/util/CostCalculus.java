/**
 * 
 */
package commons.util;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class CostCalculus {

	private static final long GB_IN_BYTES = 1024 * 1024 * 1024;

	/**
	 * @param totalTransfered
	 * @param limits
	 * @param costs
	 * @return
	 */
	public static double calcTransferenceCost(long totalTransfered,
			long[] limits, double[] costs) {
		double transferenceLeft = (1.0*totalTransfered)/GB_IN_BYTES;
		int currentIndex = 0;
		double total = 0;
		while(transferenceLeft != 0 && currentIndex != limits.length){
			if(transferenceLeft <= limits[currentIndex]){
				total += transferenceLeft * costs[currentIndex];
				transferenceLeft = 0;
			}else{
				total += limits[currentIndex] * costs[currentIndex];
				transferenceLeft -= limits[currentIndex]; 
			}
			currentIndex++;
		}
		
		if(transferenceLeft != 0){
			total += limits[currentIndex] * costs[currentIndex];
			transferenceLeft = 0; 
		}
		return total;
	}

}
