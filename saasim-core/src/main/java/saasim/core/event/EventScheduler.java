package saasim.core.event;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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
	private transient List<Class<? extends Annotation>> eventTypes;
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
		this.eventTypes = new ArrayList<Class<? extends Annotation>>();
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

	/**
	 * Cleans handlers records and registers new handler classes. It search each class for methods annotated with
	 * previous event types registered.
	 * @param handlerClasses Handler classes
	 * @return This scheduler
	 */
	public EventScheduler clearAndRegisterHandlerClasses(Class<?>... handlerClasses) {
		
		assert handlerClasses != null : "Can't register null annotation. Check your code!";
		assert handlerClasses.length != 0 : "You must register at least one event annotation. Check your code!";
		assert !Arrays.asList(handlerClasses).contains(null) : "Can't register null annotation. Check your code!";

		handlingMethods = new HashMap<Class<?>, Map<Class<? extends Annotation>,Method>>();
		
		for (Class<?> clazz : handlerClasses) {
			Map<Class<? extends Annotation>, Method> map = extractHandlers(clazz, new HashMap<Class<? extends Annotation>, Method>());
			
			assert !map.isEmpty() : "Class " + clazz + " has no methods signed with any of the event types you've registered.";
			
			handlingMethods.put(clazz, map);
		}

		return this;
	}
	
	private Map<Class<? extends Annotation>, Method> extractHandlers(Class<?> clazz, HashMap<Class<? extends Annotation>, Method> hashMap) {
		
		for (Class<? extends Annotation> eventType : eventTypes) {
			Set<Method> methods = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(eventType));
			
			if(!methods.isEmpty()){
				hashMap.put(eventType, methods.iterator().next());
			}
		}
		
		return hashMap;
	}

	/**
	 * Cleans annotation records and register new ones. 
	 * @param eventType {@link Annotation} classes used as event types.
	 * @return This scheduler.
	 */
	public EventScheduler clearAndRegisterAnnotations(Class<? extends Annotation>... eventType) {
		assert eventType != null : "Can't register null annotation. Check your code!";
		assert eventType.length != 0 : "You must register at least one event annotation. Check your code!";
		assert !Arrays.asList(eventType).contains(null) : "Can't register null annotation. Check your code!";
		
		eventTypes = new ArrayList<Class<? extends Annotation>>(Arrays.asList(eventType));
		return this;
	}
}