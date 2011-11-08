package commons.io;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.Request;

public class TimeBasedWorkloadParserTest extends ValidConfigurationTest{
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildTwoUsersConfiguration();
	}
		
	@Test
	public void testConstructor(){
		GEISTWorkloadParser parser = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser};
		
		EasyMock.replay(parser);
		
		new TimeBasedWorkloadParser(5000, parsers);
		
		EasyMock.verify(parser);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithoutParsers(){
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{};
		
		new TimeBasedWorkloadParser(5000, parsers);
	}
	
	@Test(expected=RuntimeException.class)
	public void testClear() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		GEISTWorkloadParser geistParser = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{geistParser};
		
		EasyMock.replay(geistParser);
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		parser.clear();
		
		EasyMock.verify(geistParser);
	}
	
	@Test(expected=RuntimeException.class)
	public void testSetDaysAlreadyRead(){
		GEISTWorkloadParser geistParser = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{geistParser};
		
		EasyMock.replay(geistParser);
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		//Requesting changes
		parser.setDaysAlreadyRead(120);
		
		EasyMock.verify(geistParser);
	}
	
	@Test
	public void testHasNextWithOneParserHavingEvents(){
		GEISTWorkloadParser parser1 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		GEISTWorkloadParser parser2 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		
		EasyMock.replay(parser1, parser2);
		
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		assertEquals(true, parser.hasNext());
		
		EasyMock.verify(parser1, parser2);
	}
	
	@Test
	public void testHasNextWithAnyParserHavingEvents(){
		GEISTWorkloadParser parser1 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		GEISTWorkloadParser parser2 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2);
		
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(5000, parsers);
		
		assertEquals(false, parser.hasNext());
		
		EasyMock.verify(parser1, parser2);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNextWithoutRequest(){
		GEISTWorkloadParser parser1 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(null);
		GEISTWorkloadParser parser2 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		
		EasyMock.replay(parser1, parser2);
		
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser1, parser2};
		
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
		GEISTWorkloadParser parser1 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTWorkloadParser parser2 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser1, parser2};
		
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
		GEISTWorkloadParser parser1 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTWorkloadParser parser2 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser1, parser2};
		
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
		GEISTWorkloadParser parser1 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTWorkloadParser parser2 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(fourthRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		EasyMock.replay(parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(tick, parsers);
		List<Request> requests = parser.next();
		assertEquals(4, requests.size());
		assertEquals(firstRequest, requests.get(0));
		assertEquals(secondRequest, requests.get(1));
		assertEquals(thirdRequest, requests.get(2));
		assertEquals(fourthRequest, requests.get(3));
		
		EasyMock.verify(parser1, parser2, firstRequest, secondRequest, thirdRequest);
	}
	
	@Test
	public void testClose(){
		int tick = 5000;
		
		//Parsers
		GEISTWorkloadParser parser1 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		parser1.close();
		EasyMock.expectLastCall();
		
		GEISTWorkloadParser parser2 = EasyMock.createStrictMock(GEISTWorkloadParser.class);
		parser2.close();
		EasyMock.expectLastCall();
		
		EasyMock.replay(parser1, parser2);
		
		GEISTWorkloadParser[] parsers = new GEISTWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(tick, parsers);
		parser.close();
		
		EasyMock.verify(parser1, parser2);
	}
	
	/**
	 * Trace order:
	 * 
	 * 1st
	 * USER_1 0 11 0 1 50000 200 200 200 200 200
	 * USER_1 0 12 50000 1 50000 200 200 200 200 200
	 * USER_2 0 21 40000 1 50000 200 200 200 200 200
	 * 
	 * 2nd
	 * USER_1 0 13 60000 1 50000 200 200 200 200 200
	 * 
	 * 3rd
	 * USER_1 0 14 120000 1 50000 200 200 200 200 200
	 * USER_2 0 22 150000 1 50000 200 200 200 200 200
	 * 
	 * 5th
	 * 
	 * 6th
	 * USER_1 0 15 240000 1 50000 200 200 200 200 200
	 * USER_2 0 23 250000 1 50000 200 200 200 200 200
	 * 
	 * 7th
	 * USER_1 0 16 359999 1 50000 200 200 200 200 200
	 * 
	 * 
	 */
	@Test
	public void testParsingOrder(){
		WorkloadParser<List<Request>> parser = WorkloadParserFactory.getWorkloadParser();
		assertTrue(parser.hasNext());
		
		List<Request> requests = parser.next();
		assertNotNull(requests);
		assertEquals(11, requests.get(0).getReqID());
		assertEquals(12, requests.get(1).getReqID());
		assertEquals(21, requests.get(2).getReqID());
		
		requests = parser.next();
		assertNotNull(requests);
		assertEquals(13, requests.get(0).getReqID());
		
		requests = parser.next();
		assertNotNull(requests);
		assertEquals(14, requests.get(0).getReqID());
		assertEquals(22, requests.get(1).getReqID());
		
		requests = parser.next();
		assertNotNull(requests);
		assertTrue(requests.isEmpty());
		
		requests = parser.next();
		assertNotNull(requests);
		assertEquals(15, requests.get(0).getReqID());
		assertEquals(23, requests.get(1).getReqID());
		
		requests = parser.next();
		assertNotNull(requests);
		assertEquals(16, requests.get(0).getReqID());
	}
}
