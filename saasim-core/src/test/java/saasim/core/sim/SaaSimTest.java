/**
 * 
 */
package saasim.core.sim;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import saasim.core.config.Configuration;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SaaSimTest {

	private Configuration config;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		assert !new File(".saasim.dat").exists() || new File(".saasim.dat").delete(): "Unwanted file saasim.properties on root directory. Please delete it!";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		config = new Configuration("src/test/resources/scenario_01/config.properties");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link saasim.core.sim.SaaSim#SaaSim()}.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void testSaaSimWithCorruptedCheckpoint() throws ConfigurationException, IOException {
		assert new File(".saasim.dat").createNewFile(): "Could not create a file for testing purposes, verify permission.";
		new SaaSim(config);
		assert new File(".saasim.dat").delete(): "Could not delete file for testing purposes, verify permission.";
	}

	/**
	 * Test method for {@link saasim.core.sim.SaaSim#SaaSim()}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testSaaSimWithoutCheckpoint() throws ConfigurationException {
		new SaaSim(config);
	}

	/**
	 * Test method for {@link saasim.core.sim.SaaSim#SaaSim()}.
	 * @throws ConfigurationException 
	 */
	@Ignore@Test
	public void testSaaSimWithCheckpoint() throws ConfigurationException {
		new SaaSim(config).start();
		
		new SaaSim(config);
	}

	
	/**
	 * Test method for {@link saasim.core.sim.SaaSim#SaaSim(saasim.core.config.Configuration)}.
	 */
	@Test@Ignore
	public void testSaaSimConfiguration() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link saasim.core.sim.SaaSim#start()}.
	 */
	@Test@Ignore
	public void testStart() {
		fail("Not yet implemented");
	}

}
