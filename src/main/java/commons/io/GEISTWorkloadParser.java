package commons.io;

import java.util.StringTokenizer;

import commons.cloud.Request;
import commons.config.Configuration;

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
public class GEISTWorkloadParser extends AbstractWorkloadParser{
	
	/**
	 * Default constructor
	 * @param workloads 
	 */
	public GEISTWorkloadParser(String workload) {
		super(workload, Configuration.getInstance().getSimulationInfo().getCurrentDayInMillis());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkloadParser<Request> clone() {
		return new GEISTWorkloadParser(workload);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request parseRequest(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, "( +|\t+)+");
		
		int userID = Integer.parseInt(tokenizer.nextToken());
		long reqID = Long.parseLong(tokenizer.nextToken());
		long arrivalTimeInMillis = Long.parseLong(tokenizer.nextToken()) + shift;
		long requestSizeInBytes = Long.parseLong(tokenizer.nextToken());
		long responseSizeInBytes = Long.parseLong(tokenizer.nextToken());
		
		long [] demand = new long[tokenizer.countTokens()];
		int index = 0;
		while(tokenizer.hasMoreTokens()){
			demand[index++] = Long.parseLong(tokenizer.nextToken());
		}
		
		return new Request(reqID, saasClientID, userID, arrivalTimeInMillis,
				requestSizeInBytes, responseSizeInBytes, demand);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return 1;
	}
}
