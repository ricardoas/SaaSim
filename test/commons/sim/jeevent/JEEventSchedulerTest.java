package commons.sim.jeevent;

import static commons.sim.jeevent.JEEventScheduler.SCHEDULER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class JEEventSchedulerTest {

	@Before
	public void setUp() throws Exception {
		SCHEDULER.clear();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		SCHEDULER.clear();
	}

	@Test
	public void testQueue_event() {
		fail("Not yet implemented");
	}

	@Test
	public void testCancel_event() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).once();
		EasyMock.replay(handler);
		int id = SCHEDULER.registerHandler(handler);
		assertEquals(handler, SCHEDULER.getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testNowWithBoundlessSimulator() {
		assertEquals(new JETime(0L), SCHEDULER.now());
		SCHEDULER.start();
		assertEquals(JETime.INFINITY, SCHEDULER.now());
	}

	@Test
	public void testNowWithFiniteSimulator() {
		SCHEDULER.setEmulationEnd(new JETime(3600000L));
		assertEquals(new JETime(0L), SCHEDULER.now());
		SCHEDULER.start();
		assertEquals(new JETime(3600000L), SCHEDULER.now());
	}

}
