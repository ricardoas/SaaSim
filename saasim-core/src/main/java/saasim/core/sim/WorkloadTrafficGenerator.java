package saasim.core.sim;

import java.util.ArrayList;
import java.util.List;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.io.TraceReader;
import saasim.core.io.TraceReaderFactory;

import com.google.inject.Inject;

/**
 * it generates workload to {@link Application}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class WorkloadTrafficGenerator {
	
	public static final String TRACE_GENERATOR_BUFFER = "trace.generator.buffer";

	//FIXME seems to be in the wrong place
	public static final String SAAS_TENANT_TRACE = "saas.tenant.trace";
	
	private static final int DEFAULT_BUFFER_SIZE = 60 * 1000;
	private final EventScheduler scheduler;
	private final Application application;
	private final List<TraceReader<Request>> parsers;
	private final int bufferSize;

	/**
	 * Default constructor.
	 * 
	 * @param configuration {@link Configuration} instance.
	 * @param scheduler {@link Event} queue manager.
	 * @param application {@link Application} being simulated.
	 * @param readerFactory {@link TraceReader} factory object.
	 */
	@Inject
	public WorkloadTrafficGenerator(Configuration configuration, EventScheduler scheduler, Application application, TraceReaderFactory<Request> readerFactory) {
		this.scheduler = scheduler;
		this.application = application;
		
		String[] fileNames = configuration.getStringArray(SAAS_TENANT_TRACE);
		parsers = new ArrayList<>();
		for (String string : fileNames) {
			parsers.add(readerFactory.create(string, parsers.size()));
		}
		
		this.bufferSize = configuration.getInt(TRACE_GENERATOR_BUFFER, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Start load generation.
	 */
	public void start(){
		long counter = scheduler.now() + bufferSize;
		
		for (TraceReader<Request> parser : parsers) {
			long timestamp;
			do{
				final Request r = parser.next();
				if(r == null){
					break;
				}
				timestamp = r.getArrivalTimeInMillis();
				scheduler.queueEvent(new Event(r.getArrivalTimeInMillis()) {
					@Override
					public void trigger() {
						application.queue(r);
						
					}
				});
			}while(timestamp <= counter);
		}
		
		scheduler.queueEvent(new Event(counter) {
			@Override
			public void trigger() {
				start();
			}
		});
	}

}
