package saasim.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;


public class AccessLogParser {
	
	static class Request{
		long arrival;
		double demand = 0;
		int simblings;
		int concurrencyCounter;
		
		public Request(long arrival) {
			this.arrival = arrival;
		}
	}
	
	static class Event implements Comparable<Event>{
		long time;
		boolean start;
		Request r;
		public Event(long time, boolean start, Request r) {
			this.time = time;
			this.start = start;
			this.r = r;
		}
		@Override
		public int compareTo(Event o) {
			return (int) (time != o.time? time - o.time: !start?-1:1);
		}
		@Override
		public String toString() {
			return "Event [time=" + time + ", start=" + start + ", r=" + r
					+ "]";
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		String filename = args[0];
		
		int index = Integer.valueOf(args[1]);
		
		Scanner input = new Scanner(new File(filename));
		
		ArrayList<Request> requests = new ArrayList<Request>();
		
		TreeSet<Event> events = new TreeSet<Event>();
		
		while(input.hasNextLine()){
			String[] tokens = input.nextLine().split(" +");
			Request r = new Request(Long.valueOf(tokens[index]));
			events.add(new Event(Long.valueOf(tokens[index]), true, r));
			events.add(new Event(Long.valueOf(tokens[index+1]), false, r));
		}
		
		long min = events.first().time;
		long previousTime = min;
		
		int tomcatThread=0;
		int monitor = 1;
		
		int concurrencyCounter = 0;
		long prevArrival = -1000;
		
		for (Event event : events) {
			if(event.start){
				
				double d = (1.0 * event.time - previousTime)/(requests.size()*monitor+tomcatThread);
				Iterator<Request> it = requests.iterator();
				while (it.hasNext()) {
					Request request = (Request) it.next();
					request.demand += d;
				}
				event.r.simblings = requests.size();
				if(requests.size() == 0){
					if(event.r.arrival - prevArrival < 40){
						concurrencyCounter++;
					}else{
						concurrencyCounter = 0;
					}
				}else{
					concurrencyCounter++;
				}
				event.r.concurrencyCounter = concurrencyCounter;
				requests.add(event.r);
			}else{
				double d = (1.0 * event.time - previousTime)/(requests.size()*monitor+tomcatThread);
				Iterator<Request> it = requests.iterator();
				while (it.hasNext()) {
					Request request = (Request) it.next();
					request.demand += d;
					if(request.equals(event.r)){
						it.remove();
						System.out.println("1 1 " + (request.arrival) + " 1 1 " + (long)Math.round(request.demand) + " " + request.simblings + " " + request.concurrencyCounter);
					}
				}
			}
			previousTime = event.time;
			prevArrival = previousTime;
		}
	}

//	1796.5
//	1796
//	872.5
//	872
//	449.4166666666667
//	449
//	884.0833333333334
//	884
//	878.8833333333333
//	878
//	881.7166666666667
//	881
//	458.6333333333333
//	458
//	459.6333333333333
//	459
//	673.0
//	673
//	1134.6333333333332
//	1134

}
