package util;

import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.Before;

import commons.config.Configuration;
import commons.io.AbstractWorkloadParser;
import commons.io.Checkpointer;

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
		Checkpointer.clear();
		
		Field field = Configuration.class.getDeclaredField("instance");
		field.setAccessible(true);
		field.set(null, null);
		
		field = AbstractWorkloadParser.class.getDeclaredField("saasClientIDSeed");
		field.setAccessible(true);
		field.set(null, 0);
	}
	
	@AfterClass
	public static void tearDownAfterClass(){
		Checkpointer.clear();
	}


}
