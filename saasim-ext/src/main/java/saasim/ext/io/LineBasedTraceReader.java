package saasim.ext.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import saasim.core.io.TraceReader;
import saasim.core.saas.Request;


/**
 * Implementation of {@link TraceReader} capable of read a file trace where each line represents a {@link Request}. 
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class LineBasedTraceReader implements TraceReader{
	
	
	private BufferedReader reader;
	protected int tenantID;

	public LineBasedTraceReader() {
		
	}
	
	@Override
	public void setUp(String fileName, int tenantID) throws FileNotFoundException {
		this.tenantID = tenantID;
		this.reader = new BufferedReader(new FileReader(fileName));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Request next() {
		String line;
		try {
			line = reader.readLine();
			return line == null? null: parseRequest(line);
		} catch (Exception e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}

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
