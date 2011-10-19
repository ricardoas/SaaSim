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
	
	private static final Request[] requests = new Request[]{
		new Request(0, 0, 0, 0, 100, 100, new long[]{100, 100, 100}),
		new Request(1, 0, 0, 0, 100, 100, new long[]{100, 100, 100}),
		new Request(2, 0, 0, 0, 100, 100, new long[]{100, 100, 100}),
		new Request(3, 0, 0, 0, 100, 100, new long[]{100, 100, 100}),
		new Request(4, 0, 0, 0, 100, 100, new long[]{100, 100, 100}),
		null
	};
	
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
			return requests[reqID++];
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
	
	@Test
	public void testHasNext() throws ConfigurationException{
		buildFullConfiguration();
		TestParser parser = new TestParser(PropertiesTesting.VALID_WORKLOAD_3, 0);
		assertTrue(parser.hasNext());
		assertNotNull(parser.next());
		assertNotNull(parser.next());
		assertNotNull(parser.next());
		assertNotNull(parser.next());
		assertNotNull(parser.next());
		assertFalse(parser.hasNext());
		assertNull(parser.next());
	}
	

	/**
	 * Test method for {@link commons.io.WorkloadParser#close()}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testClose() throws ConfigurationException {
		buildFullConfiguration();
		new TestParser(PropertiesTesting.VALID_WORKLOAD_3, 0).close();
	}
}
