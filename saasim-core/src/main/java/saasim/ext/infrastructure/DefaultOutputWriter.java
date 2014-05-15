package saasim.ext.infrastructure;

import org.apache.log4j.Logger;

import saasim.core.infrastructure.MonitoringServiceConsumer;
import saasim.core.infrastructure.Statistics;

public class DefaultOutputWriter implements MonitoringServiceConsumer{
	
	private Logger logger = Logger.getLogger(MonitoringServiceConsumer.class);

	@Override
	public void report(Statistics statistics) {
		logger.info(statistics);
	}
}
