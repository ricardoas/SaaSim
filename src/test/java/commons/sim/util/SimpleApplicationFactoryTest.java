package commons.sim.util;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.Monitor;

import commons.config.Configuration;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.schedulingheuristics.RoundRobinHeuristicForHeterogenousMachines;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class})
public class SimpleApplicationFactoryTest {

	private void clearInstance() throws NoSuchFieldException, IllegalAccessException {
		Field field = ApplicationFactory.class.getDeclaredField("instance");
		field.setAccessible(true);
		field.set(null, null);
	}
	
	@Before
	public void setUp() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		clearInstance();
	}
	
	@After
	public void tearDown() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		clearInstance();
	}
	
	@Test
	public void testGetInstanceWithValidClass() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn(SimpleApplicationFactory.class.getCanonicalName());
		
		PowerMock.replayAll(config);
		
		ApplicationFactory factory = ApplicationFactory.getInstance();
		assertNotNull(factory);
		
		PowerMock.verifyAll();
	}
	
	@Test(expected=RuntimeException.class)
	public void testGetInstanceWithInvalidClass() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("SimpleApplication");
		
		PowerMock.replayAll(config);
		
		ApplicationFactory.getInstance();
		
		PowerMock.verifyAll();
	}

	@Test
	public void testCreateNewApplication() throws ClassNotFoundException {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn(SimpleApplicationFactory.class.getCanonicalName());
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?> [] heuristicClasses = new Class<?>[1];
		heuristicClasses[0] = Class.forName(RoundRobinHeuristicForHeterogenousMachines.class.getCanonicalName());
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(heuristicClasses);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int []{3});
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		PowerMock.replayAll(config, scheduler, monitor);
		
		ApplicationFactory factory = ApplicationFactory.getInstance();
		assertNotNull(factory);
		
		LoadBalancer[] loadBalancers = factory.createNewApplication(scheduler, monitor);
		assertNotNull(loadBalancers);
		assertEquals(1, loadBalancers.length);
		assertEquals(0, loadBalancers[0].getTier());
		assertEquals(0, loadBalancers[0].getServers().size());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCreateNewApplicationWithMultipleTiers() throws ClassNotFoundException {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn(SimpleApplicationFactory.class.getCanonicalName());
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(2);
		Class<?> [] heuristicClasses = new Class<?>[2];
		heuristicClasses[0] = Class.forName(RoundRobinHeuristicForHeterogenousMachines.class.getCanonicalName());
		heuristicClasses[1] = Class.forName(RoundRobinHeuristicForHeterogenousMachines.class.getCanonicalName());
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(heuristicClasses);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int []{3, 3});
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(2);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		PowerMock.replayAll(config, scheduler, monitor);
		
		ApplicationFactory factory = ApplicationFactory.getInstance();
		assertNotNull(factory);
		
		LoadBalancer[] loadBalancers = factory.createNewApplication(scheduler, monitor);
		assertNotNull(loadBalancers);
		assertEquals(2, loadBalancers.length);
		assertEquals(0, loadBalancers[0].getTier());
		assertEquals(0, loadBalancers[0].getServers().size());
		assertEquals(1, loadBalancers[1].getTier());
		assertEquals(0, loadBalancers[1].getServers().size());
		
		PowerMock.verifyAll();
	}
}
