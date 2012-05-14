package saasim.sim.core;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Ignore;
import org.junit.Test;

import saasim.sim.core.AbstractEventHandler;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.Event;
import saasim.sim.core.EventHandler;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.util.ValidConfigurationTest;


public class EventSchedulerTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}
	
	@Test
	public void testRegisterHandler() {
		AbstractEventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.replay(handler);
		EventScheduler scheduler = new EventScheduler(EventCheckpointer.INTERVAL);
		int id = scheduler.registerHandler(handler);
		assertEquals(handler, scheduler.getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
		EventScheduler scheduler = new EventScheduler(EventCheckpointer.INTERVAL);
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(EventCheckpointer.INTERVAL, scheduler.now());
	}

	@Test(expected=AssertionError.class)
	public void testQueueEventWithUnregisteredHandler() {
		EventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		EventScheduler scheduler = new EventScheduler(EventCheckpointer.INTERVAL);
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new Event(EventType.NEWREQUEST, handler, 1000));
		scheduler.start();
		EasyMock.verify(handler);
	}

	@Ignore @Test
	public void testQueueEventWithRegisteredHandler() {
		
		final EventScheduler scheduler = new EventScheduler(EventCheckpointer.INTERVAL);
		final AbstractEventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
//		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 1000));
		scheduler.start();
		
		EasyMock.verify(handler);
	}
	
	@Ignore @Test(expected=AssertionError.class)
	public void testQueueEventWithNegativeTime() {
		final AbstractEventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		final EventScheduler scheduler = new EventScheduler(EventCheckpointer.INTERVAL);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
//		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		assertEquals(0, scheduler.now());
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, -1));
	}
	
	@Ignore @Test
	public void testQueueEventAfterEndTime() {
		final EventScheduler scheduler = new EventScheduler(3600);
		final AbstractEventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).times(3);
//		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(handler);
		
		//Queuing past event
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 3600));
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 3601));
	}

	@Test
	public void testCancelEvent() {
		
		final AbstractEventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		final EventScheduler scheduler = new EventScheduler(EventCheckpointer.INTERVAL);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
		EasyMock.replay(handler);
		
		Event event = new Event(EventType.READWORKLOAD, handler, 1000);
		scheduler.queueEvent(event);
		scheduler.cancelEvent(event);
		scheduler.start();
		assertEquals(EventCheckpointer.INTERVAL, scheduler.now());
		
		EasyMock.verify(handler);
	}

	@Test
	public void testNowWithFiniteSimulator() {
		EventScheduler scheduler = new EventScheduler(3600000L);
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(3600000, scheduler.now());
	}
	
	@Ignore @Test(expected=RuntimeException.class)
	public void testQueueEventWithPastEvent() {
		
		final EventScheduler scheduler = new EventScheduler(3600000L);
		final AbstractEventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
//		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		scheduler.start();
		assertEquals(3600000, scheduler.now());
		
		//Queuing past event
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 1));
	}
	
	@Ignore@Test
	public void testExecutingEventsAndCheckingCurrentTime() {
		
		final EventScheduler scheduler = new EventScheduler(3600000L);
		final AbstractEventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).times(2);
//		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(handler);
		
		//Queuing past event
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new Event(EventType.READWORKLOAD, handler, 55000));

		scheduler.start();
		assertEquals(55000, scheduler.now());
		
		EasyMock.verify(handler);
	}
	
	

}
