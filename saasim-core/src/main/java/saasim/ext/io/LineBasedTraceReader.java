package saasim.ext.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import saasim.core.application.Request;
import saasim.core.io.TraceParcer;
import saasim.core.io.TraceReader;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;


/**
 * Implementation of {@link TraceReader} capable of read a file trace where each line represents a {@link Request}. 
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class LineBasedTraceReader implements TraceReader<Request>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TraceParcer workloadParser;
	private BufferedReader reader;

	private int tenantID;

	/**
	 * Default constructor.
	 * 
	 * @param filename workload file name
	 * @param traceParser translate trace lines into {@link Request}s.
	 * @param fileName 
	 */
	@Inject
	public LineBasedTraceReader(@Assisted String fileName, @Assisted int tenantID, TraceParcer traceParser) {
		this.tenantID = tenantID;
		this.workloadParser = traceParser;
		
		try {
			this.reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file. ", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Request next() {
		String line;
		try {
			line = reader.readLine();
			return line == null? null: workloadParser.parseRequest(line, tenantID);
		} catch (Exception e) {
			throw new RuntimeException("Problem reading workload file.", e);
		}
	}

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
