package saasim.core.event;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.ConfigurationException;
import org.reflections.ReflectionUtils;


/**
 * Event scheduler. Events are ordered in time.
 *
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 3.0.0
 */
public final class EventScheduler implements Serializable{
	
	private static final long serialVersionUID = -5091449738303689646L;
	
	private long now;
	private long simulationEnd;
    private TreeSet<Event> eventSet;
	private Random random;
//	private transient List<Class<? extends Annotation>> eventTypes;
	private transient Map<Class<?>,Map<Class<? extends Annotation>, Method>> handlingMethods;
	
    /**
     * Default constructor.
     * @param seed 
     * @throws IOException 
     */
    public EventScheduler(long seed){
    	this.random = new Random(seed);
    	this.now = 0;
		this.eventSet = new TreeSet<Event>();
		this.handlingMethods = new HashMap<Class<?>, Map<Class<? extends Annotation>,Method>>();
    }
    
	/**
     * Add a new event to the queue. Duplicates are not allowed.
     * @param event A new event.
     */
    public void queueEvent(EventHandler targetHandler, Class<? extends Annotation> eventType,long scheduledTime, EventPriority priority, Object... arguments) {
    	
    	if (scheduledTime < now) {
		    throw new RuntimeException("ERROR: emulation time(" + now + ") already ahead of event time("+scheduledTime+"). Event is outdated and will not be processed.");
		}
		eventSet.add(new Event(targetHandler, eventType, scheduledTime, priority, arguments));
    }

	/**
     * Add a new event to the queue. Duplicates are not allowed.
     * @param event A new event.
     */
    public void queueEvent(EventHandler targetHandler, Class<? extends Annotation> eventType,long scheduledTime, Object... arguments) {
    	
    	if (scheduledTime < now) {
		    throw new RuntimeException("ERROR: emulation time(" + now + ") already ahead of event time("+scheduledTime+"). Event is outdated and will not be processed.");
		}
		eventSet.add(new Event(targetHandler, eventType, scheduledTime, arguments));
    }

    /**
     * Start the emulation.
     */
    public void start(long simulationEnd) {
    	
    	this.simulationEnd = simulationEnd;
    	
		if (!eventSet.isEmpty()) {
			schedule();
			
		}
		this.now = simulationEnd;
    }

	public String dumpPostMortemEvents() {
		StringBuilder sb = new StringBuilder("EVENTS-LEFT ");
		if(!eventSet.isEmpty()){
			while(!eventSet.isEmpty()){
				sb.append(eventSet.pollFirst());
				sb.append('\n');
			}
		}
		return sb.toString();
	}
    
    /**
     * Emulates until there is no more event to send or end of simulation time is reached.
     */
    private void schedule() {
    	
    	Event event = eventSet.pollFirst();
    	while(event != null && event.getTimestamp() <= simulationEnd){
    		now = event.getTimestamp();
    		processEvent(event);
    		event = eventSet.pollFirst();
    	}
    	if(event != null){
    		eventSet.add(event);
    	}
    }

	/**
     * Searches for the target handler and delivers the event.
     * @param event Next event to delivers.
     */
    private void processEvent(Event event) {
		
		EventHandler targetHandler = event.getTarget();
		try {
			handlingMethods.get(targetHandler.getClass()).get(event.getType()).invoke(targetHandler, event.getArguments());
		} catch (Exception e) {
			throw new RuntimeException(targetHandler + " has no implemented method for handling events of type " + event.getType(),e);
		}
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JEEventScheduler [now=" + now + ", simulationEnd="
				+ simulationEnd + ", eventSet="
				+ eventSet + ", random=" + random + "]";
	}

	public void printStatus(){
    	for (Event event : eventSet) {
    		System.out.println(event);
		}
	}
	
	public long now(){
		return this.now;
	}


	@SuppressWarnings("unchecked")
	public void setup(String[] events, String[] handlers) throws ConfigurationException {
		
		try {
			List<Class<? extends Annotation>> eventTypes = new ArrayList<>();
			
			for (String event : events) {
					assert event != null && !event.isEmpty(): "Are you serious?";
					eventTypes.add((Class<? extends Annotation>) Class.forName(event));
			}
			
			handlingMethods = new HashMap<Class<?>, Map<Class<? extends Annotation>,Method>>();
			
			for (String handler : handlers) {
				Class<?> handlerClass = Class.forName(handler);
				
				assert handlerClass != null && !handler.isEmpty(): "Are you serious?";
				
				Map<Class<? extends Annotation>, Method> map = extractHandlers(eventTypes, handlerClass);
				
				assert !map.isEmpty() : "Class " + handlerClass + " has no methods signed with any of the event types you've registered.";
				
				handlingMethods.put(handlerClass, map);
			}
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e);
		}
		
	}

	@SuppressWarnings("unchecked")
	private Map<Class<? extends Annotation>, Method> extractHandlers(
			List<Class<? extends Annotation>> eventTypes, Class<?> handlerClass) {

		HashMap<Class<? extends Annotation>, Method> hashMap = new HashMap<Class<? extends Annotation>, Method>();
		
		for (Class<? extends Annotation> eventType : eventTypes) {
			Set<Method> methods = ReflectionUtils.getAllMethods(handlerClass, ReflectionUtils.withAnnotation(eventType));
			
			if(!methods.isEmpty()){
				hashMap.put(eventType, methods.iterator().next());
			}
		}
		
		return hashMap;
	}
}