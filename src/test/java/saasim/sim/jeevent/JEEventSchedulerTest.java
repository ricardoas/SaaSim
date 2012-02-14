package saasim.sim.jeevent;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Ignore;
import org.junit.Test;

import saasim.sim.jeevent.JEAbstractEventHandler;
import saasim.sim.jeevent.JECheckpointer;
import saasim.sim.jeevent.JEEvent;
import saasim.sim.jeevent.JEEventHandler;
import saasim.sim.jeevent.JEEventScheduler;
import saasim.sim.jeevent.JEEventType;
import saasim.sim.jeevent.JEException;
import saasim.util.ValidConfigurationTest;


public class JEEventSchedulerTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}
	
	@Test
	public void testRegisterHandler() {
		JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.replay(handler);
		JEEventScheduler scheduler = new JEEventScheduler(JECheckpointer.INTERVAL);
		int id = scheduler.registerHandler(handler);
		assertEquals(handler, scheduler.getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
		JEEventScheduler scheduler = new JEEventScheduler(JECheckpointer.INTERVAL);
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(JECheckpointer.INTERVAL, scheduler.now());
	}

	@Test(expected=AssertionError.class)
	public void testQueueEventWithUnregisteredHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		JEEventScheduler scheduler = new JEEventScheduler(JECheckpointer.INTERVAL);
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.NEWREQUEST, handler, 1000));
		scheduler.start();
		EasyMock.verify(handler);
	}

	@Ignore @Test
	public void testQueueEventWithRegisteredHandler() {
		
		final JEEventScheduler scheduler = new JEEventScheduler(JECheckpointer.INTERVAL);
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return scheduler.registerHandler(handler);
			}
		}).once();
//		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.start();
		
		EasyMock.verify(handler);
	}
	
	@Ignore @Test(expected=AssertionError.class)
	public void testQueueEventWithNegativeTime() {
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		final JEEventScheduler scheduler = new JEEventScheduler(JECheckpointer.INTERVAL);
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
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, -1));
	}
	
	@Ignore @Test
	public void testQueueEventAfterEndTime() {
		final JEEventScheduler scheduler = new JEEventScheduler(3600);
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
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
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3600));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3601));
	}

	@Test
	public void testCancelEvent() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		final JEEventScheduler scheduler = new JEEventScheduler(JECheckpointer.INTERVAL);
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
		assertEquals(JECheckpointer.INTERVAL, scheduler.now());
		
		EasyMock.verify(handler);
	}

	@Test
	public void testNowWithFiniteSimulator() {
		JEEventScheduler scheduler = new JEEventScheduler(3600000L);
		assertEquals(0L, scheduler.now());
		scheduler.start();
		assertEquals(3600000, scheduler.now());
	}
	
	@Ignore @Test(expected=JEException.class)
	public void testQueueEventWithPastEvent() {
		
		final JEEventScheduler scheduler = new JEEventScheduler(3600000L);
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
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
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1));
	}
	
	@Ignore@Test
	public void testExecutingEventsAndCheckingCurrentTime() {
		
		final JEEventScheduler scheduler = new JEEventScheduler(3600000L);
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
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
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		scheduler.queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 55000));

		scheduler.start();
		assertEquals(55000, scheduler.now());
		
		EasyMock.verify(handler);
	}
	
	

}