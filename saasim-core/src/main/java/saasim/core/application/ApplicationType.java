package saasim.core.application;

/**
 * Application types.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum ApplicationType {
	
	/**
	 * Vanilla type. Application with single tier.
	 */
	SINGLE_TIER(""),

	/**
	 * Multitier pipeline application
	 */
	MULTI_TIER(""),
	
	/**
	 * 
	 */
	CUSTOM("");
	
	private final String className;
	
	/**
	 * Default private constructor.
	 * @param className the name of heuristic class.
	 */
	private ApplicationType(String className){
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