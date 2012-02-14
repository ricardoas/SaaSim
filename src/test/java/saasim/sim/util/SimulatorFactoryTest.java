package saasim.sim.util;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import saasim.config.Configuration;
import saasim.sim.Simulator;
import saasim.sim.jeevent.JECheckpointer;
import saasim.util.ValidConfigurationTest;


public class SimulatorFactoryTest extends ValidConfigurationTest{

	@Test
	public void testBuildSameSimulatorAfterCheckPoint() throws ConfigurationException{
		
		buildFullConfiguration();
		Simulator application = Configuration.getInstance().getApplication();
		assertNotNull(application);
		
		JECheckpointer.save();
		
		buildFullConfiguration();
		
		Simulator applicationAfterCheckpoint = Configuration.getInstance().getApplication();
		assertNotNull(applicationAfterCheckpoint);
		assertEquals(applicationAfterCheckpoint, applicationAfterCheckpoint);
	}
	
}
