package saasim.ext.infrastructure;

import saasim.core.infrastructure.MonitoringServiceConsumer;
import saasim.core.infrastructure.Statistics;

public class DefaultOutputWriter implements MonitoringServiceConsumer{

	@Override
	public void report(Statistics statistics) {
		System.out.println(statistics);
	}
}
