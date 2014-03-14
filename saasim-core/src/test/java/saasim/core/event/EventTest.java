package saasim.core.event;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

public class EventTest {
	

	@Test
	public void testCompareToWithDifferentTimes(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		Event eventA = new Event(handler, TestEvent.class, 1000);
		Event eventB = new Event(handler, TestEvent.class, 2000);
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeDifferentPriority(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		Event eventA = new Event(handler, TestEvent.class, 1000, EventPriority.VERY_HIGH);
		Event eventB = new Event(handler, TestEvent.class, 1000, EventPriority.VERY_LOW);
		assertEquals(-6, eventA.compareTo(eventB));
		assertEquals(6, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeAndPriority(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		Event eventA = new Event(handler, TestEvent.class, 1000, EventPriority.DEFAULT);
		Event eventB = new Event(handler, TestEvent.class, 1000, EventPriority.DEFAULT);
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
}
