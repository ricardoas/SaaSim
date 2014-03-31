package saasim.core.event;
public class SuperTypeHandler implements EventHandler{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2100501678674392966L;
	
	public int field;

	@TestEvent
	public void doSomething(){
		field = 0;
	}
}
