package commons.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import commons.cloud.Request;
import commons.config.Configuration;

/**
 * Represents a abstract {@link WorkloadParser}.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractWorkloadParser implements WorkloadParser<Request>{
	
	protected static int saasClientIDSeed = 0;
	
	private BufferedReader reader;
	private int currentDay = 0;
	
	protected int periodsAlreadyRead = 0;
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
		
		String workloadFile = readFileToUse(workload);
		try {
			this.saasClientID = saasClientIDSeed++;
			this.reader = new BufferedReader(new FileReader(workloadFile));//Using normal load file
			this.next = readNext();
		} catch (FileNotFoundException e) {
			if(workloadFile.isEmpty()){
				throw new RuntimeException("Blank line in " + workload + " file." , e);
			}
			throw new RuntimeException("Problem reading workload file. ", e);
		}
	}
	
	/**
	 * Read file to be used for this {@link AbstractWorkloadParser}.
	 * @param workload The file to read.
	 * @return The content of file.
	 */
	private String readFileToUse(String workload) {
		this.currentDay = Configuration.getInstance().getSimulationInfo().getCurrentDay();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(workload));
			String file = reader.readLine();
			int currentLine = 0;
			while(currentLine < this.currentDay){
				currentLine++;
				file = reader.readLine();
			}
			reader.close();
			return file == null? "": file;
		} catch (Exception e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDaysAlreadyRead(int simulatedDays){
		throw new RuntimeException("not yet implemented");
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
