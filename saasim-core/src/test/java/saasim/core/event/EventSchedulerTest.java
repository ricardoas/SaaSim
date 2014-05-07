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
	public void testCleanAndRegisterOverwritingAndAnnotationChildHandlerClass() throws ConfigurationException{
		final ChildWithOverwritingAndAnnotationHandler handler = new ChildWithOverwritingAndAnnotationHandler();
		scheduler.queueEvent(new Event(1) {
			@Override
			public void trigger() {
				handler.doSomething();
			}
		});
		scheduler.start(1);
		assertEquals(54, handler.field);
	}

	@Test
	public void testCleanAndRegisterHandlerClassWithMisusedAnnotation() throws ConfigurationException{
		final ChildWithMisusedAnnotationHandler handler = new ChildWithMisusedAnnotationHandler();
		scheduler.queueEvent(new Event(1) {
			@Override
			public void trigger() {
				handler.doSomething();
			}
		});
		scheduler.start(1);
		assertEquals(0, handler.field);
	}
	
	@Test(expected=RuntimeException.class)
	public void testQueuePastTimeEvent() {
		final ChildWithOverwritingAndAnnotationHandler handler = null;
		scheduler.queueEvent(new Event(1) {
			@Override
			public void trigger() {
				handler.doSomething();
			}
		});
	}
	
}
