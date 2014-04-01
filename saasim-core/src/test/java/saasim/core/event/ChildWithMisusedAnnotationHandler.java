package saasim.core.event;
public class ChildWithMisusedAnnotationHandler extends SuperTypeHandler{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4400016827894319294L;

	@TestEvent
	public void doSomethingElse() {
		field = 0;
	}

}
