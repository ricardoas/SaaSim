package commons.io;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

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
public class GEISTMultiFileWorkloadParserTest {

	@Test(expected=NoSuchElementException.class)
	public void testParseRequestWithEmptyLine() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
		parser.parseRequest("");
		
		PowerMock.verifyAll();
	}
	
	@Test(expected=NullPointerException.class)
	public void testParseRequestWithNullLine() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
		parser.parseRequest(null);
		
		PowerMock.verifyAll();
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testParseRequestWithInvalidLine() {
		int saasclientID = 1;
		String workload = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workload, saasclientID);
		
		parser.parseRequest("10 10 8 7");
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testParseRequestWithValidLine() {
		int saasclientID = 1;
		String workloads = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	/**
	 * Not yet implemented!
	 */
	@Test(expected=RuntimeException.class)
	public void testApplyError(){
		
		int saasclientID = 1;
		String workloads = PropertiesTesting.VALID_WORKLOAD_3;
		
		SimulationInfo simInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		PowerMock.replayAll(config);
		
		GEISTMultiFileWorkloadParser parser = new GEISTMultiFileWorkloadParser(workloads, saasclientID);
		
		parser.applyError(0.5);
		
		PowerMock.verifyAll();
	}
}
