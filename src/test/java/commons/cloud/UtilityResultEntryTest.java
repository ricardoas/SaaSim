package commons.cloud;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.junit.Test;

public class UtilityResultEntryTest {
	
	@Test
	public void testConstructWithEmptyUsersAndProviders(){
		UtilityResultEntry entry = new UtilityResultEntry(999, new HashMap<Integer, User>(), new HashMap<String, Provider>());
		assertEquals(999, entry.getTime());
		assertEquals(0, entry.getCost(), 0.00001);
		assertEquals(0, entry.getReceipt(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(0, entry.getUtility(), 0.00001);
	}
	
	@Test
	public void testCompareToWithEqualTimes(){
		UtilityResultEntry entry = new UtilityResultEntry(0, new HashMap<Integer, User>(), new HashMap<String, Provider>());
		UtilityResultEntry entry2 = new UtilityResultEntry(0, new HashMap<Integer, User>(), new HashMap<String, Provider>());
		
		assertEquals(0, entry.compareTo(entry2));
		
		entry = new UtilityResultEntry(10000, new HashMap<Integer, User>(), new HashMap<String, Provider>());
		entry2 = new UtilityResultEntry(10000, new HashMap<Integer, User>(), new HashMap<String, Provider>());
		
		assertEquals(0, entry.compareTo(entry2));
	}
	
	@Test
	public void testCompareToWithDifferentTimes(){
		UtilityResultEntry entry = new UtilityResultEntry(0, new HashMap<Integer, User>(), new HashMap<String, Provider>());
		UtilityResultEntry entry2 = new UtilityResultEntry(1, new HashMap<Integer, User>(), new HashMap<String, Provider>());
		
		assertEquals(-1, entry.compareTo(entry2));
		assertEquals(1, entry2.compareTo(entry));
	}
	
	@Test
	public void testAddToReceiptWithValidUser(){
		User user1 = EasyMock.createStrictMock(User.class);
		User user2 = EasyMock.createStrictMock(User.class);
		EasyMock.replay(user1, user2);
		
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		users.put(1, user1);
		users.put(2, user2);
		
		UtilityResultEntry entry = new UtilityResultEntry(0, users, new HashMap<String, Provider>());
		int cpuCost = 50;
		int transferenceCost = 111;
		int storageCost = 999;
		entry.addToReceipt(1, "c1", 100, cpuCost, 2500, transferenceCost, storageCost);
		assertEquals(cpuCost + transferenceCost + storageCost, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getCost(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(cpuCost + transferenceCost + storageCost, entry.getUtility(), 0.0001);
		
		EasyMock.verify(user1, user2);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddToReceiptWithInvalidUser(){
		User user1 = EasyMock.createStrictMock(User.class);
		User user2 = EasyMock.createStrictMock(User.class);
		EasyMock.replay(user1, user2);
		
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		users.put(1, user1);
		users.put(2, user2);
		
		UtilityResultEntry entry = new UtilityResultEntry(0, users, new HashMap<String, Provider>());
		int cpuCost = 50;
		int transferenceCost = 111;
		int storageCost = 999;
		
		//Invalid user
		entry.addToReceipt(11111, "c1", 100, cpuCost, 2500, transferenceCost, storageCost);
	}
	
	@Test
	public void testAddTransferenceToCost(){
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		HashMap<String, Provider> providers = new HashMap<String, Provider>();
		providers.put("amazon", provider);
		
		UtilityResultEntry entry = new UtilityResultEntry(0, new HashMap<Integer, User>(), providers);
		double inCost = 111.999;
		double outCost = 0.9999;
		entry.addTransferenceToCost("amazon", 10000, inCost, 2000, outCost);
		
		assertEquals(0, entry.getReceipt(), 0.0001);
		assertEquals(inCost + outCost, entry.getCost(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(-(inCost + outCost), entry.getUtility(), 0.0001);
		
		EasyMock.verify(provider);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddTransferenceToCostWithInvalidProvider(){
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		HashMap<String, Provider> providers = new HashMap<String, Provider>();
		providers.put("amazon", provider);
		
		UtilityResultEntry entry = new UtilityResultEntry(0, new HashMap<Integer, User>(), providers);
		double inCost = 111.999;
		double outCost = 0.9999;
		entry.addTransferenceToCost("rackspace", 10000, inCost, 2000, outCost);
		
		EasyMock.verify(provider);
	}
	
	@Test
	public void testAddUsageToCost(){
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		HashMap<String, Provider> providers = new HashMap<String, Provider>();
		providers.put("amazon", provider);
		
		UtilityResultEntry entry = new UtilityResultEntry(0, new HashMap<Integer, User>(), providers);
		double onDemandCost = 1.23;
		double reservedCost = 2.99;
		double monitoringCost = 3.87;
		entry.addUsageToCost("amazon", MachineType.SMALL, 55, onDemandCost, 99, reservedCost , monitoringCost);
		
		assertEquals(0, entry.getReceipt(), 0.0001);
		assertEquals(onDemandCost + reservedCost + monitoringCost, entry.getCost(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(-(onDemandCost + reservedCost + monitoringCost), entry.getUtility(), 0.0001);
		
		EasyMock.verify(provider);
	}
	
	@Test
	public void testCompleteScenario(){
		User user1 = EasyMock.createStrictMock(User.class);
		User user2 = EasyMock.createStrictMock(User.class);
		EasyMock.replay(user1, user2);
		
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		users.put(1, user1);
		users.put(2, user2);
		
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		HashMap<String, Provider> providers = new HashMap<String, Provider>();
		providers.put("amazon", provider);
		
		UtilityResultEntry entry = new UtilityResultEntry(0, users, providers);
		
		//Adding usage to cost
		double onDemandCost = 99999;
		double reservedCost = 7756.33234;
		double monitoringCost = 77466;
		entry.addUsageToCost("amazon", MachineType.SMALL, 55, onDemandCost, 99, reservedCost , monitoringCost);
		
		//Adding transference cost
		double inCost = 0.001;
		double outCost = 33333;
		entry.addTransferenceToCost("amazon", 10000, inCost, 2000, outCost);
		
		//Adding to receipt
		double cpuCost = 129.33;
		double transferenceCost = 87.1111;
		double storageCost = 1.9999;
		entry.addToReceipt(1, "c1", 100, cpuCost, 2500, transferenceCost, storageCost);
		
		//Checking values
		double totalReceipt = cpuCost + transferenceCost + storageCost;
		double totalCost = onDemandCost + reservedCost + monitoringCost + inCost + outCost;
		assertEquals(totalReceipt, entry.getReceipt(), 0.0001);
		assertEquals(totalCost, entry.getCost(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(totalReceipt - totalCost, entry.getUtility(), 0.0001);
	}
}

