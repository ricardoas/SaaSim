package provisioning.util;

import provisioning.DPS;

import commons.config.SimulatorConfiguration;


/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum DPSFactory {
	
	/**
	 * Single instance.
	 */
	INSTANCE;
	
	/**
	 * Private constructor
	 */
	private DPSFactory() {}
	
	/**
	 * 
	 * @return
	 */
	public DPS createDPS(){
		Class<?> clazz = SimulatorConfiguration.getInstance().getDPSHeuristicClass();
		
		try {
			return (DPS) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}

	
}
