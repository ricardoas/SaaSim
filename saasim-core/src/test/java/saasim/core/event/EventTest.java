package saasim.core.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link Event}
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class EventTest {
	

	@Test
	public void testCompareToWithDifferentTimes(){
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
	}
	
	@Test
	public void testCompareToWithSameTimeDifferentPriority(){
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
	}
	
	@Test
	public void testCompareToWithSameTimeAndPriority(){
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
	}
}
