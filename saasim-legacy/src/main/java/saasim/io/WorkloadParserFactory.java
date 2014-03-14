package saasim.io;

import java.util.List;

import saasim.cloud.Request;
import saasim.config.Configuration;


/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class WorkloadParserFactory {
	
	public static WorkloadParser<List<Request>> getWorkloadParser(){
		return getWorkloadParser(Configuration.getInstance().getParserPageSize().getMillis());
	}

	public static WorkloadParser<List<Request>> getWorkloadParser(long pageSize){
		
		assert pageSize > 0: "Invalid page size";
		
		return new TimeBasedWorkloadParser(pageSize, Configuration.getInstance().getWorkloads());
	}
}
