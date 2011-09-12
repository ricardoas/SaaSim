package commons.io;

import java.util.regex.Pattern;

import org.apache.commons.math.random.RandomDataImpl;

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
public class GEISTSingleFileWorkloadParser extends AbstractWorkloadParser{
	
	private static final Pattern pattern = Pattern.compile("( +|\t+)+");

	/**
	 * Default constructor
	 * @param workloadPath 
	 */
	public GEISTSingleFileWorkloadParser(String workloadPath) {
		super(workloadPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request parseRequest(String line) {
		String[] eventData = pattern.split(line);
		
//		long [] demand = new long[eventData.length - 5 - 1];
//		for (int i = 5; i < eventData.length - 1; i++) {
//			demand[i-5] = Long.valueOf(eventData[i]);
//		}
		long[] demand = new long[4];
		RandomDataImpl r = new RandomDataImpl();
		for (int i = 0; i < 4; i++){
			demand[i] = r.nextInt(200, 500);
		}
		
		return new Request(Long.valueOf(eventData[2]), Integer.valueOf(eventData[1]), Integer.valueOf(eventData[0]), 
				Long.valueOf(eventData[3]), Long.valueOf(eventData[4]),
				Long.valueOf(eventData[5]), demand);
	}
}
