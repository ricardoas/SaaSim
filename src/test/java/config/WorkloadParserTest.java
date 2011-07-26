package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import commons.cloud.Request;
import commons.cloud.User;


public class WorkloadParserTest {
	
	private static String W1 = "test_files/workload/w1.dat";
	
	@Test
	public void testW1(){
		try {
			GEISTMonthlyWorkloadParser parser = new GEISTMonthlyWorkloadParser(W1);
			
			//First month
			Map<User, List<Request>> jan = parser.next();
			assertNotNull(jan);
			assertEquals(1, jan.size());//Number of users
			User user = new User("1");
			assertEquals(8, jan.get(user).size());//Number of requests
			assertEquals(2, parser.currentMonth);
			
			//Second month
			Map<User, List<Request>> feb = parser.next();
			assertNotNull(feb);
			assertEquals(1, feb.size());//Number of users
			user = new User("2");
			assertEquals(8, feb.get(user).size());//Number of requests
			assertEquals(3, parser.currentMonth);
			
			//Third month
			Map<User, List<Request>> mar = parser.next();
			assertNotNull(mar);
			assertEquals(2, mar.size());//Number of users
			user = new User("3");
			assertEquals(6, mar.get(user).size());//Number of requests
			user = new User("4");
			assertEquals(2, mar.get(user).size());//Number of requests
			assertEquals(4, parser.currentMonth);
			
			//Fourth month
			Map<User, List<Request>> apr = parser.next();
			assertNotNull(apr);
			assertEquals(1, apr.size());//Number of users
			user = new User("4");
			assertEquals(8, apr.get(user).size());//Number of requests
			assertEquals(11, parser.currentMonth);
			
			//Fifth month
			Map<User, List<Request>> nov = parser.next();
			assertNotNull(nov);
			assertEquals(1, nov.size());//Number of users
			user = new User("5");
			assertEquals(8, nov.get(user).size());//Number of requests
			assertEquals(12, parser.currentMonth);
			
			//Sixth month
			Map<User, List<Request>> dec = parser.next();
			assertNotNull(dec);
			assertEquals(1, dec.size());//Number of users
			user = new User("6");
			assertEquals(1, dec.get(user).size());//Number of requests
			assertEquals(13, parser.currentMonth);
			
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
			fail("Invalid file!");
		}
	}
	
}


