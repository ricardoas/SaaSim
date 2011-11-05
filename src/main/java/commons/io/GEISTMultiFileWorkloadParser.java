package commons.io;

import java.util.StringTokenizer;

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
	
//	private static final Pattern pattern = Pattern.compile("( +|\t+)+");

	/**
	 * Default constructor
	 * @param workloads 
	 */
	public GEISTMultiFileWorkloadParser(String workload, int saasclientID) {
		super(workload, saasclientID, Checkpointer.loadSimulationInfo().getCurrentDayInMillis());
	}
	
	@Override
	public WorkloadParser<Request> clone() {
		return new GEISTMultiFileWorkloadParser(workload, saasClientID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request parseRequest(String line) {
//		String[] eventData = pattern.split(line);
		StringTokenizer tokenizer = new StringTokenizer(line, "( +|\t+)+");
		
//		int userID = Integer.parseInt(eventData[0]);
//		long reqID = Long.parseLong(eventData[1]);
//		long arrivalTimeInMillis = Long.parseLong(eventData[2]) + shift;
//		long requestSizeInBytes = Long.parseLong(eventData[3]);
//		long responseSizeInBytes = Long.parseLong(eventData[4]);
		
		int userID = Integer.parseInt(tokenizer.nextToken());
		long reqID = Long.parseLong(tokenizer.nextToken());
		long arrivalTimeInMillis = Long.parseLong(tokenizer.nextToken()) + shift;
		long requestSizeInBytes = Long.parseLong(tokenizer.nextToken());
		long responseSizeInBytes = Long.parseLong(tokenizer.nextToken());
		
//		long [] demand = new long[eventData.length - 5];
//		for (int i = 5; i < eventData.length; i++) {
//			demand[i-5] = Long.valueOf(eventData[i]);
//		}

		long [] demand = new long[tokenizer.countTokens()];
		int index = 0;
		while(tokenizer.hasMoreTokens()){
			demand[index++] = Long.parseLong(tokenizer.nextToken());
		}
		
		return new Request(reqID, saasClientID, userID, arrivalTimeInMillis,
				requestSizeInBytes, responseSizeInBytes, demand);
	}

	@Override
	public void applyError(double error) {
		throw new RuntimeException("Not yet implemented!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return 1;
	}
}
