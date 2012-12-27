package saasim.sim.core;

import java.io.Serializable;

/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface EventHandler extends Serializable{

	/**
	 * @param event
	 */
	void send(Event event);

	/**
	 * @return
	 */
	int getHandlerId();
	
	/**
	 * @return
	 */
	long now();
	
}