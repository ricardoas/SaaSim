/**
 * 
 */
package saasim.cloud;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import saasim.util.DataUnit;
import saasim.util.TimeUnit;
import saasim.util.ValidConfigurationTest;


/**
 * Test class for {@link User}
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UserTest extends ValidConfigurationTest {
	
	private String plan1 = "bronze";
	private static final int HIGH = 100;
	private double setupCost = 1000.0;
	private double price = 24.95;
	private long cpuLimit = 10 * TimeUnit.HOUR.getMillis();
	private double extraCpuCost = 5.0/TimeUnit.HOUR.getMillis();
	private long [] transferenceLimits = DataUnit.convert(new long[]{2048}, DataUnit.MB, DataUnit.B);
	private double [] transferenceCosts = DataUnit.convert(new double[]{0, 0.005}, DataUnit.B, DataUnit.MB);
	private long storageLimits = 200 * DataUnit.MB.getBytes();
	private double storageCosts = 0.1 / DataUnit.MB.getBytes();

	private Contract c1;
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
		c1 = new Contract(plan1, HIGH, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts, storageLimits, storageCosts);
	}


	
	/**
	 * Test method for {@link saasim.cloud.User#calculateOneTimeFees()}.
	 */
	@Test
	public void testCalculateOneTimeFees() {
		User user = new User(0, c1, storageLimits);
		assertEquals(setupCost, user.calculateOneTimeFees(), 0.0001);
	}

	/**
	 * Test method for {@link saasim.cloud.User#compareTo(User)}.
	 */
	@Ignore("Semantic of priority field changed!")
	@Test
	public void testCompareTo() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		Contract silver = EasyMock.createStrictMock(Contract.class);
		
		EasyMock.expect(gold.compareTo(gold)).andReturn(0).times(2);
		EasyMock.expect(gold.compareTo(silver)).andReturn(-1);
		EasyMock.expect(silver.compareTo(gold)).andReturn(1);
		
		EasyMock.replay(gold, silver);
		
		User goldUser1 = new User(1, gold, storageLimits);
		User goldUser2 = new User(2, gold, storageLimits);
		User silverUser = new User(3, silver, storageLimits);
		
		assertEquals(0, goldUser1.compareTo(goldUser2));
		assertEquals(0, goldUser2.compareTo(goldUser1));
		
		assertEquals(-1, goldUser1.compareTo(silverUser));
		assertEquals(1, silverUser.compareTo(goldUser1));
		
		EasyMock.verify(gold, silver);
	}
	
	/**
	 * Test method for {@link User#reportFinishedRequest(Request)} 
	 */
	@Test
	public void testReportFinishedRequest(){
		long totalProcessed = 6 * TimeUnit.HOUR.getMillis();
		long requestSize = 1024 * 100;
		long responseSize = 1024 * 1024 * 5;
		
		
		User user = new User(1, c1 , storageLimits);
		
		Request request = EasyMock.createStrictMock(Request.class);;
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(responseSize);
		
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(responseSize);
		
		EasyMock.replay(request);
		
		assertEquals(price, user.calculatePartialReceipt().getReceipt(), 0.0001);
		
		user.reportFinishedRequest(request);
		user.reportFinishedRequest(request);
		
		assertEquals(price + 2 * 5.0 , user.calculatePartialReceipt().getReceipt(), 0.0001);
		
		EasyMock.verify(request);
	}

	/**
	 * Test method for {@link User#reportLostRequest(Request)} 
	 */
	@Test
	public void testReportLostRequest(){
		long totalProcessed = 250;
		long requestSize = 1024 * 100;
		
		User user = new User(1, c1 , storageLimits);
		
		Request request = EasyMock.createStrictMock(Request.class);;
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		
		EasyMock.replay(request);
		
		assertEquals(0, user.calculatePartialReceipt().getPenalty(), 0.0001);

		user.reportLostRequest(request);
		user.reportLostRequest(request);
		
		assertEquals(price, user.calculatePartialReceipt().getPenalty(), 0.0001);
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testCalculatePenaltyWithoutLoss(){
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(gold.calculatePenalty(0)).andReturn(0d);
		EasyMock.replay(gold);
		
		User user = new User(1, gold , storageLimits);
		
		assertEquals(0, user.calculatePenalty(0), 0.0);
		
		EasyMock.verify(gold);
	}
	
	@Test
	public void testCalculatePenaltyWithSmallLoss(){
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(gold.calculatePenalty(0)).andReturn(10d);
		EasyMock.expect(gold.calculatePenalty(0.0001)).andReturn(10d);
		EasyMock.expect(gold.calculatePenalty(0.1)).andReturn(10d);
		EasyMock.replay(gold);
		
		User user = new User(1, gold , storageLimits);
		
		assertEquals(10, user.calculatePenalty(0), 0.0);
		assertEquals(10, user.calculatePenalty(0.0001), 0.0);
		assertEquals(10, user.calculatePenalty(0.1), 0.0);
		
		EasyMock.verify(gold);
	}
	
	@Test
	public void testCalculatePenaltyWithHigherLoss(){
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(gold.calculatePenalty(0.25)).andReturn(10d);
		EasyMock.expect(gold.calculatePenalty(0.75)).andReturn(10d);
		EasyMock.expect(gold.calculatePenalty(0.99999)).andReturn(10d);
		EasyMock.replay(gold);
		
		User user = new User(1, gold , storageLimits);
		
		assertEquals(10, user.calculatePenalty(0.25), 0.0);
		assertEquals(10, user.calculatePenalty(0.75), 0.0);
		assertEquals(10, user.calculatePenalty(0.99999), 0.0);
		
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link saasim.cloud.User#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencySameRequest() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);

		User user = new User(1, gold , storageLimits);
		assertEquals(user, user);
		assertTrue(user.hashCode() == user.hashCode());
		
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithSameID() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		User userA = new User(1, gold , storageLimits);
		User cloneUserA = new User(1, gold , storageLimits);
		
		assertEquals(userA, cloneUserA);
		assertTrue(userA.hashCode() == cloneUserA.hashCode());
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithDifferentID() {
		
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		User userA = new User(1, gold , storageLimits);
		User userB = new User(2, gold , storageLimits);
		
		assertTrue(!userA.equals(userB));
		assertTrue(userA.hashCode() != userB.hashCode());
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithNullObject() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		new User(1, gold , storageLimits).equals(null);
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		new User(1, gold , storageLimits).equals(new String(""));
	}


}
