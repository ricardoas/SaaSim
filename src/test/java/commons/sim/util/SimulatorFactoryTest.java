package commons.sim.util;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.Monitor;

import commons.config.Configuration;
import commons.io.TickSize;
import commons.sim.SimpleSimulator;
import commons.sim.Simulator;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.SimulationInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, ApplicationFactory.class})
public class SimulatorFactoryTest {

	@Test
	public void testBuildSimulator(){
		SimulationInfo simInfo = new SimulationInfo(10, 3);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		ApplicationFactory factory = EasyMock.createStrictMock(ApplicationFactory.class);
		PowerMock.mockStatic(ApplicationFactory.class);
		EasyMock.expect(ApplicationFactory.getInstance()).andReturn(factory);
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		EasyMock.expect(factory.buildApplication(EasyMock.isA(JEEventScheduler.class))).andReturn(loadBalancers);
		
		PowerMock.replayAll(config, factory, lb1, monitor, scheduler);
		
		Simulator simulator = SimulatorFactory.buildSimulator(scheduler);
		assertNotNull(simulator);
		assertEquals(loadBalancers, simulator.getTiers());
		
		PowerMock.verifyAll();
	}
	
}
