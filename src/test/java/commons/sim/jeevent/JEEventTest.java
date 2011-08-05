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
		assertEquals(-4, eventA.compareTo(eventB));
		assertEquals(4, eventB.compareTo(eventA));
		//FIXME this is failing because of an extra event in the hierarchy... should REQUEST_FINISHED continue to exist?
		EasyMock.verify(handler);
	}
	
	

}
