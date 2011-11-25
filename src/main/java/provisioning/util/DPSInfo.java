package provisioning.util;

import java.io.Serializable;

import provisioning.UrgaonkarHistory;
import provisioning.UrgaonkarStatistics;

public class DPSInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 608014581591046125L;
	
	public UrgaonkarStatistics[] stat;
	public UrgaonkarHistory history;
}
