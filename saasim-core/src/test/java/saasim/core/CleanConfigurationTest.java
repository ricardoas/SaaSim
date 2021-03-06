package saasim.core;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


/**
 * Super class of tests which do not need a configuration
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class CleanConfigurationTest {
	
	@BeforeClass
	public static void setUpBeforeClass(){
		BasicConfigurator.configure();
	}

	/**
	 * Cleans up configuration singleton object.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
	}
	
	@After
	public void tearDown() {
	}

	@AfterClass
	public static void tearDownAfterClass(){
	}
}
