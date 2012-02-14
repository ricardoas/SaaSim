package saasim.provisioning.util;

import java.io.Serializable;
import java.util.LinkedList;

import saasim.provisioning.UrgaonkarHistory;
import saasim.provisioning.UrgaonkarStatistics;
import saasim.sim.components.MachineDescriptor;


public class DPSInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 608014581591046125L;
	
	public UrgaonkarStatistics[] stat;
	public UrgaonkarHistory history;
	public LinkedList<LinkedList<MachineDescriptor>> list;
	
}
