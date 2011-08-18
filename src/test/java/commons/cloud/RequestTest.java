/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
		request = new Request("1", "c1", "user1", 0, 100, 1024000, new long[]{SMALL_DEMAND, LARGE_DEMAND, XLARGE_DEMAND});
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
		request.update(100);
	}

	/**
	 * Test method for {@link commons.cloud.Request#isFinished()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testIsFinishedWithoutAssigningBefore() {
		request.isFinished();
	}

	/**
	 * Test method for {@link commons.cloud.Request#getTotalToProcess()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetTotalToProcessWithoutAssigningBefore() {
		request.getTotalToProcess();
	}

	/**
	 * Test method for {@link commons.cloud.Request#getDemand()}.
	 */
	@Test
	public void testGetDemand() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.cloud.Request#reset()}.
	 */
	@Test
	public void testReset() {
		fail("Not yet implemented");
	}

}
