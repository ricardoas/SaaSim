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
	public void setUp() {
		scheduler = new JEEventScheduler();
	}

	@After
	public void tearDown() {
		scheduler = null;
	}
	
	@Test
	public void testRegisterHandler() {
		JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.replay(handler);
		int id = scheduler.registerHandler(handler);
		assertEquals(handler, scheduler.getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(JETime.INFINITY.timeMilliSeconds, scheduler.now());
	}

	@Test(expected=JEException.class)
	public void testQueueEventWithUnregisteredHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.NEWREQUEST, handler, 1000));
		scheduler.start();
		EasyMock.verify(handler);
	}

	@Test
	public void testQueueEventWithRegisteredHandler() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.start();
		
		EasyMock.verify(handler);
	}
	
	@Test(expected=JEException.class)
	public void testQueueEventWithNegativeTime() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		assertEquals(0, scheduler.now());
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, -1));
	}
	
	@Test(expected=JEException.class)
	public void testQueueEventAfterEndTime() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).times(3);
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(handler);
		
		scheduler.setEmulationEnd(3600L);
		
		//Queuing past event
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3600));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3601));
	}

	@Test
	public void testCancelEvent() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
		EasyMock.replay(handler);
		
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1000);
		scheduler.queueEvent(event);
		scheduler.cancelEvent(event);
		scheduler.start();
		assertEquals(JETime.INFINITY.timeMilliSeconds, scheduler.now());
		
		EasyMock.verify(handler);
	}

	@Test
	public void testNowWithFiniteSimulator() {
		scheduler.setEmulationEnd(3600000L);
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(3600000L, scheduler.now());
	}
	
	@Test(expected=JEException.class)
	public void testQueueEventWithPastEvent() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		scheduler.setEmulationEnd(3600000L);
		scheduler.start();
		assertEquals(3600000L, scheduler.now());
		
		//Queuing past event
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1));
	}
	
	@Test
	public void testExecutingEventsAndCheckingCurrentTime() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).times(2);
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(handler);
		
		scheduler.setEmulationEnd(3600000L);
		
		//Queuing past event
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 55000));

		scheduler.start();
		assertEquals(55000, scheduler.now());
		
		EasyMock.verify(handler);
	}
	
	

}
