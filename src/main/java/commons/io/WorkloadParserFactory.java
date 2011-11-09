/**
 * 
 */
package commons.io;

import java.util.List;

import commons.cloud.Request;
import commons.config.Configuration;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class WorkloadParserFactory {
	
	public static WorkloadParser<List<Request>> getWorkloadParser(){
		return getWorkloadParser(Configuration.getInstance().getParserPageSize().getMillis());
	}

	public static WorkloadParser<List<Request>> getWorkloadParser(long pageSize){
		
		assert pageSize > 0: "Invalid page size";
		
		Configuration config = Configuration.getInstance();
		ParserIdiom parserIdiom = config.getParserIdiom();
		
		String[] workloads = config.getWorkloads();
		
		@SuppressWarnings("unchecked")
		WorkloadParser<Request>[] parsers = new WorkloadParser[workloads.length];
		
		for (int i = 0; i < workloads.length; i++) {
			parsers[i] = parserIdiom.getInstance(workloads[i]);
		}
		
		return new TimeBasedWorkloadParser(pageSize, parsers);
	}
}
