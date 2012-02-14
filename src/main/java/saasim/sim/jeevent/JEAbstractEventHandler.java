/* JEEventHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package saasim.sim.jeevent;




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
	private static final long serialVersionUID = 5073241509806147279L;

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
	public void send(JEEvent event) {
    	getScheduler().queueEvent(event);
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
    
	@Override
	public long now() {
		return scheduler.now();
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
