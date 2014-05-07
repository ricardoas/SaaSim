package saasim.core.event;

import static org.junit.Assert.assertEquals;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import saasim.core.config.Configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

public class EventSchedulerTest{
	
	
	private static class SuperHandler{
		public int field;
		public void doSomething(){
			field = 0;
		}
	}
	
	private static  class ChildHandlerWithNoOverwriting extends SuperHandler{
	}
	
	private static  class ChildHandlerWithOverwriting extends SuperHandler{
		@Override
		public void doSomething() {
			field = 54;
		}
	}

	
	private Configuration configurationMock;
	
	@Inject private EventScheduler scheduler;
	
	@Before
	public void setUp() throws Exception {
		configurationMock = EasyMock.createStrictMock(Configuration.class);

		EasyMock.expect(configurationMock.getLong("random.seed")).andReturn(0L).once();
		EasyMock.replay(configurationMock);
		
		Guice.createInjector(new AbstractModule() {
		      @Override 
		      protected void configure() {
		          bind(Configuration.class).toInstance(configurationMock);
		        }
		      }).injectMembers(this);
		
	}
	
	@After
	public void tearDown() {
		EasyMock.verify(configurationMock);
		scheduler = null;
	}
	
	
	@Test
	public void testQueue() throws ConfigurationException{
		
		final SuperHandler handler = new SuperHandler();
		scheduler.queueEvent(new Event(1) {
			@Override
			public void trigger() {
				handler.doSomething();
			}
		});
		scheduler.start(1);
		assertEquals(0, handler.field);
	}

	@Test
	public void testQueueWithOverwritingChildHandler() throws ConfigurationException{
		
		final SuperHandler handler = new ChildHandlerWithOverwriting();
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
	public void testQueueWithChildHandler() throws ConfigurationException{

		final SuperHandler handler = new ChildHandlerWithNoOverwriting();
		scheduler.queueEvent(new Event(1) {
			@Override
			public void trigger() {
				handler.doSomething();
			}
		});
		scheduler.start(1);
		assertEquals(0, handler.field);
	}
	
	@Test(expected=NullPointerException.class)
	@SuppressWarnings("null")
	public void testQueuePastTimeEvent() {

		final SuperHandler handler = null;
		scheduler.queueEvent(new Event(1) {
			@Override
			public void trigger() {
				handler.doSomething();
			}
		});
		scheduler.start(1);
	}
	
}
