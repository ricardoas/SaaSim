package commons.sim.util;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.io.Checkpointer;
import commons.sim.Simulator;

public class SimulatorFactoryTest extends ValidConfigurationTest{

	@Test
	public void testBuildSameSimulatorAfterCheckPoint() throws ConfigurationException{
		
		buildFullConfiguration();
		Simulator application = Checkpointer.loadApplication();
		assertNotNull(application);
		
		Checkpointer.save();
		
		buildFullConfiguration();
		
		Simulator applicationAfterCheckpoint = Checkpointer.loadApplication();
		assertNotNull(applicationAfterCheckpoint);
		assertEquals(applicationAfterCheckpoint, applicationAfterCheckpoint);
	}
	
}
