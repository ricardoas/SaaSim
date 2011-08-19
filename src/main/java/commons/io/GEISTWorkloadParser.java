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
 * 	<li>Request size in Bytes</li>
 * 	<li>Response size in Bytes (Size of requested file)</li>
 * 	<li> Array of demand in millis </li>
 * </ul>
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class GEISTWorkloadParser extends AbstractWorkloadParser{
	
	/**
	 * Default constructor
	 * @param workloadPath 
	 */
	public GEISTWorkloadParser(String... workloadPath) {
		super(workloadPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request parseRequest(String line) {
		String[] eventData = line.split("( +|\t+)+");
		
		long [] demand = new long[eventData.length - 6];
		for (int i = 6; i < eventData.length; i++) {
			demand[i-6] = Long.valueOf(eventData[i]);
		}
		
		return new Request(eventData[2], eventData[0], eventData[1], Long
				.valueOf(eventData[3]), Long.valueOf(eventData[4]),
				Long.valueOf(eventData[5]), demand);
	}
}
