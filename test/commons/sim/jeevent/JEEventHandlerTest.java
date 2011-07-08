package commons.sim.jeevent;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JEEventHandlerTest {

	@Test
	public void testJEEventHandler(){
		JEEventHandler handler = new JEEventHandler(){
			@Override
			public void handleEvent(JEEvent event) {}
		};
		assertEquals(handler, JEEventScheduler.SCHEDULER.getHandler(handler.getHandlerId()));
		
	}

}
