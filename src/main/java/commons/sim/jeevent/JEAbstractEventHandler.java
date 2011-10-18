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
    
    public JEAbstractEventHandler(){
    	this.scheduler = null;
    	this.id = 0;
    }
    
    public JEAbstractEventHandler(JEEventScheduler scheduler, int id){
		this.scheduler = scheduler;
		this.id = id;
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
    
    protected void setScheduler(JEEventScheduler scheduler){
    	this.scheduler = scheduler;
    	this.id = this.scheduler.registerHandler(this);
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
