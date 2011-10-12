package commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is responsible for evaluating a geist trace file in order to aggregate the amount of
 * requests at each interval of MINUTE_IN_MILLIS. 
 * 
 * @author david
 *
 */
public class WorkloadAnalyser {
	
	private static final Pattern pattern = Pattern.compile("( +|\t+)+");
	private static long MINUTE_IN_MILLIS = 1000 * 60 * 5;
	
	public static void main(String[] args) {
		if(args.length < 2){
			System.err.println("<Geist trace file to be aggregated!> <output file>");
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			String line = reader.readLine();
			List<Integer> requestCounter = new ArrayList<Integer>();
			int counter = 0;
			int index = 1;
			
			while(line != null){
				String[] eventData = pattern.split(line);
				long arrivalTime = Long.valueOf(eventData[2]);
				if(arrivalTime <= index * MINUTE_IN_MILLIS){
					counter++;
				}else{
					index++;
					requestCounter.add(counter);
					counter = 0;
				}
				line = reader.readLine();
			}
			
			FileWriter writer = new FileWriter(new File(args[1]));
			for(int i = 0; i < requestCounter.size(); i++){
				writer.write(i+1+"\t"+requestCounter.get(i)+"\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
