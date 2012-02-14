package commons.cloud;

import commons.config.Configuration;

/**
 * Singleton for ID generation.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum IDGenerator {
	
	/**
	 * Singleton 
	 */
	GENERATOR;
	
	private long nextID;
	
	/**
	 * Private constructor
	 */
	private IDGenerator(){
		nextID = Configuration.getInstance().getSimulationInfo().getCurrentDayInMillis();
	}
	
	/**
	 * @return The next available ID
	 */
	public long next(){
		return nextID++;
	}
	
}
