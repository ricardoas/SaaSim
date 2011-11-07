/**
 * 
 */
package commons.io;

import java.util.List;
import java.util.NoSuchElementException;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.util.SimulatorProperties;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class WorkloadParserFactory {
	
	private static int index = 0;
	
	public static WorkloadParser<List<Request>> getWorkloadParser(){
		return getWorkloadParser(Configuration.getInstance().getParserPageSize().getMillis());
	}

	@SuppressWarnings("unchecked")
	public static WorkloadParser<List<Request>> getWorkloadParser(long pageSize){
		
		assert pageSize > 0: "Invalid page size";
		
		Configuration config = Configuration.getInstance();
		String[] workloads = config.getWorkloads();
		ParserIdiom parserIdiom = config.getParserIdiom();
		
		boolean useError;
		try{
			useError = config.getBoolean(SimulatorProperties.USE_ERROR);
		}catch(NoSuchElementException e){
			useError = false;
		}
		
		
		switch (parserIdiom) {
			case GEIST:
				String[] workloadFiles = config.getWorkloads();
				
				WorkloadParser<Request>[] parsers = new WorkloadParser[workloadFiles.length];
				if(workloadFiles.length == 1){
					if(useError){
						return new TimeBasedWorkloadParserWithError(pageSize, new GEISTMultiFileWorkloadParser(workloads[0], 0));
					}else{
						return new TimeBasedWorkloadParser(pageSize, new GEISTMultiFileWorkloadParser(workloads[0], index++));
					}
				}
				
				for(int i =0; i < workloadFiles.length; i++){
					parsers[i] = new GEISTMultiFileWorkloadParser(workloads[i], index++);
				}
				if(useError){
					return new TimeBasedWorkloadParserWithError(pageSize, parsers);
				}else{
					return new TimeBasedWorkloadParser(pageSize, parsers);
				}
			default:
				throw new RuntimeException("No parser specified for value " + parserIdiom);
		}
	}
}
