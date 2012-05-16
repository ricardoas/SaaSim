package saasim.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.util.TimeUnit;


/**
* A different implementation of {@link WorkloadParser}, based on time.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeBasedWorkloadParser implements WorkloadParser<List<Request>>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 587196214235851553L;
	protected final long tick;
	protected long currentTick;

	protected Request[] leftOver;
	protected WorkloadParser<Request>[] parsers;
	private String[] workloads;
	private int workloadLine;
	
	/**
	 * Default constructor.
	 * @param tick a long represent a moment in time
	 * @param parser an array without specific size, containing {@link WorkloadParser}.
	 */
	public TimeBasedWorkloadParser(long tick, WorkloadParser<Request>... parser) {
		if(parser.length == 0){
			throw new RuntimeException("Invalid TimeBasedWorkloadParser: no parsers!");
		}
		this.parsers = parser;
		this.tick = tick;
		this.currentTick = tick;//Configuration.getInstance().getSimulationInfo().getCurrentDayInMillis() + tick;
		this.leftOver = new Request[parsers.length];
	}
	
	/**
	 * Default constructor.
	 * @param tick a long represent a moment in time
	 * @param parser an array without specific size, containing {@link WorkloadParser}.
	 */
	@SuppressWarnings("unchecked")
	public TimeBasedWorkloadParser(long tick, String... workloads) {
		if(workloads.length == 0){
			throw new RuntimeException("Invalid TimeBasedWorkloadParser: no parsers!");
		}
		this.workloads = workloads;
		this.workloadLine = 0;
		this.tick = tick;
		this.currentTick = tick;
		
		this.parsers = new WorkloadParser[workloads.length];
		
		ParserIdiom idiom = Configuration.getInstance().getParserIdiom();
		
		for (int i = 0; i < workloads.length; i++) {
			parsers[i] = idiom.getInstance(readFileToUse(workloads[i]), 0);
		}

		this.leftOver = new Request[parsers.length];
	}
	
	/**
	 * Read file to be used for this {@link AbstractWorkloadParser}.
	 * @param workload The file to read.
	 * @return The content of file.
	 */
	private String readFileToUse(String workload) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(workload));
			String file = reader.readLine();
			int currentLine = 0;
			while(currentLine < this.workloadLine){
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
	public void clear() {
		workloadLine++;
		
		ParserIdiom idiom = Configuration.getInstance().getParserIdiom();
		
		for (int i = 0; i < workloads.length; i++) {
			parsers[i] = idiom.getInstance(readFileToUse(workloads[i]), workloadLine * TimeUnit.DAY.getMillis());
		}
	}

	/**
	 * Returns a list containing {@link Request}s have been read. If the arrival time of next request in {@link WorkloadParser}
	 * is less than current time, it's added in the list, otherwise, it's going to an array where it's stored while doesn't
	 * satisfies the condition.
	 */
	@Override
	public List<Request> next(){
		List<Request> requests = new ArrayList<Request>();
		
		for (int i = 0; i < leftOver.length; i++) {
			Request left = leftOver[i];
			if(left != null){
				if(left.getArrivalTimeInMillis() < currentTick){
					requests.add(left);
					leftOver[i] = null;
				}
			}
		}
		
		for (int i = 0; i < parsers.length; i++) {
			if(leftOver[i] == null){
				WorkloadParser<Request> parser = parsers[i];
				while(parser.hasNext()){
					Request next = parser.next();
					if(next.getArrivalTimeInMillis() < currentTick){
						requests.add(next);
					}else{
						leftOver[i] = next;
						break;
					}
				}
			}
		}
		this.currentTick += tick;
		return requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		for (int i = 0; i < parsers.length; i++) {
			if(leftOver[i] != null || parsers[i].hasNext()){
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		for(WorkloadParser<Request> parser : parsers){
			parser.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return parsers.length;
	}
}
