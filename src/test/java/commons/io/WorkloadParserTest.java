/**
 * 
 */
package commons.io;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.Request;
import commons.config.PropertiesTesting;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class WorkloadParserTest extends ValidConfigurationTest{
	
	private class TestParser extends AbstractWorkloadParser{

		private int reqID = 0;

		/**
		 * Default constructor.
		 * @param workload
		 * @param saasclientID
		 */
		public TestParser(String workload, int saasclientID) {
			super(workload, saasclientID);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void applyError(double error) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Request parseRequest(String line) {
			return  new Request(reqID++, 0, 0, 0, 100, 100, new long[]{100, 100, 100});
		}
	}
	
	@Test(expected=AssertionError.class)
	public void testConstructorWithNullFile() throws ConfigurationException{
		buildFullConfiguration();
		new TestParser(null, 0);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithEmptyFile() throws ConfigurationException{
		buildFullConfiguration();
		new TestParser(PropertiesTesting.EMPTY_FILE, 0);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithNonExistendTraceFile() throws ConfigurationException{
		buildFullConfiguration();
		new TestParser(PropertiesTesting.INVALID_WORKLOAD, 0);
	}
	
//	@Test
//	public void testGetWorkloadParserAndFirstFile() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
//		SimulationInfo simInfo = new SimulationInfo(0, 0);
//		
//		Configuration config = EasyMock.createStrictMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(12);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
//		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3,
//				PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3,
//				PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3, 
//				PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3});
//		EasyMock.expect(config.getParserIdiom()).andReturn(ParserIdiom.GEIST);
//		EasyMock.expect(config.getInt(SaaSUsersProperties.SAAS_NUMBER_OF_USERS)).andReturn(10);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo).times(10);
//		
//		PowerMock.replayAll(config);
//		
//		WorkloadParser<List<Request>> parser = WorkloadParserFactory.getWorkloadParser();
//		
//		assertNotNull(parser);
//		assertTrue(parser.hasNext());
//		
//		Field field = TimeBasedWorkloadParser.class.getDeclaredField("parsers");
//		field.setAccessible(true);
//		WorkloadParser<Request>[] parsers = (WorkloadParser<Request>[])field.get(parser);
//		assertEquals(10, parsers.length);
//		assertEquals(GEISTMultiFileWorkloadParser.class, parsers[0].getClass());
//		
//		field = TimeBasedWorkloadParser.class.getDeclaredField("tick");
//		field.setAccessible(true);
//		long tick = (Long) field.get(parser); 
//		assertEquals(TickSize.MINUTE.getTickInMillis(), tick);	
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testGetWorkloadParserAndOtherFile() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
//		SimulationInfo simInfo = new SimulationInfo(6, 0);
//		
//		Configuration config = EasyMock.createStrictMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(12);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
//		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3,
//				PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3,
//				PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3, 
//				PropertiesTesting.VALID_WORKLOAD_3, PropertiesTesting.VALID_WORKLOAD_3});
//		EasyMock.expect(config.getParserIdiom()).andReturn(ParserIdiom.GEIST);
//		EasyMock.expect(config.getInt(SaaSUsersProperties.SAAS_NUMBER_OF_USERS)).andReturn(10);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo).times(10);
//		
//		PowerMock.replayAll(config);
//		
//		WorkloadParser<List<Request>> parser = WorkloadParserFactory.getWorkloadParser();
//		
//		assertNotNull(parser);
//		assertTrue(parser.hasNext());
//		
//		Field field = TimeBasedWorkloadParser.class.getDeclaredField("parsers");
//		field.setAccessible(true);
//		WorkloadParser<Request>[] parsers = (WorkloadParser<Request>[])field.get(parser);
//		assertEquals(10, parsers.length);
//		assertEquals(GEISTMultiFileWorkloadParser.class, parsers[0].getClass());
//		
//		field = TimeBasedWorkloadParser.class.getDeclaredField("tick");
//		field.setAccessible(true);
//		long tick = (Long) field.get(parser); 
//		assertEquals(TickSize.MINUTE.getTickInMillis(), tick);	
//		
//		PowerMock.verifyAll();
//	}

	
	/**
	 * Test method for {@link commons.io.WorkloadParser#next()}.
	 */
	@Test
	public void testNext() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.io.WorkloadParser#hasNext()}.
	 */
	@Test
	public void testHasNext() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.io.WorkloadParser#clear()}.
	 */
	@Test
	public void testClear() {
		fail("Not yet implemented");
	}
	
	

	/**
	 * Test method for {@link commons.io.WorkloadParser#setDaysAlreadyRead(int)}.
	 */
	@Test
	public void testSetDaysAlreadyRead() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.io.WorkloadParser#applyError(double)}.
	 */
	@Test
	public void testApplyError() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.io.WorkloadParser#close()}.
	 */
	@Test
	public void testClose() {
		fail("Not yet implemented");
	}
}
