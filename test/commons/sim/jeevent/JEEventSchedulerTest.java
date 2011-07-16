package commons.sim.jeevent;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JEEventSchedulerTest {
	
	private JEEventScheduler scheduler;

	@Before
	public void setUp() throws Exception {
		scheduler = new JEEventScheduler();
	}

	@After
	public void tearDown() throws Exception {
		scheduler = null;
	}

	@Test(expected=JEException.class)
	public void testQueueEventWithUnregisteredHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, new JETime(1000)));
		scheduler.queueEvent(new JEEvent(JEEventType.NEWREQUEST, handler, new JETime(1000)));
		scheduler.start();
		EasyMock.verify(handler);
	}

	@Test
	public void testQueueEventWithRegisteredHandler() {
		
		final JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() throws Throwable {
				return scheduler.registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, new JETime(1000)));
		scheduler.start();
		
		EasyMock.verify(handler);
	}

	@Test
	public void testCancelEvent() {
		
		final JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() throws Throwable {
				return scheduler.registerHandler(handler);
			}
		}).once();
		EasyMock.replay(handler);
		
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, new JETime(1000));
		scheduler.queueEvent(event);
		scheduler.cancelEvent(event);
		scheduler.start();
		
		EasyMock.verify(handler);
	}

	@Test
	public void testRegisterHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		EasyMock.replay(handler);
		int id = scheduler.registerHandler(handler);
		assertEquals(handler, scheduler.getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
		assertEquals(new JETime(0L), scheduler.now());
		scheduler.start();
		assertEquals(JETime.INFINITY, scheduler.now());
	}
	
	@Test
	public void testNowWithFiniteSimulator() {
		scheduler.setEmulationEnd(new JETime(3600000L));
		assertEquals(new JETime(0L), scheduler.now());
		scheduler.start();
		assertEquals(new JETime(3600000L), scheduler.now());
	}

}
