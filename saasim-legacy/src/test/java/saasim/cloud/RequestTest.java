/**
 * 
 */
package saasim.cloud;

import static org.junit.Assert.*;

import org.junit.Test;

import saasim.util.CleanConfigurationTest;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class RequestTest extends CleanConfigurationTest {
	
	private static final int MICRO_DEMAND = 2000000;
	private static final int SMALL_DEMAND = 1000000;
	private static final int LARGE_DEMAND = 500000;
	private static final int MEDIUM_DEMAND = 400000;
	private static final int XLARGE_DEMAND = 300000;
	private Request request;
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
		request = new Request(1l, 1, 17756636, 0, 100, 1024000, new long[]{SMALL_DEMAND, LARGE_DEMAND,  XLARGE_DEMAND, MEDIUM_DEMAND, MICRO_DEMAND});
	}
	
	@Test
	public void testConstructor() {
		Request request = new Request(1l, 1, 17756636, 0, 100, 1024000, new long[]{MICRO_DEMAND, SMALL_DEMAND, LARGE_DEMAND, MEDIUM_DEMAND, XLARGE_DEMAND});
		assertEquals(1, request.getReqID());
		assertEquals(1, request.getSaasClient());
		assertEquals(17756636, request.getUserID());
		assertEquals(0, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(1024000, request.getResponseSizeInBytes());
		assertEquals(5, request.getCpuDemandInMillis().length);
	}

	/**
	 * Test method for {@link saasim.cloud.Request#assignTo(MachineType)}.
	 */
	@Test
	public void testAssign() {
		request.assignTo(MachineType.M1_SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		request.assignTo(MachineType.M1_LARGE);
		assertEquals(LARGE_DEMAND, request.getTotalToProcess());
		request.assignTo(MachineType.M1_XLARGE);
		assertEquals(XLARGE_DEMAND, request.getTotalToProcess());
	}

	/**
	 * Test method for {@link saasim.cloud.Request#update(long)}.
	 */
	@Test
	public void testUpdate() {
		request.assignTo(MachineType.M1_SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		assertEquals(0, request.getTotalProcessed());
		request.update(100);
		assertEquals(SMALL_DEMAND-100, request.getTotalToProcess());
		assertEquals(100, request.getTotalProcessed());
	}
	
	@Test
	public void testUpdateWithReset() {
		request.assignTo(MachineType.M1_SMALL);
		request.update(100);
		assertEquals(SMALL_DEMAND-100, request.getTotalToProcess());
		assertEquals(100, request.getTotalProcessed());
		
		request.reset();
		assertNull(request.getValue());
		
		request.assignTo(MachineType.M1_SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		assertEquals(0, request.getTotalProcessed());
	}

	/**
	 * Test method for {@link saasim.cloud.Request#update(long)}.
	 */
	@Test
	public void testUpdateWithMoreThanRequiredToFinish() {
		request.assignTo(MachineType.M1_SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		request.update(SMALL_DEMAND + 100);
		assertEquals(0, request.getTotalToProcess());
	}

	/**
	 * Test method for {@link saasim.cloud.Request#update(long)}.
	 */
	@Test(expected=AssertionError.class)
	public void testUpdateWithNegativeDemand() {
		request.assignTo(MachineType.M1_SMALL);
		assertEquals(SMALL_DEMAND, request.getTotalToProcess());
		request.update(-100);
	}

	/**
	 * Test method for {@link saasim.cloud.Request#isFinished()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testIsFinishedWithoutAssigningBefore() {
		request.isFinished();
	}

	/**
	 * Test method for {@link saasim.cloud.Request#isFinished()}.
	 */
	@Test
	public void testIsFinishedWithAssignment() {
		request.assignTo(MachineType.M1_XLARGE);
		assertFalse(request.isFinished());
		while(!request.isFinished()){
			request.update(100);
		}
		assertTrue(request.isFinished());
		assertEquals(0, request.getTotalToProcess());
	}

	/**
	 * Test method for {@link saasim.cloud.Request#getTotalToProcess()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetTotalToProcessWithoutAssigningBefore() {
		request.getTotalToProcess();
	}
	
	@Test
	public void testGetTotalMeanToProcess() {
		long mean = (MICRO_DEMAND + SMALL_DEMAND + LARGE_DEMAND + MEDIUM_DEMAND + XLARGE_DEMAND)/5;
		assertEquals(mean, request.getTotalMeanToProcess());
	}

	/**
	 * Test method for {@link saasim.cloud.Request#reset()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testReset() {
		request.assignTo(MachineType.M1_XLARGE);
		assertEquals(XLARGE_DEMAND, request.getTotalToProcess());
		request.update(100);
		assertEquals(XLARGE_DEMAND-100, request.getTotalToProcess());
		request.reset();
		request.getTotalToProcess();
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencySameRequest() {
		Request cloneRequest = new Request(1l, 1, 17756636, 0, 100, 1024000, new long[]{MICRO_DEMAND, SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
		assertEquals(request, cloneRequest);
		assertEquals(request.hashCode(), cloneRequest.hashCode());
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithRequestID() {
		Request cloneRequest = new Request(2l, 1, 17756636, 1, 100, 1024000, new long[]{MICRO_DEMAND, SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
		assertTrue(!request.equals(cloneRequest));
		assertTrue(request.hashCode() != cloneRequest.hashCode());
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test
	public void testEqualsHashCodeConsistencyWithDifferentSaaSClient() {
		Request cloneRequest = new Request(1l, 2, 17756636, 0, 100, 1024000, new long[]{MICRO_DEMAND, SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
		assertTrue(!request.equals(cloneRequest));
		assertTrue(request.hashCode() != cloneRequest.hashCode());
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithNullObject() {
		request.equals(null);
	}
	
	/**
	 * Test method for {@link saasim.cloud.Request#equals()} and 
	 * {@link saasim.cloud.Request#hashCode()}.
	 */
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject() {
		request.equals(new String(""));
	}
	

}
