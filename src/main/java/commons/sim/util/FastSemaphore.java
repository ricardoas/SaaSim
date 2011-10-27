package commons.sim.util;

import java.io.Serializable;

public class FastSemaphore implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -4883577311478490542L;
	private int permits;

	public FastSemaphore(int permits) {
		this.permits = permits;
    }
	
    public boolean tryAcquire() {
    	if(permits < 1){
    		return false;
    	}
    	permits--;
    	return true;
    }
    
    public void release() {
    	permits++;
    }
    
    public int availablePermits() {
    	return permits;
    }
}
