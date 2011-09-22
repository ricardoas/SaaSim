/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class RequestTest {

	private static final int XLARGE_DEMAND = 300000;
	private static final int LARGE_DEMAND = 500000;
	private static final int SMALL_DEMAND = 1000000;
	private Request request;
	
	@Before
	public void setUp(){
		request = new Request(1l, 1, 17756636, 0, 100, 1024000, new long[]{SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
	}

	/**
	 * Test method for {@link commons.cloud.Request#assignTo(MachineType)}.
	 */
	@Test
	public void testAssign() {
		request.assignTo(MachineType.SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		request.assignTo(MachineType.LARGE);
		assertEquals(LARGE_DEMAND, request.getTotalToProcess());
		request.assignTo(MachineType.XLARGE);
		assertEquals(XLARGE_DEMAND, request.getTotalToProcess());
	}

	/**
	 * Test method for {@link commons.cloud.Request#update(long)}.
	 */
	@Test
	public void testUpdate() {
		request.assignTo(MachineType.SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		assertEquals(0, request.getTotalProcessed());
		request.update(100);
		assertEquals(SMALL_DEMAND-100, request.getTotalToProcess());
		assertEquals(100, request.getTotalProcessed());
	}

	/**
	 * Test method for {@link commons.cloud.Request#update(long)}.
	 */
	@Test
	public void testUpdateWithMoreThanRequiredToFinish() {
		request.assignTo(MachineType.SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		request.update(SMALL_DEMAND + 100);
		assertEquals(0, request.getTotalToProcess());
	}

	/**
	 * Test method for {@link commons.cloud.Request#update(long)}.
	 */
	@Test(expected=AssertionError.class)
	public void testUpdateWithNegativeDemand() {
		request.assignTo(MachineType.SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		request.update(-100);
	}

	/**
	 * Test method for {@link commons.cloud.Request#isFinished()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testIsFinishedWithoutAssigningBefore() {
		request.isFinished();
	}

	/**
	 * Test method for {@link commons.cloud.Request#isFinished()}.
	 */
	@Test
	public void testIsFinishedWithAssignment() {
		request.assignTo(MachineType.XLARGE);
		assertFalse(request.isFinished());
		while(!request.isFinished()){
			request.update(100);
		}
		assertTrue(request.isFinished());
		assertEquals(0, request.getTotalToProcess());
	}

	/**
	 * Test method for {@link commons.cloud.Request#getTotalToProcess()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetTotalToProcessWithoutAssigningBefore() {
		request.getTotalToProcess();
	}

	/**
	 * Test method for {@link commons.cloud.Request#reset()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testReset() {
		request.assignTo(MachineType.XLARGE);
		assertEquals(XLARGE_DEMAND, request.getTotalToProcess());
		request.update(100);
		assertEquals(XLARGE_DEMAND-100, request.getTotalToProcess());
		request.reset();
		request.getTotalToProcess();
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencySameRequest() {
		Request cloneRequest = new Request(1l, 1, 17756636, 0, 100, 1024000, new long[]{SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
		assertEquals(request, cloneRequest);
		assertEquals(request.hashCode(), cloneRequest.hashCode());
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithRequestID() {
		Request cloneRequest = new Request(2l, 1, 17756636, 1, 100, 1024000, new long[]{SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
		assertTrue(!request.equals(cloneRequest));
		assertTrue(request.hashCode() != cloneRequest.hashCode());
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithDifferentSaaSClient() {
		Request cloneRequest = new Request(1l, 2, 17756636, 0, 100, 1024000, new long[]{SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
		assertTrue(!request.equals(cloneRequest));
		assertTrue(request.hashCode() != cloneRequest.hashCode());
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithNullObject() {
		request.equals(null);
	}
	
	/**
	 * Test method for {@link commons.cloud.Request#equals()} and 
	 * {@link commons.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject() {
		request.equals(new String(""));
	}
	

}
