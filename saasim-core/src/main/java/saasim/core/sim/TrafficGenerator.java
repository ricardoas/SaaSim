package saasim.core.sim;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.io.TraceReader;
import saasim.core.io.TraceReaderFactory;
import saasim.core.saas.Tenant;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * it generates workload to {@link Application}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TrafficGenerator {
	
	public static final String TRACE_GENERATOR_BUFFER = "trace.generator.buffer";

	private static final int DEFAULT_BUFFER_SIZE = 60 * 1000;
	private final EventScheduler scheduler;
	private final Application application;
	private final TraceReader<Request> parser;
	private final int bufferSize;

	/**
	 * Default constructor.
	 * 
	 * @param globalConf {@link Configuration} instance.
	 * @param scheduler {@link Event} queue manager.
	 * @param application {@link Application} being simulated.
	 * @param readerFactory {@link TraceReader} factory object.
	 */
	@Inject
	public TrafficGenerator(Configuration globalConf, EventScheduler scheduler, TraceReaderFactory<Request> readerFactory, Application application, @Assisted int tenantID) {
		this.scheduler = scheduler;
		this.application = application;
		this.parser = readerFactory.create(globalConf.getStringArray(Tenant.SAAS_TENANT_TRACE)[tenantID], tenantID);
		this.bufferSize = globalConf.getInt(TRACE_GENERATOR_BUFFER, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Start load generation.
	 */
	public void start(){
		long counter = scheduler.now() + bufferSize;
		
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
		
		scheduler.queueEvent(new Event(counter) {
			@Override
			public void trigger() {
				start();
			}
		});
	}

	public Application getApplication() {
		return application;
	}

}
