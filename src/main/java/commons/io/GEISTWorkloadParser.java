package commons.io;

import commons.cloud.Request;

/**
 * GEIST parser. A GEIST workload file contains one request per line.
 * Each line contains nine tab separated columns ordered as follow:<br>
 * <ul>
 * 	<li><b>Client ID</b>: </li>
 * 	<li>User ID</li>
 * 	<li>Request ID</li>
 * 	<li>Time</li>
 * 	<li>Size of requested file</li>
 * 	<li>Request Option</li>
 * 	<li>HTTP operation which originated the request</li>
 * 	<li>URL: FQDN of requested file</li>
 * 	<li>Demand: </li>
 * </ul>
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class GEISTWorkloadParser extends AbstractWorkloadParser{
	
	/**
	 * Default constructor
	 */
	public GEISTWorkloadParser() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request parseRequest(String line) {
		String[] eventData = line.split("( +|\t+)+");
		return new Request(eventData[0], eventData[1], eventData[2], Long
				.valueOf(eventData[3]), Long.valueOf(eventData[4]),
				Integer.valueOf(eventData[5]), eventData[6],
				eventData[7], Long.valueOf(eventData[8]));
	}
}