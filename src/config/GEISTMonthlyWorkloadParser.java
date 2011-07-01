package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.cloud.User;
import commons.config.WorkloadParser;

/**
 * This class is responsible for parsing the workload to be used. Considering SaaS plans limits, the workload should be separated according to each period
 * in the plan, e.g. each month, and each user.
 * @author davidcmm
 *
 */
public class GEISTMonthlyWorkloadParser implements WorkloadParser<Map<User, List<Request>>>{
	
	private static double JAN_L = 60 * 60 * 24 * (31);
	private static double FEB_L = 60 * 60 * 24 * (31+28);
	private static double MAR_L = 60 * 60 * 24 * (31+28+31);
	private static double APR_L = 60 * 60 * 24 * (31+28+31+30);
	private static double MAY_L = 60 * 60 * 24 * (31+28+31+30+31);
	private static double JUN_L = 60 * 60 * 24 * (31+28+31+30+31+30);
	private static double JUL_L = 60 * 60 * 24 * (31+28+31+30+31+30+31);
	private static double AUG_L = 60 * 60 * 24 * (31+28+31+30+31+30+31+31);
	private static double SEP_L = 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30);
	private static double OCT_L = 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30+31);
	private static double NOV_L = 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30+31+30);
	private static double DEC_L = 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30+31+30+31);
	
	private final String[] workloadFiles;
	private final BufferedReader [] readers;
	private int currentMonth;
	private Map<User, List<Request>> nextRequests;
	
	public GEISTMonthlyWorkloadParser(String ...workloadFiles){
		this.workloadFiles = workloadFiles;
		this.readers = new BufferedReader[workloadFiles.length];
		for(int i = 0; i < workloadFiles.length; i++){
			try {
				readers[i] = new BufferedReader(new FileReader(new File(workloadFiles[i])));
			} catch (FileNotFoundException e) {
			}
		}
		this.currentMonth = 1;
		this.nextRequests = new HashMap<User, List<Request>>();
	}
	
	@Override
	public Map<User, List<Request>> next() throws IOException {
		HashMap<User, List<Request>> currentWorkload = new HashMap<User, List<Request>>();
		int nextMonth = Integer.MAX_VALUE;
		
		//Verifying if any event was stored in previous read
		if(this.nextRequests.size() != 0){
			currentWorkload.putAll(this.nextRequests);
			this.nextRequests.clear();
		}
		
		for(int i = 0; i < this.readers.length; i++){
			BufferedReader reader = this.readers[i];
			while(reader.ready()){
				String[] eventData = reader.readLine().split("( +|\t+)+");//Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL
				Request request = new Request(eventData[0], eventData[2], Double.valueOf(eventData[3]), 
						Double.valueOf(eventData[4]), (eventData[5].contains("1")) ? true : false, eventData[6], eventData[7] );
				
				//Adding new event to its corresponding user
				int monthOfEvent = getMonthOfEvent(request.time);
				if(monthOfEvent == this.currentMonth){//An event of current iteration was found
					User user = new User(eventData[1]);//Users are identified uniquely by their ids
					List<Request> userWorkload = currentWorkload.get(user);
					if(userWorkload == null){
						userWorkload = new ArrayList<Request>();
						currentWorkload.put(user, userWorkload);
					}
					userWorkload.add(request);
				}else{
					User user = new User(eventData[1]);//Users are identified uniquely by their ids
					List<Request> userWorkload = this.nextRequests.get(user);
					if(userWorkload == null){
						userWorkload = new ArrayList<Request>();
						this.nextRequests.put(user, userWorkload);
					}
					userWorkload.add(request);
					if(monthOfEvent < nextMonth){
						nextMonth = monthOfEvent;
					}
					break;//Finishing current read
				}
			}
		}
		if(nextMonth != Integer.MAX_VALUE){
			this.currentMonth = nextMonth;
		}else{
			this.currentMonth++;
		}
	    return currentWorkload;
	}
	
	@Deprecated
	public static Map<Integer, Map<User, List<Request>>> getWorkloadPerMonth(String ... workloadFiles) throws NumberFormatException, IOException{
		HashMap<Integer, Map<User, List<Request>>> workload = new HashMap<Integer, Map<User, List<Request>>>();
		
		for(String workloadFile : workloadFiles){
			BufferedReader reader = new BufferedReader(new FileReader(new File(workloadFile)));
			while(reader.ready()){
				String[] eventData = reader.readLine().split("( +|\t+)+");//Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL
				Request request = new Request(eventData[0], eventData[2], Double.valueOf(eventData[3]), 
						Double.valueOf(eventData[4]), (eventData[5].contains("1")) ? true : false, eventData[6], eventData[7] );
				
				//Adding new event to its corresponding user and month
				int monthOfEvent = getMonthOfEvent(request.time);
				Map<User, List<Request>> monthWorkload  = workload.get(monthOfEvent);
				if(monthWorkload == null){
					monthWorkload = new HashMap<User, List<Request>>();
					workload.put(monthOfEvent, monthWorkload);
				}
				
				User user = new User(eventData[1]);//Users are identified uniquely by their ids
				List<Request> userWorkload = monthWorkload.get(user);
				if(userWorkload == null){
					userWorkload = new ArrayList<Request>();
					monthWorkload.put(user, userWorkload);
				}
				userWorkload.add(request);
			}
		}
		
		return workload;
	}
	
	private static int getMonthOfEvent(double time){
		if(time <= JAN_L){
			return 1;
		}else if(time > JAN_L && time <= FEB_L){
			return 2;
		}else if(time > FEB_L && time <= MAR_L){
			return 3;
		}else if(time > MAR_L && time <= APR_L){
			return 4;
		}else if(time > APR_L && time <= MAY_L){
			return 5;
		}else if(time > MAY_L && time <= JUN_L){
			return 6;
		}else if(time > JUN_L && time <= JUL_L){
			return 7;
		}else if(time > JUL_L && time <= AUG_L){
			return 8;
		}else if(time > AUG_L && time <= SEP_L){
			return 9;
		}else if(time > SEP_L && time <= OCT_L){
			return 10;
		}else if(time > OCT_L && time <= NOV_L){
			return 11;
		}else{
			return 12;
		}
	}
}



