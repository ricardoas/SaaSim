package commons.util;

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
	
	public static void main(String[] args) {
		
		
		double cost = CostCalculus.calcTransferenceCost(1987920692670L/Math.pow(2, 30), 
				new long []{1,10240,51200,153600}, 
				new double[]{0,0.12,0.09,0.07,0.05});
		System.out.println(cost);

		cost = CostCalculus.calcTransferenceCost(1987920692670L, 
				DataUnit.convert(new long []{1,10240,51200,153600}, DataUnit.GB, DataUnit.B), 
				DataUnit.convert(new double[]{0,0.12,0.09,0.07,0.05}, DataUnit.B, DataUnit.GB));
		System.out.println(cost);
		
		System.out.println(1987920692670L/Math.pow(2, 30));
		
		cost = CostCalculus.calcTransferenceCost(1987920692670L/Math.pow(2, 20), 
				new long []{46080}, 
				new double[]{0,0.005});
		System.out.println(cost);
		
		cost = CostCalculus.calcTransferenceCost(1987920692670L, 
				DataUnit.convert(new long []{46080}, DataUnit.MB, DataUnit.B), 
				DataUnit.convert(new double[]{0,0.005}, DataUnit.B, DataUnit.MB));
		System.out.println(cost);
		
		
}

}
