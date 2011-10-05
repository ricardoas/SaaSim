package commons.sim.jeevent;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

import util.CleanConfigurationTest;

public class JEEventSchedulerTest extends CleanConfigurationTest {
	
	@Test
	public void testRegisterHandler() {
		JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.replay(handler);
		JEEventScheduler scheduler = new JEEventScheduler();
		int id = scheduler.registerHandler(handler);
		assertEquals(handler, scheduler.getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
		JEEventScheduler scheduler = new JEEventScheduler();
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(Long.MAX_VALUE, scheduler.now());
	}

	@Test(expected=AssertionError.class)
	public void testQueueEventWithUnregisteredHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		JEEventScheduler scheduler = new JEEventScheduler();
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.NEWREQUEST, handler, 1000));
		scheduler.start();
		EasyMock.verify(handler);
	}

	@Test
	public void testQueueEventWithRegisteredHandler() {
		
		final JEEventScheduler scheduler = new JEEventScheduler();
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
	
	@Test(expected=AssertionError.class)
	public void testQueueEventWithNegativeTime() {
		final JEEventScheduler scheduler = new JEEventScheduler();
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
	
	@Test
	public void testQueueEventAfterEndTime() {
		final JEEventScheduler scheduler = new JEEventScheduler(3600L);
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
		
		//Queuing past event
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3600));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3601));
	}

	@Test
	public void testCancelEvent() {
		
		final JEEventScheduler scheduler = new JEEventScheduler();
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
		assertEquals(Long.MAX_VALUE, scheduler.now());
		
		EasyMock.verify(handler);
	}

	@Test
	public void testNowWithFiniteSimulator() {
		JEEventScheduler scheduler = new JEEventScheduler(3600000L);
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(3600000L, scheduler.now());
	}
	
	@Test(expected=JEException.class)
	public void testQueueEventWithPastEvent() {
		
		final JEEventScheduler scheduler = new JEEventScheduler(3600000L);
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
		
		scheduler.start();
		assertEquals(3600000L, scheduler.now());
		
		//Queuing past event
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1));
	}
	
	@Test
	public void testExecutingEventsAndCheckingCurrentTime() {
		
		final JEEventScheduler scheduler = new JEEventScheduler(3600000L);
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
		
		//Queuing past event
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 55000));

		scheduler.start();
		assertEquals(55000, scheduler.now());
		
		EasyMock.verify(handler);
	}
	
	

}
