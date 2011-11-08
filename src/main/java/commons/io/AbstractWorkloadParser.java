package commons.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import commons.cloud.Request;

/**
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
	 * @param workload Workload file name.
	 * @param saasclientID SaaS client ID.
	 * @param shift TODO
	 */
	public AbstractWorkloadParser(String workload, int saasclientID, long shift) {
		this.workload = workload;
		assert workload != null: "Null workload. Please check your configuration and trace files.";
		
		this.shift = shift;
		
		String workloadFile = readFileToUse(workload);
		try {
			this.saasClientID = saasclientID;
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
	 * 
	 * @param workload
	 * @return
	 */
	private String readFileToUse(String workload) {
		this.currentDay = Checkpointer.loadSimulationInfo().getCurrentDay();
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
	
	@Override
	public void clear() {
		throw new RuntimeException("not yet implemented");
	}

	@Override
	public Request next() {
		Request toReturn = this.next;
		this.next = readNext();
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
			return line == null? null: parseRequest(line);
		} catch (Exception e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}

	/**
	 * @param line
	 * @return
	 */
	protected abstract Request parseRequest(String line);

	@Override
	public void close(){
		try {
			this.reader.close();
		} catch (IOException e) {
			throw new RuntimeException("Problem closing workload file.", e);
		}
	}
}
