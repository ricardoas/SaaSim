package commons.io;

import commons.cloud.Request;

/**
 * GEIST parser. A GEIST workload file contains one request per line.
 * Each line contains nine tab separated columns ordered as follow:<br>
 * <ul>
 * 	<li>User ID</li>
 * 	<li>Request ID</li>
 * 	<li>Time</li>
 * 	<li>Request size in Bytes</li>
 * 	<li>Response size in Bytes (Size of requested file)</li>
 * 	<li> Array of demand in millis </li>
 * </ul>
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class GEISTMultiFileWorkloadParser extends AbstractWorkloadParser{
	
	private final String saasClientID;

	/**
	 * Default constructor
	 * @param workloadPath 
	 */
	public GEISTMultiFileWorkloadParser(String workloadPath, String saasclientID) {
		super(workloadPath);
		this.saasClientID = saasclientID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request parseRequest(String line) {
		String[] eventData = line.split("( +|\t+)+");
		
		long [] demand = new long[eventData.length - 5];
		for (int i = 5; i < eventData.length; i++) {
			demand[i-5] = Long.valueOf(eventData[i]);
		}
		
		return new Request(Long.parseLong(eventData[1]), saasClientID, Long.parseLong(eventData[0]), Long
				.valueOf(eventData[2]), Long.parseLong(eventData[3]),
				Long.valueOf(eventData[4]), demand);
	}
}
