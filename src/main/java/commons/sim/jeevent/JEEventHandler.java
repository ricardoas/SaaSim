package commons.sim.jeevent;

import java.io.Serializable;

/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface JEEventHandler extends Serializable{

	/**
	 * @param <T>
	 * @param event
	 */
	void handleEvent(JEEvent event);

	/**
	 * @param event
	 */
	void send(JEEvent event);

	/**
	 * @return
	 */
	int getHandlerId();

	/**
	 * @param event
	 * @param handler
	 */
	void forward(JEEvent event, JEEventHandler handler);

}