package saasim.sim.core;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import saasim.sim.core.AbstractEventHandler;
import saasim.sim.core.Event;
import saasim.sim.core.EventHandler;
import saasim.sim.core.EventType;
import saasim.util.CleanConfigurationTest;

public class EventTest extends CleanConfigurationTest {

	@Test
	public void testCompareToWithDifferentTimes(){
		EventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		Event eventA = new Event(EventType.READWORKLOAD, handler, 1000);
		Event eventB = new Event(EventType.READWORKLOAD, handler, 2000);
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeDifferentType(){
		EventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		Event eventA = new Event(EventType.READWORKLOAD, handler, 1000);
		Event eventB = new Event(EventType.NEWREQUEST, handler, 1000);
		assertEquals(-6, eventA.compareTo(eventB));
		assertEquals(6, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithDifferentTimeAndDifferentTypes(){
		EventHandler handler = EasyMock.createStrictMock(AbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		Event eventA = new Event(EventType.READWORKLOAD, handler, 1000);
		Event eventB = new Event(EventType.NEWREQUEST, handler, 2000);
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
}
