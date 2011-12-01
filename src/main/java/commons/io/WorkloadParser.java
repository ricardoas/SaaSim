package commons.io;

import java.io.IOException;

/**
 * Common set of features for workload parsers.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @param <T> To represent the granularity you
 */
public interface WorkloadParser<T> {

	/**
	 * Reads and returns the next portion of data from the workload. Note that
	 * the read and return process depends on the implementation.
	 * @return
	 * @throws RuntimeException Encapsulation of {@link IOException}. 
	 */
	public T next();

	/**
	 * Returns a value about existence the next portion of data from the workload.
	 * @return <code>true</code> if has next, <code>false</code> otherwise.
	 */
	public boolean hasNext();

	/**
	 * Clean the workload.
	 */
	public void clear();

	/**
	 * Modified the number of days already read from workload.
	 * @param simulatedDays number of days set the simulation
	 */
	public void setDaysAlreadyRead(int simulatedDays);

	/**
	 * Close the workload.
	 */
	public void close();

	/**
	 * Gets the size of workload.
	 * @return the size of workload
	 */
	public int size();

}
