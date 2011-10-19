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
	
	private static int index = 0;
	
	public static WorkloadParser<List<Request>> getWorkloadParser(){
		return getWorkloadParser(Configuration.getInstance().getParserPageSize().getTickInMillis());
	}

	@SuppressWarnings("unchecked")
	public static WorkloadParser<List<Request>> getWorkloadParser(long pageSize){
		
		assert pageSize > 0: "Invalid page size";
		
		Configuration config = Configuration.getInstance();
		String[] workloads = config.getWorkloads();
		ParserIdiom parserIdiom = config.getParserIdiom();
		
		switch (parserIdiom) {
			case GEIST:
				String[] workloadFiles = config.getWorkloads();
				
				WorkloadParser<Request>[] parsers = new WorkloadParser[workloadFiles.length];
				if(workloadFiles.length == 1){
					return new TimeBasedWorkloadParser(pageSize, new GEISTSingleFileWorkloadParser(workloads[0]));
				}
				
				for(int i =0; i < workloadFiles.length; i++){
					parsers[i] = new GEISTMultiFileWorkloadParser(workloads[i], index++);
				}
				return new TimeBasedWorkloadParser(pageSize, parsers);
			default:
				throw new RuntimeException("No parser specified for value " + parserIdiom);
		}
	}
}
