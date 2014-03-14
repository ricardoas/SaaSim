package saasim.core.config;

/**
 * This class containing the properties to use in SaaS's application.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class SaaSAppProperties {

	public static final String APPLICATION_FACTORY="saas.application.factoryclass";
	
	public static final String APPLICATION_NUM_OF_TIERS= "saas.application.numberoftiers";
	
	public static final String APPLICATION_HEURISTIC = "saas.application.heuristic";
	
	public static final String APPLICATION_CUSTOM_HEURISTIC = "saas.application.heuristicclass";
	
	public static final String APPLICATION_INITIAL_SERVER_PER_TIER = "saas.application.startreplicas";
	
	public static final String APPLICATION_MAX_SERVER_PER_TIER = "saas.application.maxreplicas";

	public static final String APPLICATION_SLA_MAX_RESPONSE_TIME = "saas.sla.maxrt";

	public static final String APPLICATION_SETUP_TIME = "saas.setuptime";
}
