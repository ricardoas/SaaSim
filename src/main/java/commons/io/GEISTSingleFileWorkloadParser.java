package commons.io;

import java.util.regex.Pattern;

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
	private int reqID;
	private int userID;

	/**
	 * Default constructor
	 * @param workloads 
	 */
	public GEISTSingleFileWorkloadParser(String[] workloads) {
		super(workloads, 1);
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
		demand[0] = Long.parseLong("200");
		demand[1] = Long.parseLong("200");
		demand[2] = Long.parseLong("200");
		demand[3] = Long.parseLong("200");
			
		long parseLong = (Long.parseLong(eventData[3]));
		return null;/* FIXME new Request(reqID++, 0, userID++, 
				parseLong, Long.parseLong(eventData[4]),
				Long.parseLong(eventData[5]), demand);*/
	}
	
	public static void main(String[] args) {
		int total = 27;
		int counter = 1;
		
		for(int i = 0; i <= total; i++){
			for(int j = 0; j <= total -i; j++){
				for(int k = 0; k <= total - i - j; k++){
					for(int l = 0; l <= total - i -j - k; l++){
						System.out.println(counter++);
					}
				}
			}
		}
	}
}

