package saasim.sim.core;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import saasim.sim.core.AbstractEventHandler;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.EventHandler;
import saasim.sim.core.EventScheduler;
import saasim.util.CleanConfigurationTest;


public class EventHandlerTest extends CleanConfigurationTest {
	
	private EventScheduler scheduler;

	@Override
	public void setUp() throws Exception{
		super.setUp();
		scheduler = new EventScheduler(EventCheckpointer.INTERVAL);
	}

	@Test
	public void testJEEventHandler(){
		EventHandler handler = new AbstractEventHandler(scheduler){
			/**
			 * 
			 */
			private static final long serialVersionUID = 8253763434426330037L;
		};
		assertEquals(handler, scheduler.getHandler(handler.getHandlerId()));
	}

}
