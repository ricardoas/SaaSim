package config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.*;

import cloud.Request;
import cloud.User;
import static org.junit.Assert.*;


public class WorkloadParserTest {
	
	private static String W1 = "test_files/workload/w1.dat";
	
	@Test
	public void testW1(){
		try {
			Map<String, Map<User, List<Request>>> workloadPerMonth = WorkloadParser.getWorkloadPerMonth(W1);
			assertNotNull(workloadPerMonth);
			assertEquals(6, workloadPerMonth.size());
			
			Map<User, List<Request>> jan = workloadPerMonth.get(WorkloadParser.JAN);
			assertNotNull(jan);
			assertEquals(1, jan.size());//Number of users
			User user = new User("1");
			assertEquals(8, jan.get(user).size());//Number of requests
			
			Map<User, List<Request>> feb = workloadPerMonth.get(WorkloadParser.FEB);
			assertNotNull(feb);
			assertEquals(1, feb.size());//Number of users
			user = new User("2");
			assertEquals(8, feb.get(user).size());//Number of requests
			
			Map<User, List<Request>> mar = workloadPerMonth.get(WorkloadParser.MAR);
			assertNotNull(mar);
			assertEquals(2, mar.size());//Number of users
			user = new User("3");
			assertEquals(6, mar.get(user).size());//Number of requests
			user = new User("4");
			assertEquals(2, mar.get(user).size());//Number of requests
			
			Map<User, List<Request>> apr = workloadPerMonth.get(WorkloadParser.APR);
			assertNotNull(apr);
			assertEquals(1, apr.size());//Number of users
			user = new User("4");
			assertEquals(8, apr.get(user).size());//Number of requests
			
			Map<User, List<Request>> nov = workloadPerMonth.get(WorkloadParser.NOV);
			assertNotNull(nov);
			assertEquals(1, nov.size());//Number of users
			user = new User("5");
			assertEquals(8, nov.get(user).size());//Number of requests
			
			Map<User, List<Request>> dec = workloadPerMonth.get(WorkloadParser.DEC);
			assertNotNull(dec);
			assertEquals(1, dec.size());//Number of users
			user = new User("6");
			assertEquals(1, dec.get(user).size());//Number of requests
			
			//Verifying that all users were read correctly
			for(User currentUser : jan.keySet()){
				assertEquals(0, currentUser.consumedCpu, 0.0);
				assertEquals(0, currentUser.consumedStorage, 0.0);
				assertEquals(0, currentUser.consumedTransference, 0.0);
			}
			
			for(User currentUser : feb.keySet()){
				assertEquals(0, currentUser.consumedCpu, 0.0);
				assertEquals(0, currentUser.consumedStorage, 0.0);
				assertEquals(0, currentUser.consumedTransference, 0.0);
			}
			
			for(User currentUser : mar.keySet()){
				assertEquals(0, currentUser.consumedCpu, 0.0);
				assertEquals(0, currentUser.consumedStorage, 0.0);
				assertEquals(0, currentUser.consumedTransference, 0.0);
			}
			
			for(User currentUser : apr.keySet()){
				assertEquals(0, currentUser.consumedCpu, 0.0);
				assertEquals(0, currentUser.consumedStorage, 0.0);
				assertEquals(0, currentUser.consumedTransference, 0.0);
			}
			
			for(User currentUser : nov.keySet()){
				assertEquals(0, currentUser.consumedCpu, 0.0);
				assertEquals(0, currentUser.consumedStorage, 0.0);
				assertEquals(0, currentUser.consumedTransference, 0.0);
			}
			
			for(User currentUser : dec.keySet()){
				assertEquals(0, currentUser.consumedCpu, 0.0);
				assertEquals(0, currentUser.consumedStorage, 0.0);
				assertEquals(0, currentUser.consumedTransference, 0.0);
			}
			
		} catch (NumberFormatException e) {
			fail("Invalid data read!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Invalid file!");
		}
	}
	
}


