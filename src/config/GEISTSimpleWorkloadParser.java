package config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.config.WorkloadParser;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class GEISTSimpleWorkloadParser implements WorkloadParser<List<Request>> {
	

	private static final int HOUR_IN_MILLIS = 3600000;

	private BufferedReader reader;
	
	private Request temp;

	/**
	 * @param workloadFileName
	 */
	public GEISTSimpleWorkloadParser(String workloadFileName) {
		
		try {
			reader = new BufferedReader(new FileReader(workloadFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return temp != null || readNext();
	}
	
	/**
	 * @return
	 */
	private boolean readNext() {
		String line;
		try {
			line = reader.readLine();
			return line != null? (temp = parseRequest(line)) != null :false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL
	 * @param line
	 * @return
	 */
	private Request parseRequest(String line) {
		String[] eventData = line.split("( +|\t+)+");
		return new Request(eventData[0], eventData[2], Long
				.valueOf(eventData[3]), Long.valueOf(eventData[4]),
				(eventData[5].contains("1")) ? true : false, eventData[6],
				eventData[7]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Request> next() throws IOException {
		List<Request> requests = new ArrayList<Request>();
		if(temp == null){
			readNext();
		}
		int currentHour = (int) (temp.time/HOUR_IN_MILLIS);
		do{
			requests.add(temp);
			readNext();
		}while(temp != null && currentHour != (int)(temp.time/HOUR_IN_MILLIS));
		
		return requests;
	}

}
