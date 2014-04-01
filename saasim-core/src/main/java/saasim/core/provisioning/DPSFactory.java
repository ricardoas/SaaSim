package saasim.core.provisioning;

import saasim.core.config.Configuration;


/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class DPSFactory {
	
	/**
	 * 
	 * @param initargs 
	 * @return
	 */
	public static DPS createDPS(Configuration config){
		Class<?> clazz = config.getDPSHeuristicClass();
		
		try {
			return (DPS) clazz.getDeclaredConstructors()[0].newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}

	
}
