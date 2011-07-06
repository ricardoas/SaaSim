package config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import commons.cloud.Request;
import commons.config.WorkloadParser;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class GEISTSimpleWorkloadParser implements WorkloadParser<Request> {
	
	private static final int DEFAULT_PAGE_SIZE = 100;

	private BufferedReader reader;
	
	private Queue<Request> queue;
	
	private int pageSize;

	public GEISTSimpleWorkloadParser(String workloadFileName) {
		
		try {
			reader = new BufferedReader(new FileReader(workloadFileName));
			queue = new LinkedList<Request>();
			pageSize = DEFAULT_PAGE_SIZE;
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
    	checkQueueContent();
    	return queue.poll();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		checkQueueContent();
		return queue.isEmpty();
	}
	
	/**
	 * 
	 */
	private void checkQueueContent(){
		try {
			if(queue.isEmpty()){
				String line;
				int page = 0;
				if(page++ < pageSize && (line = reader.readLine()) != null){
					queue.add(parseRequest(line));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL
	 * @param line
	 * @return
	 */
	private Request parseRequest(String line) {
		String[] eventData = line.split("( +|\t+)+");
		return new Request(eventData[0], eventData[2], Double
				.valueOf(eventData[3]), Double.valueOf(eventData[4]),
				(eventData[5].contains("1")) ? true : false, eventData[6],
				eventData[7]);
	}

}
