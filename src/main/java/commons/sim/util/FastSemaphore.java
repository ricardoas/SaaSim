package commons.sim.util;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

/**
 * This class represents the behavior of a {@link Semaphore} in a thread management.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class FastSemaphore implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4883577311478490542L;
	private int permits;

	/**
	 * Default constructor.
	 * @param permits a number of permissions 
	 */
	public FastSemaphore(int permits) {
		this.permits = permits;
    }
	
	/**
	 * Verifies if is possible acquire and running.
	 * @return <code>true</code> if the can acquire.
	 */
    public boolean tryAcquire() {
    	if(permits < 1){
    		return false;
    	}
    	permits--;
    	return true;
    }
    
    /**
     * Releases a one more permission in the number of permissions.
     */
    public void release() {
    	permits++;
    }
    
    /**
     * Gets the number of available permissions.
     * @return The number of permissions.
     */
    public int availablePermits() {
    	return permits;
    }
}
