package provisioning.util;

import java.io.Serializable;
import java.util.LinkedList;

import provisioning.UrgaonkarHistory;
import provisioning.UrgaonkarStatistics;

import commons.sim.components.MachineDescriptor;

public class DPSInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 608014581591046125L;
	
	public UrgaonkarStatistics[] stat;
	public UrgaonkarHistory history;
	public LinkedList<LinkedList<MachineDescriptor>> list;
	
}
