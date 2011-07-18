package commons.sim.jeevent;

public interface JEEventHandler {

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

}