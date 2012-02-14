package saasim.sim.jeevent;

import java.io.Serializable;

/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface JEEventHandler extends Serializable{

	/**
	 * @param event
	 */
	void send(JEEvent event);

	/**
	 * @return
	 */
	int getHandlerId();
	
	/**
	 * @return
	 */
	long now();
	
}