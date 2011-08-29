package commons.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import commons.cloud.Request;

public abstract class AbstractWorkloadParser implements WorkloadParser<Request> {
	
	private BufferedReader reader;

	private Request next;

	public AbstractWorkloadParser(String... workloadPath) {
		try {
			reader = new BufferedReader(new FileReader(workloadPath[0]));
			next = readNext();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}

	@Override
	public Request next() {
		Request toReturn = next;
		next = readNext();
		return toReturn;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}
	
	/**
	 * @return
	 */
	private Request readNext() {
		String line;
		try {
			line = reader.readLine();
			if(line == null){
				return null;
			}
			return parseRequest(line);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}

	/**
	 * @param line
	 * @return
	 */
	protected abstract Request parseRequest(String line);


}
