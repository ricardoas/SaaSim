package commons.sim.components;

import java.io.Serializable;

import commons.cloud.MachineType;
import commons.cloud.Provider;

/**
 * Machine information.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class MachineDescriptor implements Serializable {
	
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
	 * @param type TODO the type of this {@link MachineDescriptor}, see {@link MachineType}.
	 * @param providerID TODO the id of {@link Provider} to be used.
	 */
	public MachineDescriptor(long machineID, boolean reserved, MachineType type, int providerID) {
		this.machineID = machineID;
		this.reserved = reserved;
		this.type = type;
		this.providerID = providerID;
		this.finishTimeInMillis = 0;
		this.startTimeInMillis = 0;
	}

	/**
	 * Set the value of input transference.
	 * @param inTransference
	 */
	public void setInTransference(long inTransference) {
		this.inTransference = inTransference;
	}

	/**
	 * Set the value of output transference.
	 * @param outTransference
	 */
	public void setOutTransference(long outTransference) {
		this.outTransference = outTransference;
	}

	/**
	 * Gets the {@link MachineDescriptor} type.
	 * @return A {@link MachineType} of this machine.
	 */
	public MachineType getType(){
		return this.type;
	}
	
	/**
	 * Gets the id f this {@link MachineDescriptor}.
	 * @return the id
	 */
	public long getMachineID() {
		return machineID;
	}
	
	/**
	 * Gets a value about reserved condition of this {@link MachineDescriptor}.
	 * @return <code>true</code> if this machine is previously reserved.
	 */
	public boolean isReserved() {
		return reserved;
	}
	
	/**
	 * Gets the start time in milliseconds of this machine.
	 * @return the startTimeInMillis
	 */
	public long getStartTimeInMillis() {
		return startTimeInMillis;
	}
	
	/**
	 * Sets the start time in milliseconds of this machine.
	 * @param startTimeInMillis 
	 */
	public void setStartTimeInMillis(long startTimeInMillis) {
		this.startTimeInMillis = startTimeInMillis;
	}
	
	/**
	 * Gets the finish time in milliseconds of this machine.
	 * @return the finishTimeInMillis
	 */
	public long getFinishTimeInMillis() {
		return finishTimeInMillis;
	}

	/**
	 * Sets the finish time in milliseconds of this machine.
	 * @param finishTimeInmilliseconds
	 */
	public void setFinishTimeInMillis(long finishTimeInMillis) {
		this.finishTimeInMillis = finishTimeInMillis;
	}

	/**
	 * Gets the value about input transference.
	 * @return the inTransference
	 */
	public long getInTransference() {
		return inTransference;
	}

	/**
	 * Gets the value about output transference.
	 * @return the outTransference
	 */
	public long getOutTransference() {
		return outTransference;
	}
	
	/**
	 * Gets the up time in milliseconds of this machine, determined for the difference between
	 * finish time and start time. 
	 * @return the up time
	 */
	public long getUpTimeInMillis() {
		return finishTimeInMillis - startTimeInMillis;
	}
	
	/**
	 * Gets the id's of {@link Provider}.
	 * @return the id of {@link Provider}.
	 */
	public int getProviderID() {
		return providerID;
	}

	/**
	 * Updates values about input and output transference.
	 * @param inTransferenceInBytes value will be added to input transference 
	 * @param outTransferenceInBytes value will be added to output transference
	 */
	public void updateTransference(long inTransferenceInBytes, long outTransferenceInBytes) {
		this.inTransference += inTransferenceInBytes;
		this.outTransference += outTransferenceInBytes;
	}
	
	/**
	 * Resets the {@link MachineDescriptor}.
	 * @param now time to be set start time of this machine
	 */
	public void reset(long now){
		setStartTimeInMillis(now);
		this.inTransference = 0;
		this.outTransference = 0;
	}

	/**
	 * Compare two {@link MachineDescriptor}.
	 * <code>true</code> if them id's are equals.
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
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[machineID=" + machineID + ", type=" + type
				+ ", reserved=" + reserved + "]";
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
}
