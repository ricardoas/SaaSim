package saasim.core.event;
public class ChildWithOverwritingAndAnnotationHandler extends SuperTypeHandler{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7611094481437751352L;

	@Override
	@TestEvent
	public void doSomething() {
		field = 54;
	}
}
