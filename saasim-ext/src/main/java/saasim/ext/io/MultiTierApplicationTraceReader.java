package saasim.ext.io;

import saasim.core.saas.Request;
import saasim.ext.saas.MultiQueriesRequest;
import saasim.ext.saas.WebAppRequest;

public class MultiTierApplicationTraceReader extends CSVTraceReader {

	private static int SEED = 0;

	public MultiTierApplicationTraceReader() {
		super();
	}

	@Override
	protected Request buildRequest(int userID, long reqID,
			long arrivalTimeInMillis, long requestSizeInBytes,
			long responseSizeInBytes, long[] demand) {
		return new MultiQueriesRequest(reqID, tenantID, userID, arrivalTimeInMillis,
				requestSizeInBytes, responseSizeInBytes, demand);
		
	}
}
