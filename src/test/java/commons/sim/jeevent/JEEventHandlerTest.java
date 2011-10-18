package commons.sim.jeevent;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import util.CleanConfigurationTest;

public class JEEventHandlerTest extends CleanConfigurationTest {
	
	private JEEventScheduler scheduler;

	@Before
	public void setUp(){
		scheduler = JEEventScheduler.getInstance();
	}

	@Test
	public void testJEEventHandler(){
		JEEventHandler handler = new JEAbstractEventHandler(scheduler){
			@Override
			public void handleEvent(JEEvent event) {/* Empty implementation. */}
		};
		assertEquals(handler, scheduler.getHandler(handler.getHandlerId()));
	}

}
