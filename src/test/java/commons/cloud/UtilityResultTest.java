package commons.cloud;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.easymock.EasyMock;
import org.junit.Test;

import util.CleanConfigurationTest;

public class UtilityResultTest extends CleanConfigurationTest {
	
	@Test
	public void testUtilityWithoutAddingValues(){
		UtilityResult result = new UtilityResult(0, 0);
		assertEquals(0, result.getUtility(), 0.0001);
	}
	
	@Test
	public void testAddEntry(){
		double onDemandCost = 99999;
		double reservedCost = 7756.33234;
		double monitoringCost = 77466;
		
		double inCost = 0.001;
		double outCost = 33333;
		
		double cpuCost = 129.33;
		double transferenceCost = 87.1111;
		double storageCost = 1.9999;
		
		UtilityResultEntry entry = buildEntry(0, onDemandCost, reservedCost, monitoringCost, inCost, outCost,
				cpuCost, transferenceCost, storageCost);
		
		UtilityResult result = new UtilityResult(0, 0);
		result.addEntry(entry);
		
		double totalReceipt = cpuCost + transferenceCost + storageCost;
		double totalCost = onDemandCost + reservedCost + monitoringCost + inCost + outCost;
		
		assertEquals(totalReceipt - totalCost, result.getUtility(), 0.0001);
		
		//Adding another entry
		entry = buildEntry(1, onDemandCost, reservedCost, monitoringCost, inCost, outCost,
				cpuCost, transferenceCost, storageCost);
		result.addEntry(entry);
		assertEquals(2 * (totalReceipt - totalCost), result.getUtility(), 0.0001);
	}

	private static UtilityResultEntry buildEntry(long time, double onDemandCost, double reservedCost, double monitoringCost, double inCost, double outCost, double cpuCost, double transferenceCost, double storageCost) {
		User user1 = EasyMock.createStrictMock(User.class);
		User user2 = EasyMock.createStrictMock(User.class);
		EasyMock.replay(user1, user2);
		
		User[] users = new User[2];
		users[0] = user1;
		users[1] = user2;
		
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("Amazon");
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		Provider[] providers = new Provider[1];
		providers[0] = provider;
		
		UtilityResultEntry entry = new UtilityResultEntry(time, users, providers);
		
		//Adding usage to cost
		entry.addUsageToCost(0, MachineType.M1_SMALL, 55, onDemandCost, 99, reservedCost , monitoringCost);
		
		//Adding transference cost
		entry.addTransferenceToCost(0, 10000, inCost, 2000, outCost);
		
		//Adding to receipt
		entry.addToReceipt(0, "c1", 100, cpuCost, 2500, transferenceCost, storageCost);
		
		return entry;
	}
	
	@Test
	public void testAddUserUniqueFee(){
		UtilityResult result = new UtilityResult(2, 0);
		
		int firstUserFee = 1000;
		int secondUserFee = 2000;
	
		result.addUserUniqueFee(0, firstUserFee);
		result.addUserUniqueFee(1, secondUserFee);
		
		assertEquals(firstUserFee + secondUserFee, result.getUtility(), 0.00001);
	}
	
	@Test
	public void testAddProviderUniqueCost(){
		UtilityResult result = new UtilityResult(0, 2);
		
		double amazonCost = 1000.0;
		double rackspaceCost = 3000.0;
		result.addProviderUniqueCost(0, MachineType.C1_MEDIUM, amazonCost);
		result.addProviderUniqueCost(1, MachineType.C1_MEDIUM, rackspaceCost);
		
		assertEquals(-(amazonCost + rackspaceCost), result.getUtility(), 0.00001);
	}
	
	@Test
	public void testGetUtilityForWholeScenario(){
		double onDemandCost = 11.998;
		double reservedCost = 77.33234;
		double monitoringCost = 6;
		
		double inCost = 0.001;
		double outCost = 3;
		
		double cpuCost = 1.33;
		double transferenceCost = 8.1111;
		double storageCost = 1.9999;
		
		UtilityResultEntry entry = buildEntry(0, onDemandCost, reservedCost, monitoringCost, inCost, outCost,
				cpuCost, transferenceCost, storageCost);
		
		UtilityResult result = new UtilityResult(1, 1);
	
		//Adding entry
		result.addEntry(entry);
		
		//Adding user unique fee
		int firstUserFee = 977;
		result.addUserUniqueFee(0, firstUserFee);
		
		//Adding provider unique cost
		double rackspaceCost = 3333.0;
		result.addProviderUniqueCost(0, MachineType.C1_MEDIUM, rackspaceCost);
		
		double totalReceipt = firstUserFee + cpuCost + transferenceCost + storageCost;
		double totalCost = rackspaceCost + onDemandCost + reservedCost + monitoringCost + inCost + outCost;
		
		assertEquals(totalReceipt - totalCost, result.getUtility(), 0.00001);
	}
	
	@Test
	public void testIterator(){
		UtilityResult result = new UtilityResult(1, 1);
		Iterator<UtilityResultEntry> iterator = result.iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}
}
