package saasim.ext.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

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
public class WorldCup98TraceReader implements TraceReader<Request>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TraceParcer workloadParser;
	private GZIPInputStream reader;

	private int tenantID;

	/**
	 * Default constructor.
	 * 
	 * @param filename workload file name
	 * @param traceParser translate trace lines into {@link Request}s.
	 * @param fileName 
	 */
	@Inject
	public WorldCup98TraceReader(@Assisted String fileName, @Assisted int tenantID, TraceParcer traceParser) {
		this.tenantID = tenantID;
		this.workloadParser = traceParser;
		
		try {
			this.reader = new GZIPInputStream(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem reading workload file. ", e);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading workload file. ", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Request next() {
		
		try {
			byte[] b = new byte[4];

			if(reader.read(b) == -1){
				return null;
			}

			int first, second, third, fourth;
			int index = 0;
			first = ((int) (b[index++])) & 0x000000FF;
			second = ((int) (b[index++])) & 0x000000FF;
			third = ((int) (b[index++])) & 0x000000FF;
			fourth = ((int) (b[index++])) & 0x000000FF;

			long uint = ((long) (first << 24 | second << 16 | third << 8 | fourth)) & 0xFFFFFFFFL;

			return workloadParser.parseRequest(null, tenantID);
		} catch (IOException e) {
			return null;
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
