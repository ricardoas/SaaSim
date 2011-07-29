/* JEEventHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package commons.sim.jeevent;


/**
 * TODO make doc
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class JEAbstractEventHandler implements JEEventHandler {
	
    private final int id;
    
    private final JEEventScheduler scheduler;
    
    /**
     * Default empty constructor. Creates and registers a handler in the scheduler.
     */
    public JEAbstractEventHandler(JEEventScheduler scheduler) {
    	
    	this.scheduler = scheduler;
		this.id = getScheduler().registerHandler(this);
    }
    
    /**
	 * {@inheritDoc}
	 */
	
    @Override
	public abstract void handleEvent(JEEvent event);
    
    /**
	 * {@inheritDoc}
	 */
	
    @Override
	public void send(JEEvent event) {
    	getScheduler().queueEvent(event);
    }
    
    @Override
	public void forward(JEEvent event, JEEventHandler handler) {
    	getScheduler().queueEvent(new JEEvent(event, handler));
    }
    
    /**
	 * @return the scheduler
	 */
	protected JEEventScheduler getScheduler() {
		return scheduler;
	}

	/**
	 * {@inheritDoc}
	 */
	
    @Override
	public int getHandlerId() {
    	return id;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		JEAbstractEventHandler other = (JEAbstractEventHandler) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
