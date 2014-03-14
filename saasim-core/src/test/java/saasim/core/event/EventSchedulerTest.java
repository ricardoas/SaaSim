package saasim.core.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import saasim.core.CleanConfigurationTest;
import saasim.core.TestConfigurationBuilder;
import saasim.core.util.FastSemaphore;

@SuppressWarnings("unchecked")
public class EventSchedulerTest extends CleanConfigurationTest{
	
	private static class EmptyHandler implements EventHandler{

		/**
		 * 
		 */
		private static final long serialVersionUID = -2729205672392391192L;
		
	}
	
	private static class SuperTypeHandler implements EventHandler{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 2100501678674392966L;
		
		public int field;

		@TestEvent
		public void doSomething(){
			field = 0;
		}
	}
	
	private static class EmptyChildHandler extends SuperTypeHandler{
		

		/**
		 * 
		 */
		private static final long serialVersionUID = -1281910865959683602L;
		
	}

	private static class ChildWithOverwritingHandler extends SuperTypeHandler{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 7798349470462370700L;

		@Override
		public void doSomething() {
			field = 42;
		}

	}
	
	private static class ChildWithOverwritingAndAnnotationHandler extends SuperTypeHandler{
		

		/**
		 * 
		 */
		private static final long serialVersionUID = -7611094481437751352L;

		@Override
		@TestEvent
		public void doSomething() {
			field = 54;
		}
	}

	private static class ChildWithMisusedAnnotationHandler extends SuperTypeHandler{
		

		/**
		 * 
		 */
		private static final long serialVersionUID = -4400016827894319294L;

		@TestEvent
		public void doSomethingElse() {
			field = 0;
		}

	}

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
	
	@Test(expected=AssertionError.class)
	public void testClearAndRegisterAnnotationsWithEmptyParam(){
		scheduler.clearAndRegisterAnnotations();
	}
	
	@Test(expected=AssertionError.class)
	public void testClearAndRegisterAnnotationsWithNullParam(){
		Class<? extends Annotation> annotation = null;
		scheduler.clearAndRegisterAnnotations(annotation);
	}
	
	@Test(expected=AssertionError.class)
	public void testClearAndRegisterAnnotationsWithNullParams(){
		scheduler.clearAndRegisterAnnotations(null, null);
	}
	
	@Test(expected=AssertionError.class)
	public void testCleanAndRegisterHandlerClassesWithEmptyParam(){
		scheduler.clearAndRegisterHandlerClasses();
	}
	
	@Test(expected=AssertionError.class)
	public void testCleanAndRegisterHandlerClassesWithNullParam(){
		Class<? extends EventHandler> handlerClazz = null;
		scheduler.clearAndRegisterHandlerClasses(handlerClazz);
	}
	
	@Test(expected=AssertionError.class)
	public void testCleanAndRegisterHandlerClassesWithNullParams(){
		scheduler.clearAndRegisterHandlerClasses(null, null);
	}
	
	@Test(expected=AssertionError.class)
	public void testCleanAndRegisterEmptyHandlerClass(){
		scheduler.clearAndRegisterHandlerClasses(EmptyHandler.class);
	}
	
	@Test
	public void testCleanAndRegisterEmptyChildHandlerClass(){
		scheduler.clearAndRegisterAnnotations(TestEvent.class);
		scheduler.clearAndRegisterHandlerClasses(EmptyChildHandler.class);
	}
	
	@Test
	public void testCleanAndRegisterOverwritingChildHandlerClass(){
		scheduler.clearAndRegisterAnnotations(TestEvent.class);
		scheduler.clearAndRegisterHandlerClasses(ChildWithOverwritingHandler.class);
		
		ChildWithOverwritingHandler handler = new ChildWithOverwritingHandler();
		scheduler.queueEvent(handler, TestEvent.class, 1);
		scheduler.start(1);
		assertEquals(42, handler.field);
	}
	
	@Test
	public void testCleanAndRegisterOverwritingAndAnnotationChildHandlerClass(){
		scheduler.clearAndRegisterAnnotations(TestEvent.class);
		scheduler.clearAndRegisterHandlerClasses(ChildWithOverwritingAndAnnotationHandler.class);
		
		ChildWithOverwritingAndAnnotationHandler handler = new ChildWithOverwritingAndAnnotationHandler();
		scheduler.queueEvent(handler, TestEvent.class, 1);
		scheduler.start(1);
		assertEquals(54, handler.field);
	}

	@Test
	public void testCleanAndRegisterHandlerClassWithMisusedAnnotation(){
		scheduler.clearAndRegisterAnnotations(TestEvent.class);
		scheduler.clearAndRegisterHandlerClasses(ChildWithMisusedAnnotationHandler.class);

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
	public void testQueueStartAndQueueAgain() {
		scheduler.clearAndRegisterAnnotations(TestEvent.class, AnotherTestEvent.class, OneMoreTestEvent.class);
		
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
		
		scheduler.clearAndRegisterHandlerClasses(handler.getClass());
		
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
