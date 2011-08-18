package commons.sim.components;

import commons.cloud.MachineType;

/**
 * Machine information.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MachineDescriptor {
	
	private final long machineID;
	private MachineType type;
	private final boolean reserved;
	private long startTimeInMillis;
	private long finishTimeInMillis;
	private long inTransference;
	private long outTransference;
	
	private double costPayed;
	private long inTransferencePayed;
	private long outTransferencePayed;

	/**
	 * Default constructor.
	 * @param machineID Machine unique ID.
	 * @param reserved A {@link Boolean} value indicating if this machine has been bought on reservation market.
	 * @param type TODO
	 */
	public MachineDescriptor(long machineID, boolean reserved, MachineType type) {
		this.machineID = machineID;
		this.reserved = reserved;
		this.type = type;
	}

	public MachineType getType(){
		return this.type;
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

	/**
	 * @return the inTransference
	 */
	public long getInTransference() {
		return inTransference;
	}

	/**
	 * @param inTransference the inTransference to set
	 */
	public void setInTransference(long inTransference) {
		this.inTransference = inTransference;
	}

	/**
	 * @return the outTransference
	 */
	public long getOutTransference() {
		return outTransference;
	}

	/**
	 * @param outTransference the outTransference to set
	 */
	public void setOutTransference(long outTransference) {
		this.outTransference = outTransference;
	}

	
	public void setCostAlreadyPayed(double costPayed) {
		this.costPayed = costPayed;
	}
	
	public double getCostAlreadyPayed(){
		return this.costPayed;
	}
	
	public void setInTransferencePayed(long value){
		inTransferencePayed = value;
	}
	
	public long getInTransferencePayed(){
		return inTransferencePayed;
	}
	
	public void setOutTransferencePayed(long value){
		outTransferencePayed = value;
	}
	
	public long getOutTransferencePayed(){
		return outTransferencePayed;
	}

	public void updateTransference(long sizeInBytes, long responseSizeInBytes) {
		this.inTransference += sizeInBytes;
		this.outTransference += responseSizeInBytes;
	}
}
