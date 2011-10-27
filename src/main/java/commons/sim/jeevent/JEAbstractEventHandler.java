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
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 2572195285367262298L;

	private int id;
    
    private JEEventScheduler scheduler;
    
    /**
     * Default empty constructor. Creates and registers a handler in the scheduler.
     */
    public JEAbstractEventHandler(JEEventScheduler scheduler) {
    	
    	this.scheduler = scheduler;
		this.id = this.scheduler.registerHandler(this);
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
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		assert (obj != null): "Can't compare with null object.";
		assert  (getClass() == obj.getClass()): "Can't compare with another class object.";

		if (this == obj)
			return true;
		return id == ((JEAbstractEventHandler) obj).id;
	}
}
