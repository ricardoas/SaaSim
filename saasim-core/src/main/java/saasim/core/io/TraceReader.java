package saasim.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import saasim.core.application.Request;

/**
 * Common set of features for workload parsers.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @param <T> To represent the granularity you want to read.
 */
public interface TraceReader{

	/**
	 * Reads and returns the next portion of data from the workload. Note that
	 * the read and return process depends on the implementation.
	 * @return
	 * @throws RuntimeException when {@link IOException} is launched. 
	 */
	public Request next();

	/**
	 * Close the workload.
	 */
	public void close();

	void setUp(String file, int tenantID) throws FileNotFoundException;

}
