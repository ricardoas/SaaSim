package saasim.ext.io;

import java.io.FileNotFoundException;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.io.TraceReader;
import saasim.core.io.TrafficGenerator;
import saasim.core.saas.Tenant;

import com.google.inject.Inject;

/**
 * it generates workload to {@link Application}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class BufferedTrafficGenerator implements TrafficGenerator {
	
	private static final String TRAFFIC_GENERATOR_BUFFER = "traffic.generator.buffer";
	private static final int DEFAULT_BUFFER_SIZE = 60 * 1000;
	private static int tenantID = 0;
	
	private final EventScheduler scheduler;
	private Application application;
	private TraceReader reader;
	private final int bufferSize;

	/**
	 * Default constructor.
	 * 
	 * @param globalConf {@link Configuration} instance.
	 * @param scheduler {@link Event} queue manager.
	 * @param application {@link Application} being simulated.
	 * @param readerFactory {@link TraceReader} factory object.
	 * @throws FileNotFoundException 
	 */
	@Inject
	public BufferedTrafficGenerator(Configuration globalConf, EventScheduler scheduler, TraceReader reader, Application application) throws FileNotFoundException {
		this.scheduler = scheduler;
		this.reader = reader;
		this.application = application;
		
		this.bufferSize = globalConf.getInt(TRAFFIC_GENERATOR_BUFFER, DEFAULT_BUFFER_SIZE);
		this.reader.setUp(globalConf.getStringArray(Tenant.SAAS_TENANT_TRACE)[tenantID], tenantID);
		tenantID++;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.io.TrafficGenerator#start()
	 */
	public void start(){
		long counter = scheduler.now() + bufferSize;
		
		long timestamp;
		do{
			final Request r = reader.next();
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

	@Override
	public Application getApplication() {
		return application;
	}

}
