package commons.sim;

/**
 * Singleton simulation clock.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum Clock {
	
	/**
	 * Unique instance
	 */
	INSTANCE();
	
	private long tick;
	
	/**
	 * Private constructor. Clock starts at zero.
	 */
	private Clock(){
		reset();
	}
	
	/**
	 * Reset clock time.
	 */
	public void reset() {
		this.tick = 0;
	}

	/**
	 * Make it work!
	 * @return The current tick
	 */
	public long walk() {
		return ++this.tick;
	}
	
}
