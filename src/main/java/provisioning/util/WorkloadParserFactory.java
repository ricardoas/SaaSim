/**
 * 
 */
package provisioning.util;

import java.util.List;

import commons.cloud.Request;
import commons.cloud.User;
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
			List<User> users = config.getUsers();
			for (int i = 0; i < parsers.length; i++) {
				parsers[i] = new GEISTMultiFileWorkloadParser(workloads[i], users.get(i).getId());
			}
			return new TimeBasedWorkloadParser(pageSize, parsers);
		default:
			throw new RuntimeException("No parser specified for value " + parserIdiom);
		}
	}

}
