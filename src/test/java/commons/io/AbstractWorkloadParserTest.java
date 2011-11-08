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
import commons.util.SimulationInfo;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class AbstractWorkloadParserTest extends ValidConfigurationTest{
	
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
			super(workload, 0);
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
	public void testEmptyWorkload() throws ConfigurationException{
		buildFullConfiguration();
		new TestParser(PropertiesTesting.EMPTY_WORKLOAD, 0);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithWorkloadWithMissingTrace() throws ConfigurationException{
		buildFullConfiguration();
		new TestParser(PropertiesTesting.WORKLOAD_WITH_MISSING_TRACE, 0);
	}
	
	@Test
	public void testConstructorWithWorkloadWithEmptyTrace() throws ConfigurationException{
		buildFullConfiguration();
		TestParser parser = new TestParser(PropertiesTesting.WORKLOAD_WITH_EMPTY_TRACE, 0);
		assertNotNull(parser);
		assertFalse(parser.hasNext());
		assertNull(parser.next());
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithWorkloadWithBlankLine() throws ConfigurationException{
		buildFullConfiguration();
		SimulationInfo info = Checkpointer.loadSimulationInfo();
		info.addDay();
		info.addDay();
		info.addDay();
		new TestParser(PropertiesTesting.WORKLOAD_WITH_BLANK_LINE, 0);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithWorkloadWithBlankEndLine() throws ConfigurationException{
		buildFullConfiguration();
		SimulationInfo info = Checkpointer.loadSimulationInfo();
		info.addDay();
		info.addDay();
		info.addDay();
		info.addDay();
		info.addDay();
		info.addDay();
		info.addDay();
		info.addDay();
		info.addDay();
		new TestParser(PropertiesTesting.WORKLOAD_WITH_BLANK_END_LINE, 0);
	}
	
	@Test
	public void testHasNext() throws ConfigurationException{
		buildFullConfiguration();
		TestParser parser = new TestParser(PropertiesTesting.WORKLOAD, 0);
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
		new TestParser(PropertiesTesting.WORKLOAD, 0).close();
	}
	
	
}
