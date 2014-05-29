package saasim.ext.io;

import saasim.core.saas.Request;
import saasim.ext.saas.WebAppRequest;

public class CSVTraceReader extends LineBasedTraceReader {

	public CSVTraceReader() {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.ext.io.LineBasedTraceReader#parseRequest(java.lang.String)
	 */
	@Override
	protected Request parseRequest(String line) {
		String[] tokens = line.split(",");

		int index = 0;
		
		int userID = Integer.parseInt(tokens[index++]);
		long reqID = Long.parseLong(tokens[index++]);
		long arrivalTimeInMillis = Long.parseLong(tokens[index++]);
		long requestSizeInBytes = Long.parseLong(tokens[index++]);
		long responseSizeInBytes = Long.parseLong(tokens[index++]);

		long [] demand = new long[3];
		for (int i = 0; i < demand.length; i++) {
			demand[i] = Long.parseLong(tokens[index++]);
		}
		return new WebAppRequest(reqID, tenantID, userID, arrivalTimeInMillis,
				requestSizeInBytes, responseSizeInBytes, demand);
	}

}
