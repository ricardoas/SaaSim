package commons.io;

import static org.junit.Assert.*;

import org.junit.Test;

import commons.cloud.Request;

public class AbstractWorkloadParserTest {

	@Test(expected=RuntimeException.class)
	public void testAbstractWorkloadParserConstructorWithoutWorkloads() {
		int saasclientID = 1;
		String[] workloads = new String[]{};
		
		new GEISTMultiFileWorkloadParser(workloads, saasclientID);
	}
	
	@Test
	public void testAbstractWorkloadParserConstructorWithValidWorkloads() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload"};
		
		new GEISTMultiFileWorkloadParser(workloads, saasclientID);
	}

	@Test(expected=RuntimeException.class)
	public void testSetDaysAlreadyRead() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		parser.setDaysAlreadyRead(10);
	}

	@Test(expected=RuntimeException.class)
	public void testClear() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		parser.clear();
	}

	@Test
	public void testNext() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
		Request request = parser.next();
		assertNotNull(request);
		assertEquals(1, request.getUserID());
		assertEquals(1, request.getReqID());
		assertEquals(160168, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(2, request.getUserID());
		assertEquals(2, request.getReqID());
		assertEquals(160302, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(1, request.getUserID());
		assertEquals(3, request.getReqID());
		assertEquals(160315, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(3, request.getUserID());
		assertEquals(4, request.getReqID());
		assertEquals(160915, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(4, request.getUserID());
		assertEquals(5, request.getReqID());
		assertEquals(161383, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNull(request);
	}

	@Test
	public void testHasNext() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
		assertTrue(parser.hasNext());
		Request request = parser.next();
		assertTrue(parser.hasNext());
		
		request = parser.next();
		assertTrue(parser.hasNext());
		
		request = parser.next();
		assertTrue(parser.hasNext());
		
		request = parser.next();
		assertTrue(parser.hasNext());
		
		request = parser.next();
		assertFalse(parser.hasNext());
		
		request = parser.next();
		assertNull(request);
	}

	@Test
	public void testChangeToPeak() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		parser.changeToPeak();
		
		Request request = parser.next();
		assertNotNull(request);
		assertEquals(100, request.getUserID());
		assertEquals(100, request.getReqID());
		assertEquals(160168, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(200, request.getUserID());
		assertEquals(200, request.getReqID());
		assertEquals(160302, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(100, request.getUserID());
		assertEquals(300, request.getReqID());
		assertEquals(160315, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(300, request.getUserID());
		assertEquals(400, request.getReqID());
		assertEquals(160915, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(400, request.getUserID());
		assertEquals(500, request.getReqID());
		assertEquals(161383, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNull(request);
	}

	@Test
	public void testChangeToTransition() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		parser.changeToTransition();
		
		Request request = parser.next();
		assertNotNull(request);
		assertEquals(10, request.getUserID());
		assertEquals(10, request.getReqID());
		assertEquals(160168, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(20, request.getUserID());
		assertEquals(20, request.getReqID());
		assertEquals(160302, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(10, request.getUserID());
		assertEquals(30, request.getReqID());
		assertEquals(160315, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(30, request.getUserID());
		assertEquals(40, request.getReqID());
		assertEquals(160915, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(40, request.getUserID());
		assertEquals(50, request.getReqID());
		assertEquals(161383, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNull(request);
	}

	@Test
	public void testChangeToNormal() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		parser.changeToNormal();
		
		Request request = parser.next();
		assertNotNull(request);
		assertEquals(1, request.getUserID());
		assertEquals(1, request.getReqID());
		assertEquals(160168, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(2, request.getUserID());
		assertEquals(2, request.getReqID());
		assertEquals(160302, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(1, request.getUserID());
		assertEquals(3, request.getReqID());
		assertEquals(160315, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(3, request.getUserID());
		assertEquals(4, request.getReqID());
		assertEquals(160915, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(4, request.getUserID());
		assertEquals(5, request.getReqID());
		assertEquals(161383, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
	}
	
	@Test
	public void testChangeWhileReading() {
		int saasclientID = 1;
		String[] workloads = new String[]{"src/test/resources/workload", "src/test/resources/workload/transition", "src/test/resources/workload/peak"};
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
		Request request = parser.next();
		assertNotNull(request);
		assertEquals(1, request.getUserID());
		assertEquals(1, request.getReqID());
		assertEquals(160168, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(2, request.getUserID());
		assertEquals(2, request.getReqID());
		assertEquals(160302, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(1, request.getUserID());
		assertEquals(3, request.getReqID());
		assertEquals(160315, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		parser.changeToPeak();
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(100, request.getUserID());
		assertEquals(100, request.getReqID());
		assertEquals(160168, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(200, request.getUserID());
		assertEquals(200, request.getReqID());
		assertEquals(160302, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(100, request.getUserID());
		assertEquals(300, request.getReqID());
		assertEquals(160315, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(300, request.getUserID());
		assertEquals(400, request.getReqID());
		assertEquals(160915, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNotNull(request);
		assertEquals(400, request.getUserID());
		assertEquals(500, request.getReqID());
		assertEquals(161383, request.getArrivalTimeInMillis());
		assertEquals(100, request.getRequestSizeInBytes());
		assertEquals(500000, request.getResponseSizeInBytes());
		assertEquals(10, request.getCpuDemandInMillis()[0]);
		assertEquals(9, request.getCpuDemandInMillis()[1]);
		assertEquals(8, request.getCpuDemandInMillis()[2]);
		assertEquals(7, request.getCpuDemandInMillis()[3]);
		
		request = parser.next();
		assertNull(request);
	}
}
