package commons.cloud;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import util.CleanConfigurationTest;

import commons.sim.components.MachineDescriptor;

public class UtilityResultEntryTest extends CleanConfigurationTest {
	
	@Test
	public void testConstructWithEmptyUsersAndProviders(){
		UtilityResultEntry entry = new UtilityResultEntry(999, new User[]{}, new Provider[]{});
		assertEquals(999, entry.getTime());
		assertEquals(0, entry.getCost(), 0.00001);
		assertEquals(0, entry.getReceipt(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(0, entry.getUtility(), 0.00001);
	}
	
	@Test
	public void testCompareToWithEqualTimes(){
		UtilityResultEntry entry = new UtilityResultEntry(0, new User[]{}, new Provider[]{});
		UtilityResultEntry entry2 = new UtilityResultEntry(0, new User[]{}, new Provider[]{});
		
		assertEquals(0, entry.compareTo(entry2));
		
		entry = new UtilityResultEntry(10000, new User[]{}, new Provider[]{});
		entry2 = new UtilityResultEntry(10000, new User[]{}, new Provider[]{});
		
		assertEquals(0, entry.compareTo(entry2));
	}
	
	@Test
	public void testCompareToWithDifferentTimes(){
		UtilityResultEntry entry = new UtilityResultEntry(0, new User[]{}, new Provider[]{});
		UtilityResultEntry entry2 = new UtilityResultEntry(1, new User[]{}, new Provider[]{});
		
		assertEquals(-1, entry.compareTo(entry2));
		assertEquals(1, entry2.compareTo(entry));
	}
	
	@Test
	public void testAddToReceiptWithValidUser(){
		User user1 = EasyMock.createStrictMock(User.class);
		User user2 = EasyMock.createStrictMock(User.class);
		EasyMock.replay(user1, user2);
		
		User [] users = new User[2];
		users[0] = user1;
		users[1] = user2;
		
		UtilityResultEntry entry = new UtilityResultEntry(0, users, new Provider[]{});
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
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testAddToReceiptWithInvalidUser(){
		User user1 = EasyMock.createStrictMock(User.class);
		User user2 = EasyMock.createStrictMock(User.class);
		EasyMock.replay(user1, user2);
		
		User [] users = new User[2];
		users[0] = user1;
		users[1] = user2;
		
		UtilityResultEntry entry = new UtilityResultEntry(0, users, new Provider[]{});
		int cpuCost = 50;
		int transferenceCost = 111;
		int storageCost = 999;
		
		//Invalid user
		entry.addToReceipt(10, "c1", 100, cpuCost, 2500, transferenceCost, storageCost);
	}
	
	@Test
	public void testAddTransferenceToCost(){
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("Amazon");
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		Provider[] providers = new Provider[1];
		providers[0] = provider;
		
		UtilityResultEntry entry = new UtilityResultEntry(0, new User[]{}, providers);
		double inCost = 111.999;
		double outCost = 0.9999;
		entry.addTransferenceToCost(0, 10000, inCost, 2000, outCost);
		
		assertEquals(0, entry.getReceipt(), 0.0001);
		assertEquals(inCost + outCost, entry.getCost(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(-(inCost + outCost), entry.getUtility(), 0.0001);
		
		EasyMock.verify(provider);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testAddTransferenceToCostWithInvalidProvider(){
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("Amazon");
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		Provider[] providers = new Provider[1];
		providers[0] = provider;
		
		UtilityResultEntry entry = new UtilityResultEntry(0, new User[]{}, providers);
		double inCost = 111.999;
		double outCost = 0.9999;
		entry.addTransferenceToCost(1, 10000, inCost, 2000, outCost);
		
	}
	
	@Test
	public void testAddUsageToCost(){
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("Amazon");
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		Provider[] providers = new Provider[1];
		providers[0] = provider;
		
		UtilityResultEntry entry = new UtilityResultEntry(0, new User[]{}, providers);
		double onDemandCost = 1.23;
		double reservedCost = 2.99;
		double monitoringCost = 3.87;
		entry.addUsageToCost(0, MachineType.M1_SMALL, 55, onDemandCost, 99, reservedCost , monitoringCost);
		
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
		
		User [] users = new User[2];
		users[0] = user1;
		users[1] = user2;
		
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("Amazon");
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		Provider[] providers = new Provider[1];
		providers[0] = provider;
		
		UtilityResultEntry entry = new UtilityResultEntry(0, users, providers);
		
		//Adding usage to cost
		double onDemandCost = 99999;
		double reservedCost = 7756.33234;
		double monitoringCost = 77466;
		entry.addUsageToCost(0, MachineType.M1_SMALL, 55, onDemandCost, 99, reservedCost , monitoringCost);
		
		//Adding transference cost
		double inCost = 0.001;
		double outCost = 33333;
		entry.addTransferenceToCost(0, 10000, inCost, 2000, outCost);
		
		//Adding to receipt
		double cpuCost = 129.33;
		double transferenceCost = 87.1111;
		double storageCost = 1.9999;
		entry.addToReceipt(0, "c1", 100, cpuCost, 2500, transferenceCost, storageCost);
		
		//Checking values
		double totalReceipt = cpuCost + transferenceCost + storageCost;
		double totalCost = onDemandCost + reservedCost + monitoringCost + inCost + outCost;
		assertEquals(totalReceipt, entry.getReceipt(), 0.0001);
		assertEquals(totalCost, entry.getCost(), 0.00001);
		assertEquals(0, entry.getPenalty(), 0.00001);
		assertEquals(totalReceipt - totalCost, entry.getUtility(), 0.0001);
	}
	
	@Test
	public void testHashCodeEqualsConsistencyWithSameTime() {
		UtilityResultEntry entry = new UtilityResultEntry(2, new User[]{}, new Provider[]{});
		UtilityResultEntry entryClone = new UtilityResultEntry(2, new User[]{}, new Provider[]{});
		assertTrue(entry.equals(entry));
		assertTrue(entry.equals(entryClone));
		assertTrue(entryClone.equals(entry));
		assertTrue(entry.hashCode() == entryClone.hashCode());
	}
	
	@Test
	public void testHashCodeEqualsConsistencyWithDifferentTime() {
		UtilityResultEntry entry1 = new UtilityResultEntry(2, new User[]{}, new Provider[]{});
		UtilityResultEntry entry2 = new UtilityResultEntry(10, new User[]{}, new Provider[]{});
		assertFalse(entry1.equals(entry2));
		assertFalse(entry2.equals(entry1));
		assertFalse(entry1.hashCode() == entry2.hashCode());
	}
	
	@Test
	public void testEqualsConsistencyWithNullObject() {
		UtilityResultEntry entry1 = new UtilityResultEntry(2, new User[]{}, new Provider[]{});
		assertFalse(entry1.equals(null));
	}
	
	@Test
	public void testEqualsConsistencyWithAnotherClassObject() {
		UtilityResultEntry entry1 = new UtilityResultEntry(2, new User[]{}, new Provider[]{});
		assertFalse(entry1.equals(new MachineDescriptor(0, false, MachineType.M1_SMALL, 0)));
	}
}

