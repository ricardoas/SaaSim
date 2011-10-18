package commons.sim.jeevent;

import java.io.Serializable;

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

	void forward(JEEvent event, JEEventHandler handler);

}