package commons.cloud;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.config.Configuration;
import commons.util.DataUnit;
import commons.util.TimeUnit;

/**
 * This class contains tests for the Contract entity between a SaaS user and a SaaS provider.
 * As {@link Contract} does not contains validation (assumed to be done by the {@link Configuration}
 * during loading), creation is not tested. 
 * 
 * @author davidcmm
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class ContractTest extends ValidConfigurationTest {
	
	private String plan1 = "bronze";
	private String plan2 = "gold";
	private static final int HIGH = 0;
	private static final int LOW = 1;
	private double setupCost = 1000.0;
	private double price = 24.95;
	private long cpuLimit = 10 * TimeUnit.HOUR.getMillis();
	private double extraCpuCost = 5.0/TimeUnit.HOUR.getMillis();
	private long [] transferenceLimits = {2048 * DataUnit.MB.getBytes()};
	private double [] transferenceCosts = {0, 0.005/DataUnit.MB.getBytes()};
	private long storageLimits = 200 * DataUnit.MB.getBytes();
	private double storageCosts = 0.1 / DataUnit.MB.getBytes();

	private Contract c1;
	private Contract c2;
	private Contract c3;
	
	@Before
	public void setUp(){
		c1 = new Contract(plan1, HIGH, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts, storageLimits, storageCosts);
		c2 = new Contract(plan2, LOW, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts, storageLimits, storageCosts);
		c3 = new Contract(plan2, LOW, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts, storageLimits, storageCosts);
	}

	/**
	 * Test method for {@link Contract#compareTo(Contract)}
	 */
	@Test
	public void testCompareTo(){
		assertEquals(0, c1.compareTo(c1));
		assertEquals(1, c1.compareTo(c2));
		assertEquals(1, c1.compareTo(c3));
		assertEquals(-1, c2.compareTo(c1));
		assertEquals(0, c2.compareTo(c2));
		assertEquals(0, c2.compareTo(c3));
		assertEquals(-1, c3.compareTo(c1));
		assertEquals(0, c3.compareTo(c2));
		assertEquals(0, c3.compareTo(c3));
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithoutConsumption(){
		int userID = 1;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addToReceipt(userID, plan1, 0, price, 0, 0, 0);
		EasyMock.replay(entry);
		
		c1.calculateReceipt(entry, userID, 0, 0, 0, 0);
		
		EasyMock.verify(entry);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithConsumptionLowerThanCPULimit(){
		int userID = 1;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addToReceipt(userID, plan1, 0, price, 0, 0, 0);
		EasyMock.replay(entry);
		
		c1.calculateReceipt(entry, userID, 10, 0, 0, 0);

		EasyMock.verify(entry);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithConsumptionHigherThanCPULimit(){
		long extraConsumedCpu = 50;
		int userID = 1;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addToReceipt(userID , plan1, 50 * TimeUnit.HOUR.getMillis(), price + 50 * TimeUnit.HOUR.getMillis() * extraCpuCost, 0, 0, 0);
		EasyMock.replay(entry);

		c1.calculateReceipt(entry, userID, cpuLimit + extraConsumedCpu * TimeUnit.HOUR.getMillis(), 0, 0, 0);
		
		EasyMock.verify(entry);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithTransferConsumptionBelowMinimum(){
		long transferenceInBytes = 1024 * DataUnit.MB.getBytes();
		int userID = 1;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addToReceipt(userID, plan1, 0, price, transferenceInBytes, 0, 0);
		EasyMock.replay(entry);

		c1.calculateReceipt(entry, userID, cpuLimit, transferenceInBytes, 0, 0);
		
		EasyMock.verify(entry);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithTransferConsumptionAboveMaximum(){
		long transferenceInBytes = 3 * DataUnit.GB.getBytes();
		double expectedCost = transferenceLimits[0] * transferenceCosts[0] + 
				(transferenceInBytes - transferenceLimits[0]) * transferenceCosts[1];
		int userID = 1;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addToReceipt(userID, plan1, 0, price, transferenceInBytes, expectedCost, 0);
		EasyMock.replay(entry);

		c1.calculateReceipt(entry, userID, cpuLimit, transferenceInBytes, 0, 0);
		
		EasyMock.verify(entry);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithStorageConsumptionBelowMinimum(){
		int userID = 1;

		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addToReceipt(userID, plan1, 0, price, 0, 0, 0);
		EasyMock.replay(entry);

		c1.calculateReceipt(entry, userID, cpuLimit, 0, 0, storageLimits);
		
		EasyMock.verify(entry);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithStorageConsumptionAboveMaximum(){
		long extraStorageConsumedInMB = 10;
		int userID = 1;

		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addToReceipt(userID, plan1, 0, price, 0, 0, extraStorageConsumedInMB * storageCosts);
		EasyMock.replay(entry);

		c1.calculateReceipt(entry, userID, cpuLimit, 0, 0, (storageLimits + extraStorageConsumedInMB));
		
		EasyMock.verify(entry);
	}
	
	/**
	 * Test method for {@link Contract#calculateOneTimeFees()}
	 */
	@Test
	public void testCalculateOneTimeFees(){
		assertEquals(setupCost, c1.calculateOneTimeFees(), 0.0);
	}
	
	@Test
	public void testCalculatePenaltyWithoutLoss(){
		assertEquals(0, c1.calculatePenalty(0), 0.0);
		assertEquals(0, c2.calculatePenalty(0), 0.0);
		assertEquals(0, c3.calculatePenalty(0), 0.0);
	}
	
	@Test
	public void testCalculatePenaltyWithSmallestLoss(){
		assertEquals(0, c1.calculatePenalty(0.0001), 0.0);
		assertEquals(price / 4, c1.calculatePenalty(0.005), 0.0);
		assertEquals(price / 2, c1.calculatePenalty(0.05), 0.0);
		assertEquals(price, c1.calculatePenalty(0.09999999), 0.0);
		assertEquals(price, c1.calculatePenalty(0.1), 0.0);
	}
	
	@Test
	public void testCalculatePenaltyWithLoss(){
		assertEquals(price / 4, c1.calculatePenalty(0.005), 0.0);
		assertEquals(price / 4, c1.calculatePenalty(0.01), 0.0);
		assertEquals(0, c1.calculatePenalty(0.001), 0.0);
	}
	
	@Test
	public void testCalculatePenaltyWithHigherLoss(){
		assertEquals(price, c1.calculatePenalty(0.100001), 0.0);
		assertEquals(price, c1.calculatePenalty(0.5), 0.0);
		assertEquals(price, c1.calculatePenalty(0.99999), 0.0);
	}

	@Test
	public void testEqualsHashCodeIntegrityWithSameObject(){
		assertEquals(c2, c2);
		assertEquals(c2.hashCode(), c2.hashCode());
	}

	@Test
	public void testEqualsHashCodeIntegrityWithCloneObject(){
		assertEquals(c2, c3);
		assertEquals(c2.hashCode(), c3.hashCode());
	}

	@Test
	public void testEqualsHashCodeIntegrityWithDifferentObject(){
		assertTrue(!c1.equals(c2));
		assertTrue(c1.hashCode() != c2.hashCode());
	}
	
	@Test(expected=AssertionError.class)
	public void testHashCodeWithNullName(){
		Contract fakeContract = new Contract(null, HIGH, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts, storageLimits, storageCosts);
		fakeContract.hashCode();
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithNull(){
		c1.equals(null);
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject(){
		c1.equals(new String(""));
	}
}
