package saasim.ext.cloud;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum InstanceType {

	M1_SMALL	(1, 	1), 
	M1_LARGE	(2, 	2), 
	M1_XLARGE	(2, 	4), 
	C1_MEDIUM	(2.5, 	2), 
	C1_XLARGE	(2.5, 	8), 
	M2_XLARGE	(3.25, 	2), 
	M2_2XLARGE	(3.25, 	4), 
	M2_4XLARGE	(3.25, 	4), 
	T1_MICRO	(2, 	1);

	private final double cpuPower;
	private final int numberOfCPU;

	/**
	 * Default constructor.
	 * 
	 * @param power Core relative power
	 * @param numberOfCores Number of identical cores.
	 */
	private InstanceType(double power, int numberOfCores) {
		this.cpuPower = power;
		this.numberOfCPU = numberOfCores;
	}

	/**
	 * @return the relative power of each core.
	 */
	public double getPower() {
		return cpuPower;
	}

	/**
	 * @return the number of cores
	 */
	public int getNumberOfCores() {
		return numberOfCPU;
	}

	/**
	 * @return Relative power of machine types
	 */
	public double getRelativePower() {
		return getPower() * getNumberOfCores();
	}
}