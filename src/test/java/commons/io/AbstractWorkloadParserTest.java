package commons.io;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.util.SimulationInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class AbstractWorkloadParserTest {

	@Test(expected=RuntimeException.class)
	public void testAbstractWorkloadParserConstructorWithoutWorkloads() {
		int saasclientID = 1;
		String workload = "";
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testAbstractWorkloadParserConstructorWithValidWorkloads() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
		PowerMock.verifyAll();
	}
	
	@Test(expected=RuntimeException.class)
	public void testAbstractWorkloadParserConstructorWithValidWorkloadsButInvalidDay() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(399, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
		PowerMock.verifyAll();
	}

	@Test(expected=RuntimeException.class)
	public void testSetDaysAlreadyRead() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		parser.setDaysAlreadyRead(10);
		
		PowerMock.verifyAll();
	}

	@Test(expected=RuntimeException.class)
	public void testClear() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		parser.clear();
		
		PowerMock.verifyAll();
	}

	@Test
	public void testNextForFirstDay() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
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
		
		PowerMock.verifyAll();
	}

	@Test
	public void testHasNextForFirstDay() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testNextForAPeakDay() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(2, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
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
		
		PowerMock.verifyAll();
	}

	@Test
	public void testHasNextForPeakDay() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(2, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testNextForATransitionDay() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(8, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
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
		
		PowerMock.verifyAll();
	}

	@Test
	public void testHasNextForATransitionDay() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(8, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test(expected=RuntimeException.class)
	public void testClose() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		AbstractWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
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
		
		parser.close();
		
		parser.next();
		
		PowerMock.verifyAll();
	}
}
