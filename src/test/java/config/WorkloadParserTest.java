package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.sim.util.SimulatorProperties;


/**
 * FIXME don't know what to do yet.
 */
public class WorkloadParserTest {
	
	private static String W1 = "src/test/resources/workload/w1.dat";
	private static String CONFIG_FILE = "src/test/resources/config.properties";
	
	@Test
	public void testW1(){
		try {
			Configuration.buildInstance(CONFIG_FILE);
			Configuration config = Configuration.getInstance();
			config.setProperty(SimulatorProperties.WORKLOAD_PATH, W1);
			
			GEISTMonthlyWorkloadParser parser = new GEISTMonthlyWorkloadParser();
			
			//First month
			List<Request> requests = parser.next();
			Map<User, List<Request>> jan = parser.getWorkloadPerUser();
			assertNotNull(requests);
			assertNotNull(jan);
			assertEquals(1, jan.size());//Number of users
			assertEquals(8, requests.size());
			User user = new User("1");
			assertEquals(8, jan.get(user).size());//Number of requests
			assertEquals(2, parser.currentMonth);
			assertTrue(parser.hasNext());
			
			//Second month
			requests = parser.next();
			Map<User, List<Request>> feb = parser.getWorkloadPerUser();
			assertNotNull(feb);
			assertNotNull(requests);
			assertEquals(1, feb.size());//Number of users
			assertEquals(8, requests.size());
			user = new User("2");
			assertEquals(8, feb.get(user).size());//Number of requests
			assertEquals(3, parser.currentMonth);
			assertTrue(parser.hasNext());
			
			//Third month
			requests = parser.next();
			Map<User, List<Request>> mar = parser.getWorkloadPerUser();
			assertNotNull(mar);
			assertNotNull(requests);
			assertEquals(2, mar.size());//Number of users
			assertEquals(8, requests.size());
			user = new User("3");
			assertEquals(6, mar.get(user).size());//Number of requests
			user = new User("4");
			assertEquals(2, mar.get(user).size());//Number of requests
			assertEquals(4, parser.currentMonth);
			assertTrue(parser.hasNext());
			
			//Fourth month
			requests = parser.next();
			Map<User, List<Request>> apr = parser.getWorkloadPerUser();
			assertNotNull(apr);
			assertNotNull(requests);
			assertEquals(1, apr.size());//Number of users
			assertEquals(8, requests.size());
			user = new User("4");
			assertEquals(8, apr.get(user).size());//Number of requests
			assertEquals(11, parser.currentMonth);
			assertTrue(parser.hasNext());
			
			//Fifth month
			requests = parser.next();
			Map<User, List<Request>> nov = parser.getWorkloadPerUser();
			assertNotNull(nov);
			assertNotNull(requests);
			assertEquals(1, nov.size());//Number of users
			assertEquals(8, requests.size());
			user = new User("5");
			assertEquals(8, nov.get(user).size());//Number of requests
			assertEquals(12, parser.currentMonth);
			assertTrue(parser.hasNext());
			
			//Sixth month
			requests = parser.next();
			Map<User, List<Request>> dec = parser.getWorkloadPerUser();
			assertNotNull(dec);
			assertNotNull(requests);
			assertEquals(1, dec.size());//Number of users
			assertEquals(1, requests.size());
			user = new User("6");
			assertEquals(1, dec.get(user).size());//Number of requests
			assertEquals(13, parser.currentMonth);
			assertFalse(parser.hasNext());
			
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
			fail("Valid data read!");
		} catch (IOException e) {
			fail("Valid file!");
		} catch (ConfigurationException e) {
			fail("Valid file! "+e.getMessage());
		}
	}
	
}


