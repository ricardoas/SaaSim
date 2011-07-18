package commons.sim.jeevent;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

public class JEEventTest {

	@Test
	public void testCompareToWithDifferentTimes(){
		JEEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		JEEvent eventA = new JEEvent(JEEventType.READWORKLOAD, handler, new JETime(1000));
		JEEvent eventB = new JEEvent(JEEventType.READWORKLOAD, handler, new JETime(2000));
		assertEquals(-1, eventA.compareTo(eventB));
		assertEquals(1, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	@Test
	public void testCompareToWithSameTimeDifferentType(){
		JEEventHandler handler = EasyMock.createStrictMock(JEAbstractEventHandler.class);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		EasyMock.replay(handler);
		JEEvent eventA = new JEEvent(JEEventType.READWORKLOAD, handler, new JETime(1000));
		JEEvent eventB = new JEEvent(JEEventType.NEWREQUEST, handler, new JETime(1000));
		assertEquals(-3, eventA.compareTo(eventB));
		assertEquals(3, eventB.compareTo(eventA));
		EasyMock.verify(handler);
	}
	
	

}
