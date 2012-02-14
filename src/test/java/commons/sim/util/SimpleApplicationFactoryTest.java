package commons.sim.util;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.config.Configuration;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JECheckpointer;

public class SimpleApplicationFactoryTest extends ValidConfigurationTest{

	@Test
	public void testGetInstanceWithValidClass() throws ConfigurationException {
		buildFullConfiguration();
		assertNotNull(ApplicationFactory.getInstance());
	}
	
	@Test
	public void testGetInstanceWithInvalidClass() throws ConfigurationException {
		buildInvalidApplicationConfiguration();
		ApplicationFactory.getInstance();
	}

	@Test
	public void testCreateNewApplication() throws ConfigurationException {
		buildFullConfiguration();
		
		ApplicationFactory factory = ApplicationFactory.getInstance();
		assertNotNull(factory);
		
		LoadBalancer[] loadBalancers = factory.buildApplication(Configuration.getInstance().getScheduler());
		assertNotNull(loadBalancers);
		assertEquals(1, loadBalancers.length);
	}
	
	@Test
	public void testCreateNewApplicationWithMultipleTiers() throws ConfigurationException {
		buildManyTiersApplicationConfiguration();
		
		ApplicationFactory factory = ApplicationFactory.getInstance();
		assertNotNull(factory);
		
		LoadBalancer[] loadBalancers = factory.buildApplication(Configuration.getInstance().getScheduler());
		assertNotNull(loadBalancers);
		assertEquals(5, loadBalancers.length);
	}
}
