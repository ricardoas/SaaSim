package saasim.core.event;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

public class EventTest {
	

	@Test
	public void testCompareToWithDifferentTimes(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		Event eventA = new Event(1000){
			@Override
			public void trigger() {
			}};
		Event eventB = new Event(2000){
			@Override
			public void trigger() {
			}};
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeDifferentPriority(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		Event eventA = new Event(1000, EventPriority.VERY_HIGH){
			@Override
			public void trigger() {
			}};
		Event eventB = new Event(1000, EventPriority.VERY_LOW){
			@Override
			public void trigger() {
			}};
		assertEquals(-6, eventA.compareTo(eventB));
		assertEquals(6, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeAndPriority(){
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EasyMock.replay(handler);
		Event eventA = new Event(1000, EventPriority.DEFAULT){
			@Override
			public void trigger() {
			}};
		Event eventB = new Event(1000, EventPriority.DEFAULT){
			@Override
			public void trigger() {
			}};
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
}
