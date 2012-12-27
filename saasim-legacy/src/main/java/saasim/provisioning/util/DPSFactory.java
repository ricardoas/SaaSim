package saasim.provisioning.util;

import saasim.config.Configuration;
import saasim.provisioning.DPS;



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
	public static DPS createDPS(Object... initargs){
		Class<?> clazz = Configuration.getInstance().getDPSHeuristicClass();
		
		try {
			return (DPS) clazz.getDeclaredConstructors()[0].newInstance(initargs);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}

	
}
