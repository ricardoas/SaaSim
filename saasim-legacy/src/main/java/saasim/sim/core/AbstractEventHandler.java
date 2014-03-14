package saasim.sim.core;




/**
 * TODO make doc
 *
 * @author thiago - thiago@lsd.ufcg.edu.br
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractEventHandler implements EventHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5073241509806147279L;

	private int id;
    
    private EventScheduler scheduler;

    /**
     * Default empty constructor. Creates and registers a handler in the scheduler.
     */
    public AbstractEventHandler(EventScheduler scheduler) {
    	
    	this.scheduler = scheduler;
		this.id = this.scheduler.registerHandler(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void send(Event event) {
    	getScheduler().queueEvent(event);
    }
    
    /**
	 * @return the scheduler
	 */
	protected EventScheduler getScheduler() {
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
		return id == ((AbstractEventHandler) obj).id;
	}
}
