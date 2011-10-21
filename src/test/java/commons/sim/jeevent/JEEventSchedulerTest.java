package commons.sim.jeevent;

import static commons.sim.jeevent.JEEventScheduler.getInstance;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

import util.CleanConfigurationTest;

import commons.io.Checkpointer;

public class JEEventSchedulerTest extends CleanConfigurationTest {
	
	@Test
	public void testRegisterHandler() {
		JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.replay(handler);
		int id = getInstance().registerHandler(handler);
		assertEquals(handler, getInstance().getHandler(id));
		EasyMock.verify(handler);
	}

	@Test
	public void testStart() {
		assertEquals(0L, getInstance().now());
		getInstance().start();
		assertEquals(Checkpointer.INTERVAL, getInstance().now());
	}

	@Test(expected=AssertionError.class)
	public void testQueueEventWithUnregisteredHandler() {
		JEEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		getInstance().queueEvent(new JEEvent(JEEventType.NEWREQUEST, handler, 1000));
		getInstance().start();
		EasyMock.verify(handler);
	}

	@Test
	public void testQueueEventWithRegisteredHandler() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return getInstance().registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		getInstance().start();
		
		EasyMock.verify(handler);
	}
	
	@Test(expected=AssertionError.class)
	public void testQueueEventWithNegativeTime() {
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return getInstance().registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		assertEquals(0, getInstance().now());
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, -1));
	}
	
	@Test
	public void testQueueEventAfterEndTime() {
		getInstance().reset(3600L);
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return getInstance().registerHandler(handler);
			}
		}).times(3);
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(handler);
		
		//Queuing past event
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3600));
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 3601));
	}

	@Test
	public void testCancelEvent() {
		
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return getInstance().registerHandler(handler);
			}
		}).once();
		EasyMock.replay(handler);
		
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1000);
		getInstance().queueEvent(event);
		getInstance().cancelEvent(event);
		getInstance().start();
		assertEquals(Checkpointer.INTERVAL, getInstance().now());
		
		EasyMock.verify(handler);
	}

	@Test
	public void testNowWithFiniteSimulator() {
		getInstance().reset(3600000L);
		assertEquals(0L, getInstance().now());
		getInstance().start();
		assertEquals(Checkpointer.INTERVAL, getInstance().now());
	}
	
	@Test(expected=JEException.class)
	public void testQueueEventWithPastEvent() {
		
		getInstance().reset(3600000L);
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return getInstance().registerHandler(handler);
			}
		}).once();
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().once();
		EasyMock.replay(handler);
		
		getInstance().start();
		assertEquals(Checkpointer.INTERVAL, getInstance().now());
		
		//Queuing past event
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1));
	}
	
	@Test
	public void testExecutingEventsAndCheckingCurrentTime() {
		
		getInstance().reset(3600000L);
		final JEAbstractEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				return getInstance().registerHandler(handler);
			}
		}).times(2);
		handler.handleEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(handler);
		
		//Queuing past event
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 1000));
		getInstance().queueEvent(new JEEvent(JEEventType.READWORKLOAD, handler, 55000));

		getInstance().start();
		assertEquals(55000, getInstance().now());
		
		EasyMock.verify(handler);
	}
	
	

}
