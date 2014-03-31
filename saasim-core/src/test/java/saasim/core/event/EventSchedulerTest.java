package saasim.core.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import saasim.core.CleanConfigurationTest;
import saasim.core.TestConfigurationBuilder;
import saasim.core.util.FastSemaphore;

public class EventSchedulerTest extends CleanConfigurationTest{
	
	private EventScheduler scheduler;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		TestConfigurationBuilder.buildConfiguration01();
		scheduler = new EventScheduler(0);
	}
	
	@Override
	@After
	public void tearDown() {
		super.tearDown();
		scheduler = null;
	}
	
	@Test
	public void testClearAndRegisterAnnotationsWithEmptyParam() throws ConfigurationException{
		scheduler.setup(new String[]{}, new String[]{});
	}
	
	@Test(expected=AssertionError.class)
	public void testClearAndRegisterAnnotationsWithEmptyStringParam() throws ConfigurationException{
		scheduler.setup(new String[]{""}, new String[]{""});
	}
	
	@Test(expected=AssertionError.class)
	public void testClearAndRegisterAnnotationsWithNullParam() throws ConfigurationException{
		scheduler.setup(new String[]{null}, new String[]{null});
	}
	
	@Test
	public void testCleanAndRegisterOverwritingAndAnnotationChildHandlerClass() throws ConfigurationException{
		scheduler.setup(new String[] {"saasim.core.event.TestEvent"}, new String[] {"saasim.core.event.ChildWithOverwritingAndAnnotationHandler"});
		
		ChildWithOverwritingAndAnnotationHandler handler = new ChildWithOverwritingAndAnnotationHandler();
		scheduler.queueEvent(handler, TestEvent.class, 1);
		scheduler.start(1);
		assertEquals(54, handler.field);
	}

	@Test
	public void testCleanAndRegisterHandlerClassWithMisusedAnnotation() throws ConfigurationException{
		scheduler.setup(new String[] {"saasim.core.event.TestEvent"}, new String[] {"saasim.core.event.ChildWithMisusedAnnotationHandler"});

		ChildWithMisusedAnnotationHandler handler = new ChildWithMisusedAnnotationHandler();
		scheduler.queueEvent(handler, TestEvent.class, 1);
		scheduler.start(1);
		assertEquals(0, handler.field);
	}
	
	@Test(expected=RuntimeException.class)
	public void testQueuePastTimeEvent() {
		scheduler.queueEvent(null, TestEvent.class, -1, EventPriority.DEFAULT);
	}
	
	@Test
	public void testQueueStartAndQueueAgain() throws ConfigurationException {
		
		final FastSemaphore semaphore = new FastSemaphore(0);
		
		EventHandler handler = new EventHandler() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -6979714487193692831L;
			@TestEvent public void handlerTestEvent(){
				semaphore.release();
			}
			@AnotherTestEvent public void handlerAnotherTestEvent(){
				semaphore.release();
			}
			@OneMoreTestEvent public void handlerOneMoreTestEvent(){
				semaphore.release();
			}
		};
		
		scheduler.setup(new String[] {"saasim.core.event.TestEvent", "saasim.core.event.AnotherTestEvent", "saasim.core.event.OneMoreTestEvent"}, new String[] {handler.getClass().getName()});

		scheduler.queueEvent(handler, TestEvent.class, 99);
		scheduler.queueEvent(handler, AnotherTestEvent.class, 100);
		scheduler.queueEvent(handler, OneMoreTestEvent.class, 101);
		
		scheduler.start(100);
		
		assertTrue(semaphore.tryAcquire());
		assertTrue(semaphore.tryAcquire());
		assertFalse(semaphore.tryAcquire());
		
		assertEquals(100, scheduler.now());
		
		scheduler.queueEvent(handler, OneMoreTestEvent.class, 102);
		
		scheduler.start(105);
		
		assertTrue(semaphore.tryAcquire());
		assertTrue(semaphore.tryAcquire());
		assertFalse(semaphore.tryAcquire());
		
		assertEquals(105, scheduler.now());
	}
	
	
	
}
