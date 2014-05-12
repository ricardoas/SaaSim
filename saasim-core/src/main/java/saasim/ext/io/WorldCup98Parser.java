package saasim.ext.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import saasim.core.application.Request;
import saasim.core.io.TraceParcer;
import saasim.ext.application.WebAppRequest;


/**
 * GEIST parser. A GEIST workload file contains one request per line.
 * Each line contains nine tab separated columns ordered as follow:<br>
 * <ul>
 * 	<li>User ID</li>
 * 	<li>Request ID</li>
 * 	<li>Time</li>
 * 	<li>Request size in Bytes</li>
 * 	<li>Response size in Bytes (Size of requested file)</li>
 * 	<li> Array of demand in millis </li>
 * </ul>
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class WorldCup98Parser implements TraceParcer{
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Request parseRequest(String line, int tenantID) {
		StringTokenizer tokenizer = new StringTokenizer(line, "( +|\t+)+");
		
		int userID = Integer.parseInt(tokenizer.nextToken());
		long reqID = Long.parseLong(tokenizer.nextToken());
		long arrivalTimeInMillis = Long.parseLong(tokenizer.nextToken());
		long requestSizeInBytes = Long.parseLong(tokenizer.nextToken());
		long responseSizeInBytes = Long.parseLong(tokenizer.nextToken());
		
		long [] demand = new long[tokenizer.countTokens()];
		int index = 0;
		while(tokenizer.hasMoreTokens()){
			demand[index++] = Long.parseLong(tokenizer.nextToken());
		}
		return new WebAppRequest(reqID, tenantID, userID, arrivalTimeInMillis,
				requestSizeInBytes, responseSizeInBytes, demand);
	}
	
//	public static void main(String[] args) throws IOException {
//		String file = "/Users/ricardo/Downloads/worldcup98/wc_day5_1";
//		
//		GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(new File(file+".gz")));
//		byte[] b = new byte[4];
//		inputStream.read(b);
//		
//		int first, second, third, fourth;
//            int index = 0;
//            first = ((int) (b[index++])) & 0x000000FF;
//            second = ((int) (b[index++])) & 0x000000FF;
//            third = ((int) (b[index++])) & 0x000000FF;
//            fourth = ((int) (b[index++])) & 0x000000FF;
//		
//		long uint = ((long) (first << 24 | second << 16 | third << 8 | fourth)) & 0xFFFFFFFFL;
//		System.out.println(uint);
//	}
}
