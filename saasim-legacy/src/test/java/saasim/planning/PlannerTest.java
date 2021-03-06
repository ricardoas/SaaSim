package saasim.planning;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.config.PropertiesTesting;
import saasim.planning.heuristic.PlanningHeuristic;
import saasim.planning.util.PlanningHeuristicFactory;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventScheduler;


@RunWith(PowerMockRunner.class)
@PrepareForTest(PlanningHeuristicFactory.class)
public class PlannerTest {

	@Before
	public void setUp() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_SINGLE_WORKLOAD_FILE);
	}
	
	@Test
	public void testConstructor() {
		Provider prov1 = EasyMock.createStrictMock(Provider.class);
		User user1 = EasyMock.createStrictMock(User.class);
		
		Provider[] providers = new Provider[]{prov1};
		User[] cloudUsers = new User[]{user1};
		
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.replay(prov1, user1, scheduler, monitor);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		new Planner(scheduler, monitor, loadBalancers, providers, cloudUsers);
		
		EasyMock.verify(prov1, user1);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithNullUsers() {
		Provider prov1 = EasyMock.createStrictMock(Provider.class);
		User user1 = EasyMock.createStrictMock(User.class);
		
		Provider[] providers = new Provider[]{prov1};
		User[] cloudUsers = null;
		
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.replay(prov1, user1, scheduler, monitor);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		new Planner(scheduler, monitor, loadBalancers, providers, cloudUsers);
		
		EasyMock.verify(prov1, user1);
	}
	
	@Test(expected=RuntimeException.class)
	public void testConstructorWithNullProviders() {
		Provider prov1 = EasyMock.createStrictMock(Provider.class);
		User user1 = EasyMock.createStrictMock(User.class);
		
		Provider[] providers = null;
		User[] cloudUsers = new User[]{user1};
		
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.replay(prov1, user1, scheduler, monitor);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		new Planner(scheduler, monitor, loadBalancers, providers, cloudUsers);
		
		EasyMock.verify(prov1, user1);
	}

	@Test
	public void testPlan() {
		Map<MachineType, Integer> plan = new HashMap<MachineType, Integer>();
		plan.put(MachineType.C1_MEDIUM, 3);
		plan.put(MachineType.C1_MEDIUM, 1);
		
		Provider prov1 = EasyMock.createStrictMock(Provider.class);
		User user1 = EasyMock.createStrictMock(User.class);
		
		Provider[] providers = new Provider[]{prov1};
		User[] cloudUsers = new User[]{user1};
		
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		PlanningHeuristic heuristic = EasyMock.createStrictMock(PlanningHeuristic.class);
		PowerMock.mockStatic(PlanningHeuristicFactory.class);
		EasyMock.expect(PlanningHeuristicFactory.createHeuristic(scheduler, monitor, loadBalancers)).andReturn(heuristic);
		heuristic.findPlan(providers, cloudUsers);
		EasyMock.expect(heuristic.getPlan(cloudUsers)).andReturn(plan);
		
		PowerMock.replayAll(prov1, user1, heuristic, scheduler, monitor);
		
		Planner planner = new Planner(scheduler, monitor, loadBalancers, providers, cloudUsers);
		Map<MachineType, Integer> obtainedPlan = planner.plan();
		assertNotNull(obtainedPlan);
		assertEquals(plan, obtainedPlan);
		
		PowerMock.verifyAll();
	}

}
