package commons.sim.jeevent;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class JEException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2468858394012204461L;

	/**
	 * 
	 */
	public JEException() {
		super();
	}

	/**
	 * @param message
	 */
	public JEException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public JEException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JEException(String message, Throwable cause) {
		super(message, cause);
	}

}
