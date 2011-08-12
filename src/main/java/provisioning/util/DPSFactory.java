package provisioning.util;

import provisioning.DPS;

import commons.config.Configuration;


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
	 * @param initargs 
	 * @return
	 */
	public DPS createDPS(Object... initargs){
		Class<?> clazz = Configuration.getInstance().getDPSHeuristicClass();
		
		try {
			return (DPS) clazz.getDeclaredConstructors()[0].newInstance(initargs);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}

	
}
