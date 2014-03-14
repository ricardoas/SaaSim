package saasim.core;

import java.lang.reflect.Field;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import saasim.core.config.Configuration;
import saasim.core.event.EventCheckpointer;


/**
 * Super class of tests which do not need a configuration
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class CleanConfigurationTest {
	
	@BeforeClass
	public static void setUpBeforeClass(){
		BasicConfigurator.configure();
		EventCheckpointer.clear();
	}

	/**
	 * Cleans up configuration singleton object.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		
		Field field = Configuration.class.getDeclaredField("instance");
		field.setAccessible(true);
		field.set(null, null);
		
//		FIXME UNCOMMENT ME AFTER IMPLEMENTING AbstractWorkloadParser 
//		field = AbstractWorkloadParser.class.getDeclaredField("saasClientIDSeed");
//		field.setAccessible(true);
//		field.set(null, 0);
	}
	
	@After
	public void tearDown() {
		EventCheckpointer.clear();
	}

	@AfterClass
	public static void tearDownAfterClass(){
		EventCheckpointer.clear();
	}
}
