package commons.sim.jeevent;

import static commons.sim.jeevent.JEEventScheduler.SCHEDULER;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
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

	@Test(expected=JEException.class)
	public void testQueueEventWithUnregisteredHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		SCHEDULER.queueEvent(new JEEvent(JEEventType.READWORKLOAD, "eventA", handler, new JETime(1000)));
		SCHEDULER.queueEvent(new JEEvent(JEEventType.LESSIMPORTANTEVENT, "eventB", handler, new JETime(1000)));
		SCHEDULER.start();
		EasyMock.verify(handler);
	}

	@Test
	public void testQueueEventWithRegisteredHandler() {
		
		final JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() throws Throwable {
				return SCHEDULER.registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		SCHEDULER.queueEvent(new JEEvent(JEEventType.READWORKLOAD, "eventA", handler, new JETime(1000)));
		SCHEDULER.start();
		
		EasyMock.verify(handler);
	}

	@Test
	public void testCancelEvent() {
		
		final JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() throws Throwable {
				return SCHEDULER.registerHandler(handler);
			}
		}).once();
		EasyMock.replay(handler);
		
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, "eventA", handler, new JETime(1000));
		SCHEDULER.queueEvent(event);
		SCHEDULER.cancelEvent(event);
		SCHEDULER.start();
		
		EasyMock.verify(handler);
	}

	@Test
	public void testRegisterHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.replay(handler);
		int id = SCHEDULER.registerHandler(handler);
		assertEquals(handler, SCHEDULER.getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
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
