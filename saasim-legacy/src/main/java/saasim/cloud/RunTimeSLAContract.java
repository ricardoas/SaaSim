package saasim.cloud;

public class RunTimeSLAContract extends Contract{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1894420900167755298L;
	
	public RunTimeSLAContract(String planName, int priority, double setupCost,
			double price, long cpuLimitInMillis, double extraCpuCostPerMillis,
			long[] transferenceLimitsInBytes,
			double[] transferenceCostsPerBytes, long storageLimitInBytes,
			double storageCostPerBytes) {
		super(planName, priority, setupCost, price, cpuLimitInMillis,
				extraCpuCostPerMillis, transferenceLimitsInBytes,
				transferenceCostsPerBytes, storageLimitInBytes, storageCostPerBytes);
	}
	
	
	@Override
	public double calculatePenalty(double slaInfractionPercentage, double unavailability) {
		
		return super.calculatePenalty(unavailability, 0);
	}
	

}
