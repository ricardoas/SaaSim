package saasim.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import saasim.cloud.Request;


/**
 * Represents a abstract {@link WorkloadParser}.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractWorkloadParser implements WorkloadParser<Request>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9167171166836872343L;

	protected static int saasClientIDSeed = 0;
	
	private BufferedReader reader;
	private Request next;
	
	protected final int saasClientID;
	protected final long shift;
	protected final String workload;

	/**
	 * Default constructor.
	 * 
	 * @param workload workload file name
	 * @param shift TODO 
	 */
	public AbstractWorkloadParser(String workload, long shift) {
		this.workload = workload;
		assert workload != null: "Null workload. Please check your configuration and trace files.";
		
		this.shift = shift;
		
		this.saasClientID = saasClientIDSeed++;
		try {
			this.reader = new BufferedReader(new FileReader(workload));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file. ", e);
		}
		this.next = readNext();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		throw new RuntimeException("not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Request next() {
		Request toReturn = this.next;
		this.next = readNext();
		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return next != null;
	}
	
	/**
	 * Read the next {@link Request} in the file, using {@link #parseRequest(String)}.
	 * @return the {@link Request}
	 */
	private Request readNext() {
		String line;
		try {
			line = reader.readLine();
			return line == null? null: parseRequest(line);
		} catch (Exception e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}

	/**
	 * Parse a line of file from workload in a {@link Request}.
	 * @param line line to be parsed in a {@link Request}
	 * @return the {@link Request}
	 */
	protected abstract Request parseRequest(String line);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close(){
		try {
			this.reader.close();
		} catch (IOException e) {
			throw new RuntimeException("Problem closing workload file.", e);
		}
	}
}
