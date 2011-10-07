package commons.io;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.Test;

import commons.cloud.Request;

public class GEISTMultiFileWorkloadParserTest {

	@Test(expected=NoSuchElementException.class)
	public void testParseRequestWithEmptyLine() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
		parser.parseRequest("");
	}
	
	@Test(expected=NullPointerException.class)
	public void testParseRequestWithNullLine() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
		parser.parseRequest(null);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testParseRequestWithInvalidLine() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
		parser.parseRequest("10 10 8 7");
	}
	
	@Test
	public void testParseRequestWithValidLine() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
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
}
