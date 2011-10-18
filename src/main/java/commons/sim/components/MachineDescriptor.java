package commons.sim.components;

import java.io.Serializable;

import commons.cloud.MachineType;

/**
 * Machine information.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class MachineDescriptor implements Serializable{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = -548947493055431782L;
	
	private final long machineID;
	private final MachineType type;
	private final boolean reserved;
	private final int providerID;
	private long startTimeInMillis;
	private long finishTimeInMillis;
	private long inTransference;
	private long outTransference;
	
	/**
	 * Default constructor.
	 * @param machineID Machine unique ID.
	 * @param reserved A {@link Boolean} value indicating if this machine has been bought on reservation market.
	 * @param type TODO
	 * @param providerID TODO
	 */
	public MachineDescriptor(long machineID, boolean reserved, MachineType type, int providerID) {
		this.machineID = machineID;
		this.reserved = reserved;
		this.type = type;
		this.providerID = providerID;
		this.finishTimeInMillis = 0;
		this.startTimeInMillis = 0;
	}

	public void setInTransference(long inTransference) {
		this.inTransference = inTransference;
	}

	public void setOutTransference(long outTransference) {
		this.outTransference = outTransference;
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

	/**
	 * @return the inTransference
	 */
	public long getInTransference() {
		return inTransference;
	}

	/**
	 * @return the outTransference
	 */
	public long getOutTransference() {
		return outTransference;
	}
	
	public int getProviderID() {
		return providerID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (machineID ^ (machineID >>> 32));
		result = prime * result + (reserved ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		assert (obj != null): "Comparing with null object";
		assert (getClass() == obj.getClass()): "comparing with different class object";
		
		if (this == obj)
			return true;
		MachineDescriptor other = (MachineDescriptor) obj;
		return (machineID == other.machineID);
	}

	/**
	 * @param inTransferenceInBytes
	 * @param outTransferenceInBytes
	 */
	public void updateTransference(long inTransferenceInBytes, long outTransferenceInBytes) {
		this.inTransference += inTransferenceInBytes;
		this.outTransference += outTransferenceInBytes;
	}
	
	/**
	 * @param now
	 */
	public void reset(long now){
		setStartTimeInMillis(now);
		this.inTransference = 0;
		this.outTransference = 0;
	}

	/**
	 * @return 
	 * 
	 */
	public long getUpTimeInMillis() {
		return finishTimeInMillis - startTimeInMillis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[machineID=" + machineID + ", type=" + type
				+ ", reserved=" + reserved + "]";
	}
}
