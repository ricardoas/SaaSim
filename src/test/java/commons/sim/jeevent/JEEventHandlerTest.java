package commons.sim.jeevent;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class JEEventHandlerTest {
	
	private JEEventScheduler scheduler;

	@Before
	public void setUp(){
		scheduler = new JEEventScheduler();
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
