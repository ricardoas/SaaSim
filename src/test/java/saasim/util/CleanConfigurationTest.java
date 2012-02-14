package saasim.util;

import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.Before;

import saasim.config.Configuration;
import saasim.io.AbstractWorkloadParser;
import saasim.sim.jeevent.JECheckpointer;


/**
 * Super class of tests which do not need a configuration
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class CleanConfigurationTest {
	
	/**
	 * Cleans up configuration singleton object.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		JECheckpointer.clear();
		
		Field field = Configuration.class.getDeclaredField("instance");
		field.setAccessible(true);
		field.set(null, null);
		
		field = AbstractWorkloadParser.class.getDeclaredField("saasClientIDSeed");
		field.setAccessible(true);
		field.set(null, 0);
	}
	
	@AfterClass
	public static void tearDownAfterClass(){
		JECheckpointer.clear();
	}


}
