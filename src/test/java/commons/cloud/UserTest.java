/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import util.CleanConfigurationTest;

/**
 * Test class for {@link User}
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UserTest extends CleanConfigurationTest {
	
	private static final int STORAGE_IN_BYTES = 1024000;
	private static final double SETUP = 1000.0;
	
	/**
	 * Test method for {@link commons.cloud.User#calculatePartialReceipt()}.
	 */
	@Test
	public void testCalculateReceipt() {
		double penalty = 0d;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addPenalty(1, penalty, 0, 0);
		
		Contract contract = EasyMock.createStrictMock(Contract.class);
		
		User user = new User(1, contract, STORAGE_IN_BYTES);
		
		EasyMock.expect(contract.calculatePenalty(Double.NaN)).andReturn(penalty);
		contract.calculateReceipt(entry, user.getId(), 0, 0, 0, STORAGE_IN_BYTES);
		EasyMock.replay(contract, entry);
	
		user.calculatePartialReceipt(entry);
	
		EasyMock.verify(contract, entry);
	}

	/**
	 * Test method for {@link commons.cloud.User#calculateOneTimeFees()}.
	 */
	@Test
	public void testCalculateOneTimeFees() {
		Contract contract = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(contract.calculateOneTimeFees()).andReturn(SETUP);
		EasyMock.replay(contract);
		
		User user = new User(0, contract, STORAGE_IN_BYTES);
		user.calculateOneTimeFees(new UtilityResult(1, 0));
	
		EasyMock.verify(contract);
	}

	/**
	 * Test method for {@link commons.cloud.User#compareTo(User)}.
	 */
	@Test
	public void testCompareTo() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		Contract silver = EasyMock.createStrictMock(Contract.class);
		
		EasyMock.expect(gold.compareTo(gold)).andReturn(0).times(2);
		EasyMock.expect(gold.compareTo(silver)).andReturn(-1);
		EasyMock.expect(silver.compareTo(gold)).andReturn(1);
		
		EasyMock.replay(gold, silver);
		
		User goldUser1 = new User(1, gold, STORAGE_IN_BYTES);
		User goldUser2 = new User(2, gold, STORAGE_IN_BYTES);
		User silverUser = new User(3, silver, STORAGE_IN_BYTES);
		
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
		long totalProcessed = 250;
		long requestSize = 1024 * 100;
		long responseSize = 1024 * 1024 * 5;
		Double penalty = 0d;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addPenalty(1, penalty, 2, 2);
		
		Contract gold = EasyMock.createStrictMock(Contract.class);
		
		User user = new User(1, gold , STORAGE_IN_BYTES);
		
		EasyMock.expect(gold.calculatePenalty(0.0)).andReturn(penalty);
		gold.calculateReceipt(entry, user.getId(), 2*totalProcessed, 2*requestSize, 2*responseSize, STORAGE_IN_BYTES);
		
		Request request = EasyMock.createStrictMock(Request.class);;
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(responseSize);
		
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(responseSize);
		
		
		EasyMock.replay(gold, request, entry);
		
		user.reportFinishedRequest(request);
		user.reportFinishedRequest(request);
		
		assertEquals(STORAGE_IN_BYTES, user.getStorageInBytes());
		assertEquals(totalProcessed * 2, user.getConsumedCpuInMillis());
		assertEquals(requestSize * 2, user.getConsumedInTransferenceInBytes());
		assertEquals(responseSize * 2, user.getConsumedOutTransferenceInBytes());
		
		user.calculatePartialReceipt(entry);
		
		EasyMock.verify(gold, request, entry);
		
		assertEquals(STORAGE_IN_BYTES, user.getStorageInBytes());
		assertEquals(0, user.getConsumedCpuInMillis());
		assertEquals(0, user.getConsumedInTransferenceInBytes());
		assertEquals(0, user.getConsumedOutTransferenceInBytes());
	}

	/**
	 * Test method for {@link User#reportLostRequest(Request)} 
	 */
	@Test
	public void testReportLostRequest(){
		long totalProcessed = 250;
		long requestSize = 1024 * 100;
		Double penalty = 100d;
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addPenalty(1, penalty, 0, 2);
		
		Contract gold = EasyMock.createStrictMock(Contract.class);
		
		User user = new User(1, gold , STORAGE_IN_BYTES);
		
		EasyMock.expect(gold.calculatePenalty(1.0)).andReturn(penalty);
		gold.calculateReceipt(entry, user.getId(), 2*totalProcessed, 2*requestSize, 0, STORAGE_IN_BYTES);
		
		Request request = EasyMock.createStrictMock(Request.class);;
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		
		EasyMock.expect(request.getTotalProcessed()).andReturn(totalProcessed);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(requestSize);
		
		EasyMock.replay(gold, request, entry);
		
		user.reportLostRequest(request);
		user.reportLostRequest(request);
		
		assertEquals(2, user.getNumberOfLostRequests());

		user.calculatePartialReceipt(entry);
		
		assertEquals(0, user.getNumberOfLostRequests());
		
		EasyMock.verify(gold, request, entry);
	}
	
	@Test
	public void testCalculatePenaltyWithoutLoss(){
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(gold.calculatePenalty(0)).andReturn(0d);
		EasyMock.replay(gold);
		
		User user = new User(1, gold , STORAGE_IN_BYTES);
		
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
		
		User user = new User(1, gold , STORAGE_IN_BYTES);
		
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
		
		User user = new User(1, gold , STORAGE_IN_BYTES);
		
		assertEquals(10, user.calculatePenalty(0.25), 0.0);
		assertEquals(10, user.calculatePenalty(0.75), 0.0);
		assertEquals(10, user.calculatePenalty(0.99999), 0.0);
		
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link commons.cloud.User#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencySameRequest() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);

		User user = new User(1, gold , STORAGE_IN_BYTES);
		assertEquals(user, user);
		assertTrue(user.hashCode() == user.hashCode());
		
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithSameID() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		User userA = new User(1, gold , STORAGE_IN_BYTES);
		User cloneUserA = new User(1, gold , STORAGE_IN_BYTES);
		
		assertEquals(userA, cloneUserA);
		assertTrue(userA.hashCode() == cloneUserA.hashCode());
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithDifferentID() {
		
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		User userA = new User(1, gold , STORAGE_IN_BYTES);
		User userB = new User(2, gold , STORAGE_IN_BYTES);
		
		assertTrue(!userA.equals(userB));
		assertTrue(userA.hashCode() != userB.hashCode());
		EasyMock.verify(gold);
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithNullObject() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		new User(1, gold , STORAGE_IN_BYTES).equals(null);
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		EasyMock.replay(gold);
		
		new User(1, gold , STORAGE_IN_BYTES).equals(new String(""));
	}


}
