package commons.cloud;


/**
 * These are the possible values for machine types, currently based on amazon EC2 instance types.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum MachineType{

	T1_MICRO(2, 1),
	M1_SMALL(1, 1), 
	M1_LARGE(2, 2), 
	C1_MEDIUM(2.5, 2), 
	M1_XLARGE(2, 4), 
	M2_XLARGE(3.25, 2), M2_2XLARGE(3.25, 4), M2_4XLARGE(3.25, 4),
	C1_XLARGE(2.5, 8);
	
	private final double power;
	private final int numberOfCores;
	
	/**
	 * Default constructor.
	 * @param power Core relative power
	 * @param numberOfCores Number of identical cores.
	 */
	private MachineType(double power, int numberOfCores) {
		this.power = power;
		this.numberOfCores = numberOfCores;
	}

	/**
	 * @return the power
	 */
	public double getPower() {
		return power;
	}

	/**
	 * @return the numberOfCores
	 */
	public int getNumberOfCores() {
		return numberOfCores;
	}
	
	
	
};
