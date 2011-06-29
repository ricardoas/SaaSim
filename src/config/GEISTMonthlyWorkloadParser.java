package config;

import java.io.BufferedReader;
import java.io.File;
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
	
	public static final String JAN = "JAN";
	public static final String FEB = "FEB";
	public static final String MAR = "MAR";
	public static final String APR = "APR";
	public static final String MAY = "MAY";
	public static final String JUN = "JUN";
	public static final String JUL = "JUL";
	public static final String AUG = "AUG";
	public static final String SEPT = "SEPT";
	public static final String OCT = "OCT";
	public static final String NOV = "NOV";
	public static final String DEC = "DEC";
	
	public static Map<String, Map<User, List<Request>>> getWorkloadPerMonth(String ... workloadFiles) throws NumberFormatException, IOException{
		HashMap<String, Map<User, List<Request>>> workload = new HashMap<String, Map<User, List<Request>>>();
		
		for(String workloadFile : workloadFiles){
			BufferedReader reader = new BufferedReader(new FileReader(new File(workloadFile)));
			while(reader.ready()){
				String[] eventData = reader.readLine().split("( +|\t+)+");//Assuming: clientID, userID, reqID, time, bytes, has expired, http op., URL
				Request request = new Request(eventData[0], eventData[2], Double.valueOf(eventData[3]), 
						Double.valueOf(eventData[4]), (eventData[5].contains("1")) ? true : false, eventData[6], eventData[7] );
				
				//Adding new event to its corresponding user and month
				String monthOfEvent = getMonthOfEvent(request.time);
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
	
	private static String getMonthOfEvent(double time){
		if(time <= JAN_L){
			return JAN;
		}else if(time > JAN_L && time <= FEB_L){
			return FEB;
		}else if(time > FEB_L && time <= MAR_L){
			return MAR;
		}else if(time > MAR_L && time <= APR_L){
			return APR;
		}else if(time > APR_L && time <= MAY_L){
			return MAY;
		}else if(time > MAY_L && time <= JUN_L){
			return JUN;
		}else if(time > JUN_L && time <= JUL_L){
			return JUL;
		}else if(time > JUL_L && time <= AUG_L){
			return AUG;
		}else if(time > AUG_L && time <= SEP_L){
			return SEPT;
		}else if(time > SEP_L && time <= OCT_L){
			return OCT;
		}else if(time > OCT_L && time <= NOV_L){
			return NOV;
		}else{
			return DEC;
		}
	}

	@Override
	public Map<User, List<Request>> next() {
	    return null;
	}
}



