/**
 * 
 */
package commons.io;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

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
		
		int workloadSize = (int) Math.round(workloads.length * (1+config.getDouble(SimulatorProperties.PLANNING_ERROR, 0.0)));
		
		String[] workloadFilesWithErrors = Arrays.copyOf(workloads, workloadSize);
		
		for (int i = 0; i < workloadFilesWithErrors.length; i++) {
			if(workloadFilesWithErrors[i] == null){
				workloadFilesWithErrors[i] = workloads[new Random().nextInt(workloads.length)];
			}
		}
		
		WorkloadParser<Request>[] parsers = new WorkloadParser[workloads.length];
		for (int i = 0; i < workloadFilesWithErrors.length; i++) {
			parsers[i] = parserIdiom.getInstance(workloadFilesWithErrors[0]);
		}
		
		return new TimeBasedWorkloadParser(pageSize, parsers);
	}
}
