package commons.sim.components;


public class MachineDescriptor {
	
	private final long machineID;

	/**
	 * @param machineID
	 */
	public MachineDescriptor(long machineID) {
		this.machineID = machineID;
	}

	public long getMachineID() {
		return machineID;
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
