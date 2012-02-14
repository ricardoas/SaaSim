package commons.sim.util;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.config.Configuration;
import commons.sim.Simulator;
import commons.sim.jeevent.JECheckpointer;

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
