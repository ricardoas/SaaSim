package commons.io;

import java.util.regex.Pattern;

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
	
	private final int saasClientID;
	private static final Pattern pattern = Pattern.compile("( +|\t+)+");


	/**
	 * Default constructor
	 * @param workloadPath 
	 */
	public GEISTMultiFileWorkloadParser(String workloadPath, int saasclientID) {
		super(workloadPath);
		this.saasClientID = saasclientID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request parseRequest(String line) {
		String[] eventData = pattern.split(line);
		
		long [] demand = new long[eventData.length - 5];
		for (int i = 5; i < eventData.length; i++) {
			demand[i-5] = Long.valueOf(eventData[i]);
		}
		
		return new Request(Long.valueOf(eventData[1]), saasClientID, Integer.valueOf(eventData[0]), Long
				.valueOf(eventData[2]), Long.valueOf(eventData[3]),
				Long.valueOf(eventData[4]), demand);
	}
}
