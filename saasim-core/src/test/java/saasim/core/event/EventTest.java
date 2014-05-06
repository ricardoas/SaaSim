package saasim.core.event;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

public class EventTest {
	

	@Test
	public void testCompareToWithDifferentTimes(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		OldEvent eventA = new OldEvent(handler, TestEvent.class, 1000);
		OldEvent eventB = new OldEvent(handler, TestEvent.class, 2000);
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeDifferentPriority(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		OldEvent eventA = new OldEvent(handler, TestEvent.class, 1000, EventPriority.VERY_HIGH);
		OldEvent eventB = new OldEvent(handler, TestEvent.class, 1000, EventPriority.VERY_LOW);
		assertEquals(-6, eventA.compareTo(eventB));
		assertEquals(6, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeAndPriority(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		OldEvent eventA = new OldEvent(handler, TestEvent.class, 1000, EventPriority.DEFAULT);
		OldEvent eventB = new OldEvent(handler, TestEvent.class, 1000, EventPriority.DEFAULT);
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
}
