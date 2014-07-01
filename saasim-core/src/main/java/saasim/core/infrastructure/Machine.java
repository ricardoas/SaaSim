package saasim.core.infrastructure;

import saasim.core.saas.Request;


/**
 * Methods to manage a machine.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Machine{
	
	public static final String MACHINE_BACKLOGSIZE = "machine.backlogsize";
	public static final String MACHINE_THREADSIZE = "machine.numberofthreads";
	public static final String MACHINE_SETUPTIME = "machine.setuptime";

	void reconfigure(InstanceDescriptor descriptor);
	
	/**
	 * Send a new {@link Request} to be processed by this machine.
	 * @param request a new {@link Request} to be processed by this machine.
	 */
	void queue(Request request);
	
	long getStartUpDelay();
	
	void shutdown();

	boolean canShutdown();
}