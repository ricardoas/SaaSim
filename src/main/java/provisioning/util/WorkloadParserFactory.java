/**
 * 
 */
package provisioning.util;

import java.util.List;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.GEISTMultiFileWorkloadParser;
import commons.io.GEISTSingleFileWorkloadParser;
import commons.io.ParserIdiom;
import commons.io.TimeBasedWorkloadParser;
import commons.io.WorkloadParser;

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
		Configuration config = Configuration.getInstance();
		String[] workloads = config.getWorkloads();
		ParserIdiom parserIdiom = config.getParserIdiom();
		switch (parserIdiom) {
			case GEIST:
				WorkloadParser<Request>[] parsers = new WorkloadParser[workloads.length];
				if(workloads.length == 1){
					return new TimeBasedWorkloadParser(pageSize, new GEISTSingleFileWorkloadParser(workloads[0]));
				}
				for (int i = 0; i < parsers.length; i++) {
					parsers[i] = new GEISTMultiFileWorkloadParser(workloads[i], index++);
				}
				return new TimeBasedWorkloadParser(pageSize, parsers);
			default:
				throw new RuntimeException("No parser specified for value " + parserIdiom);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static WorkloadParser<Request>[] createNewParsers(long pageSize, String workloadFile, int numberOfParsers){
		Configuration config = Configuration.getInstance();
		ParserIdiom parserIdiom = config.getParserIdiom();
		switch (parserIdiom) {
			case GEIST:
				WorkloadParser<Request>[] parsers = new WorkloadParser[numberOfParsers];
				for (int i = 0; i < numberOfParsers; i++) {
					parsers[i] = new GEISTMultiFileWorkloadParser(workloadFile, index++);
				}
				return parsers;
			default:
				throw new RuntimeException("No parser specified for value " + parserIdiom);
		}	
	}

}
