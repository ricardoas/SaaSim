package commons.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import commons.cloud.Request;

public abstract class AbstractWorkloadParser implements WorkloadParser<Request> {
	
	private static final int TRACE_LENGTH_IN_DAYS = 1;
	private BufferedReader reader;
	private final StringBuilder[] workloadPath;
	private int periodType;
	
	protected int periodsAlreadyRead = 0;

	private Request next;

	public AbstractWorkloadParser(String[] workloads, int saasclientID) {
		this.workloadPath = new StringBuilder[workloads.length];
		this.periodType = 0;
		for(int i = 0; i < workloads.length; i++){
			this.workloadPath[i] = new StringBuilder(workloads[i]);
//			this.workloadPath[i].append("/"+saasclientID+".trc");
			this.workloadPath[i].append("/trace_user.trc");
		}
		try {
			this.reader = new BufferedReader(new FileReader(this.workloadPath[periodType].toString()));//Using normal load file
			
//			if(periodsAlreadyRead  < Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
				this.next = readNext();
//			}else{
//				this.next = null;
//			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}
	
	@Override
	public void setDaysAlreadyRead(int simulatedDays){
		throw new RuntimeException("not yet implemented");
//		this.periodsAlreadyRead = simulatedDays;
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
			if(line == null){
				return null;
//				System.gc();
//
//				periodsAlreadyRead += TRACE_LENGTH_IN_DAYS;
//				System.out.println("OTHER DAY: "+periodsAlreadyRead);
//				if(periodsAlreadyRead  < Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
//					reader.close();
//					reader = null;
//					reader = new BufferedReader(new FileReader( this.workloadPath[periodType].toString() ) );
//					line = reader.readLine();
//					System.out.println("new line: "+line);
				}
			return parseRequest(line);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}
	
	public int changeToPeak(){
		try {
			this.periodType = 2;
			this.reader.close();
			reader = null;
			this.reader = new BufferedReader(new FileReader(this.workloadPath[periodType].toString()));//Using peak load file
			
//			periodsAlreadyRead += TRACE_LENGTH_IN_DAYS;
//			if(periodsAlreadyRead  < Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
				this.next = readNext();
//			}else{
//				this.next = null;
//			}
			return periodsAlreadyRead;
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}
	
	public int changeToTransition(){
		try {
			this.periodType = 1;
			this.reader.close();
			reader = null;
			this.reader = new BufferedReader(new FileReader(this.workloadPath[periodType].toString()));//Using transition load file
		
//			periodsAlreadyRead += TRACE_LENGTH_IN_DAYS;
//			if(periodsAlreadyRead  < Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
				this.next = readNext();
//			}else{
//				this.next = null;
//			}
			
			return periodsAlreadyRead;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}
	
	public int changeToNormal(){
		try {
			this.periodType = 0;
			this.reader.close();
			reader = null;
			this.reader = new BufferedReader(new FileReader(this.workloadPath[periodType].toString()));//Using normal load file
			
//			periodsAlreadyRead += TRACE_LENGTH_IN_DAYS;
//			if(periodsAlreadyRead  < Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
				this.next = readNext();
//			}else{
//				this.next = null;
//			}
			
			return periodsAlreadyRead;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file.", e);
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
