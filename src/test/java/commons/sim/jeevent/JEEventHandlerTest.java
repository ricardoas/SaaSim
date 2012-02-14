package commons.sim.jeevent;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import util.CleanConfigurationTest;


public class JEEventHandlerTest extends CleanConfigurationTest {
	
	private JEEventScheduler scheduler;

	@Override
	public void setUp() throws Exception{
		super.setUp();
		scheduler = new JEEventScheduler(JECheckpointer.INTERVAL);
	}

	@Test
	public void testJEEventHandler(){
		JEEventHandler handler = new JEAbstractEventHandler(scheduler){
			/**
			 * 
			 */
			private static final long serialVersionUID = 8253763434426330037L;
		};
		assertEquals(handler, scheduler.getHandler(handler.getHandlerId()));
	}

}
