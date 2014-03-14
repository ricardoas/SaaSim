package saasim.sim.core;

import java.io.Serializable;

/**
 * Each and every event sent during the simulation is addressed to a single handler.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface EventHandler extends Serializable{

	/**
	 * Send an {@link Event} to this handler.
	 * @param event
	 */
	void send(Event event);

	/**
	 * @return This handler id.
	 */
	int getHandlerId();
	
	/**
	 * @return current time stamp.
	 */
	long now();
	
}