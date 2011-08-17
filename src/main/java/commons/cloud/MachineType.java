package commons.cloud;


/**
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 *
 */
public class MachineType {
	
	private final MachineTypeValue value;
	private final double onDemandCpuCost;
	private final double reservedCpuCost;
	private final long reservationOneYearFee;
	private final long reservationThreeYearsFee;
	
	/**
	 * Default constructor.
	 * @param value
	 * @param onDemandCpuCost
	 * @param reservedCpuCost
	 * @param reservationOneYearFee
	 * @param reservationThreeYearsFee
	 */
	public MachineType(MachineTypeValue value, double onDemandCpuCost,
			double reservedCpuCost, long reservationOneYearFee,
			long reservationThreeYearsFee) {
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

	public long getReservationOneYearFee() {
		return reservationOneYearFee;
	}

	public long getReservationThreeYearsFee() {
		return reservationThreeYearsFee;
	}
}
