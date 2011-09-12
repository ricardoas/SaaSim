package commons.io;

import java.io.IOException;

/**
 * Common set of features for workload parsers.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * 
 * @param <T> To represent the granularity you
 */
public interface WorkloadParser<T> {

	/**
	 * Reads and returns the next portion of data from the workload. Note that
	 * the read and return process depends on the implementation.
	 * 
	 * @return
	 * @throws RuntimeException Encapsulation of {@link IOException}. 
	 */
	T next();

	/**
	 * @return
	 */
	boolean hasNext();

}
