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
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SaaSUsersProperties;
import commons.util.SimulationInfo;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class WorkloadParserFactory {
	
	private static int index = 0;
	private static JEEventScheduler scheduler;
	
	public static WorkloadParser<List<Request>> getWorkloadParser(){
		return getWorkloadParser(Configuration.getInstance().getParserPageSize().getTickInMillis());
	}

	@SuppressWarnings("unchecked")
	public static WorkloadParser<List<Request>> getWorkloadParser(long pageSize){
		Configuration config = Configuration.getInstance();
		String[] workloads = config.getWorkloads();
		ParserIdiom parserIdiom = config.getParserIdiom();
		SimulationInfo simulationInfo = config.getSimulationInfo();
		
		switch (parserIdiom) {
			case GEIST:
				int numberOfUsers = config.getInt(SaaSUsersProperties.SAAS_NUMBER_OF_USERS);
				WorkloadParser<Request>[] parsers = new WorkloadParser[numberOfUsers];
				if(numberOfUsers == 1){
					TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, pageSize, new GEISTSingleFileWorkloadParser(workloads));
					return parser;
				}
				for(int i =0; i < numberOfUsers; i++){
					parsers[i] = new GEISTMultiFileWorkloadParser(workloads, index++);
				}
				return new TimeBasedWorkloadParser(scheduler, pageSize, parsers);
			default:
				throw new RuntimeException("No parser specified for value " + parserIdiom);
		}
	}
	
	public static void setScheduler(JEEventScheduler scheduler) {
		WorkloadParserFactory.scheduler = scheduler;
	}

}
