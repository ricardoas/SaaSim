package commons.sim.components;


/**
 * Machine information.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MachineDescriptor {
	
	private final long machineID;
	private final boolean reserved;
	private long startTimeInMillis;
	private long finishTimeInMillis;

	/**
	 * Default constructor.
	 * @param machineID Machine unique ID.
	 * @param reserved A {@link Boolean} value indicating if this machine has been bought on reservation market.
	 */
	public MachineDescriptor(long machineID, boolean reserved) {
		this.machineID = machineID;
		this.reserved = reserved;
	}

	public long getMachineID() {
		return machineID;
	}
	
	public boolean isReserved() {
		return reserved;
	}
	
	public long getStartTimeInMillis() {
		return startTimeInMillis;
	}
	
	public void setStartTimeInMillis(long startTimeInMillis) {
		this.startTimeInMillis = startTimeInMillis;
	}
	
	public long getFinishTimeInMillis() {
		return finishTimeInMillis;
	}

	public void setFinishTimeInMillis(long finishTimeInMillis) {
		this.finishTimeInMillis = finishTimeInMillis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (machineID ^ (machineID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MachineDescriptor other = (MachineDescriptor) obj;
		if (machineID != other.machineID)
			return false;
		return true;
	}
}
