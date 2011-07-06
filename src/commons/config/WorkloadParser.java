package commons.config;

import java.io.IOException;

/**
 * Common set of features for workload parsers.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 * 
 * @param <T> To represent the granularity you
 */
public interface WorkloadParser<T> {

	/**
	 * Reads and returns the next portion of data from the workload. Note that
	 * the read and return process can be independent depending on the
	 * implementation.
	 * 
	 * @return
	 * @throws IOException 
	 */
	T next() throws IOException;

	/**
	 * @return
	 */
	boolean hasNext();

}
