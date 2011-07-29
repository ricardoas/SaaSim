package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.cloud.User;
import commons.config.SimulatorConfiguration;
import commons.io.WorkloadParser;
import commons.sim.util.SimulatorProperties;

/**
 * This class is responsible for parsing the workload to be used. Considering SaaS plans limits, the workload should be separated according to each period
 * in the plan, e.g. each month, and each user.
 * @author davidcmm
 *
 */
public class GEISTMonthlyWorkloadParser implements WorkloadParser<List<Request>>{
	
	//Months limits in millis
	private static double JAN_L = 1000.0 * 60 * 60 * 24 * (31);
	private static double FEB_L = 1000.0 * 60 * 60 * 24 * (31+28);
	private static double MAR_L = 1000.0 * 60 * 60 * 24 * (31+28+31);
	private static double APR_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30);
	private static double MAY_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31);
	private static double JUN_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31+30);
	private static double JUL_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31+30+31);
	private static double AUG_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31+30+31+31);
	private static double SEP_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30);
	private static double OCT_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30+31);
	private static double NOV_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30+31+30);
	private static double DEC_L = 1000.0 * 60 * 60 * 24 * (31+28+31+30+31+30+31+31+30+31+30+31);
	
	private final String[] workloadFiles;
	private final BufferedReader [] readers;
	public int currentMonth;
	private Map<User, List<Request>> nextRequests;
	private Map<User, List<Request>> lastWorkloadRead;
	

	public GEISTMonthlyWorkloadParser(){
		this.workloadFiles = new String[1];
		this.workloadFiles[0] = SimulatorConfiguration.getInstance().getString(SimulatorProperties.WORKLOAD_PATH);
		
		this.readers = new BufferedReader[workloadFiles.length];
		for(int i = 0; i < workloadFiles.length; i++){
			try {
				readers[i] = new BufferedReader(new FileReader(new File(workloadFiles[i])));
			} catch (FileNotFoundException e) {
			}
		}
		this.currentMonth = 1;
		
		this.nextRequests = new HashMap<User, List<Request>>();
		this.lastWorkloadRead = new HashMap<User, List<Request>>();
	}
	
	@Override
	public List<Request> next() throws IOException {
		
		this.lastWorkloadRead = new HashMap<User, List<Request>>();
		List<Request> workloadList = new ArrayList<Request>();
		
		int nextMonth = Integer.MAX_VALUE;
		
		//Verifying if any event was stored in previous read
		if(this.nextRequests.size() != 0){
			lastWorkloadRead.putAll(this.nextRequests);
			this.nextRequests.clear();
		}
		
		for(int i = 0; i < this.readers.length; i++){
			BufferedReader reader = this.readers[i];
			while(reader.ready()){
				String[] eventData = reader.readLine().trim().split("( +|\t+)+");//Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL, demand
				Request request = new Request(eventData[0], eventData[1], eventData[2], Long.valueOf(eventData[3]), 
						Long.valueOf(eventData[4]), Integer.valueOf(eventData[5]), eventData[6], eventData[7], Long.valueOf(eventData[8]) );
				
				//Adding new event to its corresponding user
				int monthOfEvent = getMonthOfEvent(request.getTimeInMillis());
				if(monthOfEvent == this.currentMonth){//An event of current iteration was found
					User user = new User(eventData[1]);//Users are identified uniquely by their ids
					List<Request> userWorkload = lastWorkloadRead.get(user);
					if(userWorkload == null){
						userWorkload = new ArrayList<Request>();
						lastWorkloadRead.put(user, userWorkload);
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
		
		//Adding all read requests
		for(List<Request> requests : this.lastWorkloadRead.values()){
			workloadList.addAll(requests);
		}
	    return workloadList;
	}
	
	public Map<User, List<Request>> getWorkloadPerUser(){
		return this.lastWorkloadRead;
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

	@Override
	public boolean hasNext() {
		return this.nextRequests.size() > 0;
	}
}



