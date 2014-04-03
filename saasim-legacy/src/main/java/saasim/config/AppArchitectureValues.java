package saasim.config;

import saasim.sim.SimpleMultiTierApplication;


/**
 * Values of application heuristic.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum AppArchitectureValues {
	
	/**
	 * Heuristic to choose servers in a Round Robin method.
	 */
	MULTITIER(SimpleMultiTierApplication.class.getCanonicalName()), 
	
	/**
	 * 
	 */
	CUSTOM("");
	
	private final String className;
	
	/**
	 * Default private constructor.
	 * @param className the name of heuristic class.
	 */
	private AppArchitectureValues(String className){
		this.className = className;
	}

	/**
	 * Gets the name of heuristic class.
	 * @return The name of heuristic class.
	 */
	public String getClassName() {
		return className;
	}
}
