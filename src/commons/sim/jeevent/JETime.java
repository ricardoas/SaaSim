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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "INFINITY";
		}
	}

	public static JETime INFINITY = new InfiniteJEETime();

	private final long timeMilliSeconds;

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
    public int compareTo(JETime o) {
    	long diff = timeMilliSeconds - o.timeMilliSeconds;
    	if (diff < 0) {
    		return -1;
    	} else if (diff > 0) {
    		return 1;
    	}
    	return 0;
    }
    
	@Override
	public boolean equals(Object obj) {
		JETime other = (JETime) obj;
		return this.compareTo(other) == other.compareTo(this);
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
    	return Long.toString(timeMilliSeconds);
    }
}