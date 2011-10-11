package commons.io;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Request;
import commons.config.Configuration;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class})
public class TimeBasedWorkloadParserTest {
	
	@Test
	public void testConstructor(){
		GEISTMultiFileWorkloadParser parser = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser};
		
		EasyMock.replay(parser);
		
		new TimeBasedWorkloadParser(5000, parsers);
		
		EasyMock.verify(parser);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithoutParsers(){
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		new TimeBasedWorkloadParser(5000, parsers);
	}
	
	@Test(expected=RuntimeException.class)
	public void testClear() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		GEISTMultiFileWorkloadParser geistParser = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{geistParser};
		
		EasyMock.replay(geistParser);
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		parser.clear();
		
		EasyMock.verify(geistParser);
	}
	
	@Test(expected=RuntimeException.class)
	public void testSetDaysAlreadyRead(){
		GEISTMultiFileWorkloadParser geistParser = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{geistParser};
		
		EasyMock.replay(geistParser);
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		//Requesting changes
		parser.setDaysAlreadyRead(120);
		
		EasyMock.verify(geistParser);
	}
	
	@Test
	public void testHasNextWithOneParserHavingEvents(){
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		
		EasyMock.replay(parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		assertEquals(true, parser.hasNext());
		
		EasyMock.verify(parser1, parser2);
	}
	
	@Test
	public void testHasNextWithAnyParserHavingEvents(){
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		assertEquals(false, parser.hasNext());
		
		EasyMock.verify(parser1, parser2);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNextWithoutRequest(){
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(null);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		
		EasyMock.replay(parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		parser.next();
		
		EasyMock.verify(parser1, parser2);
	}
	
	@Test
	public void testNextWithRequestsAndLeftOverForNextTick(){
		int tick = 5000;
		
		//Requests
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getArrivalTimeInMillis()).andReturn(tick - 10l);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getArrivalTimeInMillis()).andReturn(tick + 1l).times(2);
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(thirdRequest.getArrivalTimeInMillis()).andReturn(tick + 1l).times(2);
		
		//Parsers
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(tick, parsers);
		List<Request> requests = parser.next();
		assertEquals(1, requests.size());
		assertEquals(firstRequest, requests.get(0));
		
		//Retrieving requests from leftOver
		requests = parser.next();
		assertEquals(2, requests.size());
		assertEquals(secondRequest, requests.get(0));
		assertEquals(thirdRequest, requests.get(1));
		
		EasyMock.verify(parser1, parser2, firstRequest, secondRequest, thirdRequest);
	}
	
	@Test
	public void testNextWithRequestsAndLeftOverForFutureTicks(){
		int tick = 5000;
		
		//Requests
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getArrivalTimeInMillis()).andReturn(tick - 10l);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getArrivalTimeInMillis()).andReturn(2l * tick).times(3);
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(thirdRequest.getArrivalTimeInMillis()).andReturn(2l * tick).times(3);
		
		//Parsers
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(tick, parsers);
		List<Request> requests = parser.next();
		assertEquals(1, requests.size());
		assertEquals(firstRequest, requests.get(0));
		
		//Any requests in leftOver
		requests = parser.next();
		assertEquals(0, requests.size());
		
		//Retrieving requests from leftOver
		requests = parser.next();
		assertEquals(2, requests.size());
		assertEquals(secondRequest, requests.get(0));
		assertEquals(thirdRequest, requests.get(1));
		
		EasyMock.verify(parser1, parser2, firstRequest, secondRequest, thirdRequest);
	}
	
	@Test
	public void testNextWithRequestsAndWithoutLeftOver(){
		int tick = 5000;
		
		//Requests
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getArrivalTimeInMillis()).andReturn(tick - 10l);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getArrivalTimeInMillis()).andReturn(tick - 1l);
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(thirdRequest.getArrivalTimeInMillis()).andReturn(tick - 1l);
		Request fourthRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(fourthRequest.getArrivalTimeInMillis()).andReturn(tick - 1l);
		
		//Parsers
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(fourthRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(tick, parsers);
		List<Request> requests = parser.next();
		assertEquals(4, requests.size());
		assertEquals(firstRequest, requests.get(0));
		assertEquals(secondRequest, requests.get(1));
		assertEquals(thirdRequest, requests.get(2));
		assertEquals(fourthRequest, requests.get(3));
		
		EasyMock.verify(parser1, parser2, firstRequest, secondRequest, thirdRequest);
	}
}
