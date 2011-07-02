package config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import commons.cloud.Request;
import commons.config.WorkloadParser;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class GEISTSimpleWorkloadParser implements WorkloadParser<Request> {
	
	private BufferedReader reader;

	public GEISTSimpleWorkloadParser(String workloadFileName) {
		
		try {
			reader = new BufferedReader(new FileReader(workloadFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Request next() {
    	return null;
    }

}
