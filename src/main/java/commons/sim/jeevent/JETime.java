/* JETime - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;

/**
 * TODO make doc
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 */
public class JETime implements Comparable<JETime> {
	
	/**
	 * To use in simulations that never end... if you know what I mean.
	 * 
	 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
	 *
	 */
	private static final class InfiniteJEETime extends JETime{

		/**
		 * Default Constructor
		 */
		public InfiniteJEETime(){
			super(-1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(JETime o) {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public JETime plus(JETime otherETime) {
			return this;
		}
		
		@Override
		public JETime multiply(long index){
	    	return this;
	    }
	    
		@Override
	    public JETime minus(JETime otherTime){
	    	return this;
	    }
	    
		@Override
	    public JETime divide(long index){
	    	return this;
	    }

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "INFINITY";
		}
	}

	public static JETime INFINITY = new InfiniteJEETime();

	public long timeMilliSeconds;

	/**
     * Default constructor
     * @param timeMilliSeconds Time in milliseconds.
     */
    public JETime(long timeMilliSeconds) {
    	this.timeMilliSeconds = timeMilliSeconds;
    }
    
    /**
     * Add to this time.
     * @param otherTime
     * @return 
     */
    public JETime plus(JETime otherTime) {
    	if(otherTime.equals(INFINITY)){
    		return INFINITY;
    	}
    	return new JETime(timeMilliSeconds + otherTime.timeMilliSeconds);
    }
    
    public JETime multiply(long index){
    	return new JETime (this.timeMilliSeconds * index);
    }
    
    public JETime minus(JETime otherTime){
    	if(otherTime.equals(INFINITY)){
    		return INFINITY;
    	}
    	return new JETime(timeMilliSeconds - otherTime.timeMilliSeconds);
    }
    
    public JETime divide(long index){
    	return new JETime(timeMilliSeconds / index);
    }
    
    /**
     * @param otherTime
     * @return
     */
    public boolean isEarlierThan(JETime otherTime) {
		return (compareTo(otherTime) < 0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public int compareTo(JETime o) {
    	if(o.timeMilliSeconds == INFINITY.timeMilliSeconds){
    		return -1;
    	}
    	long diff = timeMilliSeconds - o.timeMilliSeconds;
    	if (diff < 0) {
    		return -1;
    	} else if (diff > 0) {
    		return 1;
    	}
    	return 0;
    }
    
    
    
/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (timeMilliSeconds ^ (timeMilliSeconds >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JETime other = (JETime) obj;
		if (timeMilliSeconds != other.timeMilliSeconds)
			return false;
		return true;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
    	return Long.toString(timeMilliSeconds);
    }
}