package commons.cloud;


/**
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 *
 */
public class MachineType {
	
	private final MachineTypeValue value;
	private final double onDemandCpuCost;
	private final double reservedCpuCost;
	private final double reservationOneYearFee;
	private final double reservationThreeYearsFee;
	
	/**
	 * Default constructor.
	 * @param value
	 * @param onDemandCpuCost
	 * @param reservedCpuCost
	 * @param reservationOneYearFee
	 * @param reservationThreeYearsFee
	 */
	public MachineType(MachineTypeValue value, double onDemandCpuCost,
			double reservedCpuCost, double reservationOneYearFee,
			double reservationThreeYearsFee) {
		this.value = value;
		this.onDemandCpuCost = onDemandCpuCost;
		this.reservedCpuCost = reservedCpuCost;
		this.reservationOneYearFee = reservationOneYearFee;
		this.reservationThreeYearsFee = reservationThreeYearsFee;
	}

	public MachineTypeValue getValue() {
		return value;
	}

	public double getOnDemandCpuCost() {
		return onDemandCpuCost;
	}

	public double getReservedCpuCost() {
		return reservedCpuCost;
	}

	public double getReservationOneYearFee() {
		return reservationOneYearFee;
	}

	public double getReservationThreeYearsFee() {
		return reservationThreeYearsFee;
	}
}
