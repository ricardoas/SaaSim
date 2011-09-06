package commons.cloud;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class UtilityResultTest {
	
	@Test
	public void testUtilityWithoutAddingValues(){
		UtilityResult result = new UtilityResult();
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
		
		UtilityResult result = new UtilityResult();
		result.addEntry(entry);
		
		double totalReceipt = cpuCost + transferenceCost + storageCost;
		double totalCost = onDemandCost + reservedCost + monitoringCost + inCost + outCost;
		
		assertEquals(totalReceipt - totalCost, result.getUtility(), 0.0001);
		
		//Adding another entry
		entry = buildEntry(1, onDemandCost, reservedCost, monitoringCost, inCost, outCost,
				cpuCost, transferenceCost, storageCost);
		result.addEntry(entry);
		assertEquals(2 * (totalReceipt - totalCost), result.getUtility(), 0.0001);
		
		PowerMock.verifyAll();
	}

	private UtilityResultEntry buildEntry(long time, double onDemandCost, double reservedCost, double monitoringCost, double inCost, double outCost, double cpuCost, double transferenceCost, double storageCost) {
		User user1 = EasyMock.createStrictMock(User.class);
		User user2 = EasyMock.createStrictMock(User.class);
		EasyMock.replay(user1, user2);
		
		HashMap<String, User> users = new HashMap<String, User>();
		users.put("user1", user1);
		users.put("user2", user2);
		
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getAvailableTypes()).andReturn(MachineType.values());
		EasyMock.replay(provider);
		
		HashMap<String, Provider> providers = new HashMap<String, Provider>();
		providers.put("amazon", provider);
		
		UtilityResultEntry entry = new UtilityResultEntry(time, users, providers);
		
		//Adding usage to cost
		entry.addUsageToCost("amazon", MachineType.SMALL, 55, onDemandCost, 99, reservedCost , monitoringCost);
		
		//Adding transference cost
		entry.addTransferenceToCost("amazon", 10000, inCost, 2000, outCost);
		
		//Adding to receipt
		entry.addToReceipt("user1", "c1", 100, cpuCost, 2500, transferenceCost, storageCost);
		
		return entry;
	}
	
	@Test
	public void testAddUserUniqueFee(){
		UtilityResult result = new UtilityResult();
		
		int firstUserFee = 1000;
		int secondUserFee = 2000;
	
		result.addUserUniqueFee("user1", firstUserFee);
		result.addUserUniqueFee("user2", secondUserFee);
		
		assertEquals(firstUserFee + secondUserFee, result.getUtility(), 0.00001);
	}
	
	@Test
	public void testAddProviderUniqueCost(){
		UtilityResult result = new UtilityResult();
		
		double amazonCost = 1000.0;
		double rackspaceCost = 3000.0;
		result.addProviderUniqueCost("amazon", MachineType.MEDIUM, amazonCost);
		result.addProviderUniqueCost("rackspace", MachineType.MEDIUM, rackspaceCost);
		
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
		
		UtilityResult result = new UtilityResult();
	
		//Adding entry
		result.addEntry(entry);
		
		//Adding user unique fee
		int firstUserFee = 977;
		result.addUserUniqueFee("user1", firstUserFee);
		
		//Adding provider unique cost
		double rackspaceCost = 3333.0;
		result.addProviderUniqueCost("rackspace", MachineType.MEDIUM, rackspaceCost);
		
		double totalReceipt = firstUserFee + cpuCost + transferenceCost + storageCost;
		double totalCost = rackspaceCost + onDemandCost + reservedCost + monitoringCost + inCost + outCost;
		
		assertEquals(totalReceipt - totalCost, result.getUtility(), 0.00001);
		
		PowerMock.verifyAll();
	}
}
