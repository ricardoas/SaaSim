package saasim.cloud;

import static org.junit.Assert.*;

import org.junit.Test;

import saasim.cloud.utility.UserEntry;
import saasim.config.Configuration;
import saasim.util.DataUnit;
import saasim.util.TimeUnit;
import saasim.util.ValidConfigurationTest;


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
	private long [] transferenceLimits = DataUnit.convert(new long[]{2048}, DataUnit.MB, DataUnit.B);
	private double [] transferenceCosts = DataUnit.convert(new double[]{0, 0.005}, DataUnit.B, DataUnit.MB);
	private long storageLimits = 200 * DataUnit.MB.getBytes();
	private double storageCosts = 0.1 / DataUnit.MB.getBytes();

	private Contract c1;
	private Contract c2;
	private Contract c3;
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
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
		assertTrue(c1.compareTo(c2) < 0);
		assertTrue(c1.compareTo(c3) < 0 );
		
		assertTrue(c2.compareTo(c1) > 0);
		assertFalse(c2.compareTo(c1) < 0);
		assertEquals(0, c2.compareTo(c2));
		assertEquals(0, c2.compareTo(c3));
		
		assertTrue(c3.compareTo(c1) > 0);
		assertFalse(c3.compareTo(c1) < 0);
		assertEquals(0, c3.compareTo(c2));
		assertEquals(0, c3.compareTo(c3));
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithoutConsumption(){
		int userID = 1;
		UserEntry entry = c1.calculateReceipt(userID, 0, 0, 0, 0, 0, 0, 0);
		assertEquals(price, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getPenalty(), 0.0001);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithConsumptionLowerThanCPULimit(){
		int userID = 1;
		
		UserEntry entry = c1.calculateReceipt(userID, 10, 0, 0, 0, 0, 0, 0);

		assertEquals(price, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getPenalty(), 0.0001);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithConsumptionHigherThanCPULimit(){
		long extraConsumedCpu = 5 * TimeUnit.HOUR.getMillis();
		int userID = 1;
		
		UserEntry entry = c1.calculateReceipt(userID, cpuLimit + extraConsumedCpu, 0, 0, 0, 0, 0, 0);
		
		assertEquals(price + extraConsumedCpu * extraCpuCost, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getPenalty(), 0.0001);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithTransferConsumptionBelowMinimum(){
		long transferenceInBytes = 1024 * DataUnit.MB.getBytes();
		int userID = 1;
		
		UserEntry entry = c1.calculateReceipt(userID, cpuLimit, transferenceInBytes, 0, 0, 0, 0, 0);
		
		assertEquals(price, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getPenalty(), 0.0001);
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
		
		UserEntry entry = c1.calculateReceipt(userID, cpuLimit, transferenceInBytes, 0, 0, 0, 0, 0);
		assertEquals(price + expectedCost, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getPenalty(), 0.0001);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithStorageConsumptionBelowMinimum(){
		int userID = 1;

		UserEntry entry = c1.calculateReceipt(userID, cpuLimit, 0, 0, storageLimits, 0, 0, 0);
		assertEquals(price, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getPenalty(), 0.0001);
	}
	
	/**
	 * Test method for {@link Contract#calculateReceipt(long, long, long, long)}
	 */
	@Test
	public void testCalculateReceiptWithStorageConsumptionAboveMaximum(){
		long extraStorageConsumedInBytes = 10 * DataUnit.MB.getBytes();
		int userID = 1;

		UserEntry entry = c1.calculateReceipt(userID, cpuLimit, 0, 0, (storageLimits + extraStorageConsumedInBytes), 0, 0, 0);
		assertEquals(price + extraStorageConsumedInBytes * storageCosts, entry.getReceipt(), 0.0001);
		assertEquals(0, entry.getPenalty(), 0.0001);
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
		assertEquals(0, c1.calculatePenalty(0, 0), 0.0);
		assertEquals(0, c2.calculatePenalty(0, 0), 0.0);
		assertEquals(0, c3.calculatePenalty(0, 0), 0.0);
	}
	
	@Test
	public void testCalculatePenaltyWithSmallestLoss(){
		assertEquals(0, c1.calculatePenalty(0.0001, 0), 0.0);
		assertEquals(price / 4, c1.calculatePenalty(0.005, 0), 0.0);
		assertEquals(price / 2, c1.calculatePenalty(0.05, 0), 0.0);
		assertEquals(price, c1.calculatePenalty(0.09999999, 0), 0.0);
		assertEquals(price, c1.calculatePenalty(0.1, 0), 0.0);
	}
	
	@Test
	public void testCalculatePenaltyWithLoss(){
		assertEquals(price / 4, c1.calculatePenalty(0.005, 0), 0.0);
		assertEquals(price / 4, c1.calculatePenalty(0.01, 0), 0.0);
		assertEquals(0, c1.calculatePenalty(0.001, 0), 0.0);
	}
	
	@Test
	public void testCalculatePenaltyWithHigherLoss(){
		assertEquals(price, c1.calculatePenalty(0.100001, 0), 0.0);
		assertEquals(price, c1.calculatePenalty(0.5, 0), 0.0);
		assertEquals(price, c1.calculatePenalty(0.99999, 0), 0.0);
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
