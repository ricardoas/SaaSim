package provisioning;

/**
 * 
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface Monitor {
	
//	boolean registerApplication(Application application);
//
//	boolean unregisterApplication(Application application);
	
	/**
	 * Start monitoring registered applications.
	 */
	void start();

	/**
	 * Stop monitoring registered applications.
	 */
	void stop();

}
