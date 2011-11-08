package commons.io;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.Request;
import commons.config.PropertiesTesting;

public class GEISTWorkloadParserTest extends ValidConfigurationTest{
	
	@Test
	public void testTraceWithBlankLine() throws ConfigurationException{
		buildFullConfiguration();
		WorkloadParser<Request> parser = new GEISTWorkloadParser(PropertiesTesting.TRACE_WITH_BLANK_LINE);
		assertTrue(parser.hasNext());
		assertEquals(1, parser.next().getReqID());
		assertTrue(parser.hasNext());
		try{
			parser.next();
			fail("Should fail reading line.");
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Test
	public void testTraceWithBlankEndLine() throws ConfigurationException{
		buildFullConfiguration();
		WorkloadParser<Request> parser = new GEISTWorkloadParser(PropertiesTesting.TRACE_WITH_BLANK_END_LINE);
		assertTrue(parser.hasNext());
		Request request1 = parser.next();
		assertEquals(1, request1.getReqID());
		assertEquals(0, request1.getSaasClient());
		assertTrue(parser.hasNext());
		assertEquals(2, parser.next().getReqID());
		assertTrue(parser.hasNext());
		assertEquals(3, parser.next().getReqID());
		assertTrue(parser.hasNext());
		assertEquals(4, parser.next().getReqID());
		assertTrue(parser.hasNext());
		assertEquals(5, parser.next().getReqID());
		assertFalse(parser.hasNext());
		assertNull(parser.next());
	}
	
	@Test
	public void testValidTrace() throws ConfigurationException{
		buildFullConfiguration();
		WorkloadParser<Request> parser = new GEISTWorkloadParser(PropertiesTesting.WORKLOAD);
		assertTrue(parser.hasNext());
		Request request1 = parser.next();
		assertEquals(1, request1.getReqID());
		assertEquals(0, request1.getSaasClient());
		assertTrue(parser.hasNext());
		assertEquals(2, parser.next().getReqID());
		assertTrue(parser.hasNext());
		assertEquals(3, parser.next().getReqID());
		assertTrue(parser.hasNext());
		assertEquals(4, parser.next().getReqID());
		assertTrue(parser.hasNext());
		assertEquals(5, parser.next().getReqID());
		assertFalse(parser.hasNext());
		assertNull(parser.next());
	}
	


	@Test(expected=NoSuchElementException.class)
	public void testParseRequestWithEmptyLine() throws ConfigurationException {
		buildFullConfiguration();
		String workload = PropertiesTesting.WORKLOAD;
		
		GEISTWorkloadParser parser = new GEISTWorkloadParser(workload);
		
		parser.parseRequest("");
	}
	
	@Test(expected=NullPointerException.class)
	public void testParseRequestWithNullLine() throws ConfigurationException {
		buildFullConfiguration();
		String workload = PropertiesTesting.WORKLOAD;
		
		GEISTWorkloadParser parser = new GEISTWorkloadParser(workload);
		
		parser.parseRequest(null);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testParseRequestWithInvalidLine() throws ConfigurationException {
		buildFullConfiguration();
		String workload = PropertiesTesting.WORKLOAD;
		
		GEISTWorkloadParser parser = new GEISTWorkloadParser(workload);
		
		parser.parseRequest("10 10 8 7");
	}
	
	@Test
	public void testParseRequestWithValidLine() throws ConfigurationException {
		buildFullConfiguration();
		String workloads = PropertiesTesting.WORKLOAD;
		
		GEISTWorkloadParser parser = new GEISTWorkloadParser(workloads);
		
		Request request = parser.parseRequest("10 333 16031500 81 400000 10 5 8 1 2");
		assertNotNull(request);
		assertEquals(10, request.getUserID());
		assertEquals(333, request.getReqID());
		assertEquals(16031500, request.getArrivalTimeInMillis());
		assertEquals(81, request.getRequestSizeInBytes());
		assertEquals(400000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(5, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(1, request.getCpuDemandInMillis()[3]);
		assertEquals(2, request.getCpuDemandInMillis()[4]);
		
		request = parser.parseRequest("1 999999 100 240 1000000 10 2");
		assertNotNull(request);
		assertEquals(1, request.getUserID());
		assertEquals(999999, request.getReqID());
		assertEquals(100, request.getArrivalTimeInMillis());
		assertEquals(240, request.getRequestSizeInBytes());
		assertEquals(1000000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(2, request.getCpuDemandInMillis()[1]);
	}
	
	@Test
	public void testMultipleDaysWithValidWorkload() throws ConfigurationException{
		// First Day
		buildFullConfiguration();
		GEISTWorkloadParser parser = new GEISTWorkloadParser(PropertiesTesting.WORKLOAD);
		
		assertEquals(160168, parser.next().getArrivalTimeInMillis());
		assertEquals(160302, parser.next().getArrivalTimeInMillis());
		assertEquals(160315, parser.next().getArrivalTimeInMillis());
		assertEquals(160915, parser.next().getArrivalTimeInMillis());
		assertEquals(161383, parser.next().getArrivalTimeInMillis());
		
		Checkpointer.save();
		
		// Second Day
		buildFullConfiguration();
		parser = new GEISTWorkloadParser(PropertiesTesting.WORKLOAD);

		long dayInMillis = 86400000;
		assertEquals(dayInMillis + 160168, parser.next().getArrivalTimeInMillis());
		assertEquals(dayInMillis + 160302, parser.next().getArrivalTimeInMillis());
		assertEquals(dayInMillis + 160315, parser.next().getArrivalTimeInMillis());
		assertEquals(dayInMillis + 160915, parser.next().getArrivalTimeInMillis());
		assertEquals(dayInMillis + 161383, parser.next().getArrivalTimeInMillis());
		Checkpointer.save();
		
		// Third Day
		buildFullConfiguration();
		parser = new GEISTWorkloadParser(PropertiesTesting.WORKLOAD);

		assertEquals(2*dayInMillis + 160168, parser.next().getArrivalTimeInMillis());
		assertEquals(2*dayInMillis + 160302, parser.next().getArrivalTimeInMillis());
		assertEquals(2*dayInMillis + 160315, parser.next().getArrivalTimeInMillis());
		assertEquals(2*dayInMillis + 160915, parser.next().getArrivalTimeInMillis());
		assertEquals(2*dayInMillis + 161383, parser.next().getArrivalTimeInMillis());
		Checkpointer.save();
		
		// Fourth Day
		buildFullConfiguration();
		parser = new GEISTWorkloadParser(PropertiesTesting.WORKLOAD);

		assertEquals(3*dayInMillis + 160168, parser.next().getArrivalTimeInMillis());
		assertEquals(3*dayInMillis + 160302, parser.next().getArrivalTimeInMillis());
		assertEquals(3*dayInMillis + 160315, parser.next().getArrivalTimeInMillis());
		assertEquals(3*dayInMillis + 160915, parser.next().getArrivalTimeInMillis());
		assertEquals(3*dayInMillis + 161383, parser.next().getArrivalTimeInMillis());
		Checkpointer.save();
		
		// Fifth Day
		buildFullConfiguration();

		parser = new GEISTWorkloadParser(PropertiesTesting.WORKLOAD);
		assertEquals(4*dayInMillis + 160168, parser.next().getArrivalTimeInMillis());
		assertEquals(4*dayInMillis + 160302, parser.next().getArrivalTimeInMillis());
		assertEquals(4*dayInMillis + 160315, parser.next().getArrivalTimeInMillis());
		assertEquals(4*dayInMillis + 160915, parser.next().getArrivalTimeInMillis());
		assertEquals(4*dayInMillis + 161383, parser.next().getArrivalTimeInMillis());
		
		assertTrue(Checkpointer.loadSimulationInfo().isFinishDay());
	}
	
}
