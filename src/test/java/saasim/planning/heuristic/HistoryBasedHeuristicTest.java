package saasim.planning.heuristic;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.TypeProvider;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.planning.heuristic.HistoryBasedHeuristic;
import saasim.planning.util.MachineUsageData;
import saasim.planning.util.PlanIOHandler;
import saasim.provisioning.DPS;
import saasim.provisioning.util.DPSFactory;
import saasim.sim.SimpleSimulator;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.jeevent.JECheckpointer;
import saasim.sim.jeevent.JEEventScheduler;
import saasim.sim.schedulingheuristics.RoundRobinHeuristic;
import saasim.sim.util.SaaSAppProperties;
import saasim.sim.util.SimulatorFactory;
import saasim.sim.util.SimulatorProperties;
import saasim.util.SimulationInfo;
import saasim.util.TimeUnit;
import saasim.util.ValidConfigurationTest;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.apache.log4j.*")
@PrepareForTest({SimulatorFactory.class, DPSFactory.class, Configuration.class, PlanIOHandler.class, JECheckpointer.class})
public class HistoryBasedHeuristicTest extends ValidConfigurationTest{
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
		JECheckpointer.clear();
	}
	
	@After
	public void tearDown(){
		JECheckpointer.clear();
	}
	
	@Test
	public void testFindPlanWithoutServersAndOneDayFinished() throws ConfigurationException, ClassNotFoundException{
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>());
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(lb1, simulator, dps);
		
		JECheckpointer.loadData();
		
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithoutServersAndMoreSimulationDaysNeeded() throws ClassNotFoundException, ConfigurationException{

		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 2);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(Checkpointer.loadProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(2l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>());
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(lb1, simulator, dps);

		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		assertTrue(new File(JECheckpointer.MACHINE_DATA_DUMP).exists());
		assertTrue(new File(JECheckpointer.CHECKPOINT_FILE).exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUsedServersAndOneTier() throws ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis());
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis() / 2);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 2);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TimeUnit.DAY.getMillis() / 2);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 2);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(3, (int)plan.get(MachineType.M1_SMALL));
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUsedServersAndOneTier() throws ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 6);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 5);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 10);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUsedServersAndOneTierAndServersAggregation() throws ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 6);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 5);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 8);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));//0.4787037037037037 minimum utilisation
		types.add(new TypeProvider(1, MachineType.C1_MEDIUM, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps);

		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(1, (int)plan.get(MachineType.C1_MEDIUM));
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithMixedUsedServersAndOneTier() throws ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.C1_MEDIUM, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 2);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 3);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_XLARGE, 0);
		descriptor3.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 3);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		types.add(new TypeProvider(1, MachineType.C1_MEDIUM, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_XLARGE)).andReturn(3d);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);

		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(2, plan.size());
		assertEquals(1, (int)plan.get(MachineType.C1_MEDIUM));
		assertEquals(1, (int)plan.get(MachineType.M1_XLARGE));
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUsedServersAndMoreThanOneDay() throws IOException, ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_LARGE, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 2);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 2);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_XLARGE, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 4);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 4);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis());
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 2);
		SimulationInfo simulationInfo2 = new SimulationInfo(2, 0, 2);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_LARGE, 0.085, 0.03, 227.50, 350, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		//Machine usage data to be used in the second day
		Map<MachineType, Map<Long, Double>> machineUsagePerType = new HashMap<MachineType, Map<Long,Double>>();
		Map<Long, Double> value = new HashMap<Long, Double>();
		value.put(1l, TimeUnit.DAY.getMillis() * 2.0);
		machineUsagePerType.put(MachineType.M1_LARGE, value);
		
		Map<Long, Double> value2 = new HashMap<Long, Double>();
		value2.put(2l, TimeUnit.DAY.getMillis() * 4.0);
		machineUsagePerType.put(MachineType.M1_XLARGE, value2);
		
		Map<Long, Double> value3 = new HashMap<Long, Double>();
		value3.put(3l, TimeUnit.DAY.getMillis() * 1.0);
		machineUsagePerType.put(MachineType.M1_SMALL, value3);
		
		MachineUsageData machineData = new MachineUsageData(machineUsagePerType);
		Provider[] providers = new Provider[]{provider};
		
		PowerMock.mockStatic(PlanIOHandler.class);
		EasyMock.expect(PlanIOHandler.getMachineData()).andReturn(null);
		EasyMock.expect(PlanIOHandler.getMachineData()).andReturn(machineData);
		Map<MachineType, Integer> map = new HashMap<MachineType, Integer>();
		map.put(MachineType.M1_XLARGE, 1);
		map.put(MachineType.M1_LARGE, 1);
		map.put(MachineType.M1_SMALL, 1);
		PlanIOHandler.clear();
		PlanIOHandler.createPlanFile(map, providers);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		//Configuration
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo2);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(2l).times(3);
//		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(1d);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
//		EasyMock.expect(config.getRelativePower(MachineType.C1_XLARGE)).andReturn(2d);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0).times(2);
		
		PowerMock.replay(Configuration.class, PlanIOHandler.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1))).times(2);
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2))).times(2);
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3))).times(2);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[3];
		loadBalancers[0] = lb1;
		loadBalancers[1] = lb2;
		loadBalancers[2] = lb3;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(EasyMock.isA(SimpleSimulator.class));
		EasyMock.expectLastCall().times(2);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertTrue(output.exists());
		
		//Second day
		output.delete();
		
		heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(3, plan.size());
		assertEquals(1, (int)plan.get(MachineType.M1_XLARGE));
		assertEquals(1, (int)plan.get(MachineType.M1_SMALL));
		assertEquals(1, (int)plan.get(MachineType.M1_LARGE));
		
		output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUsedServersAndMultipleTiers() throws ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_LARGE, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 3);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_XLARGE, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() * 4);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis());
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_LARGE, 0.085, 0.03, 227.50, 350, 10));
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
//		EasyMock.expect(config.getRelativePower(MachineType.C1_XLARGE)).andReturn(2d);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1)));
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2)));
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3)));
		
		LoadBalancer[] loadBalancers = new LoadBalancer[3];
		loadBalancers[0] = lb1;
		loadBalancers[1] = lb2;
		loadBalancers[2] = lb3;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(3, plan.size());
		assertEquals(1, (int)plan.get(MachineType.M1_XLARGE));
		assertEquals(1, (int)plan.get(MachineType.M1_SMALL));
		assertEquals(1, (int)plan.get(MachineType.M1_LARGE));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUsedServersAndMultipleTiers() throws ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 10);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 9);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(2 * TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 6);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1)));
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2)));
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3)));
		
		LoadBalancer[] loadBalancers = new LoadBalancer[3];
		loadBalancers[0] = lb1;
		loadBalancers[1] = lb2;
		loadBalancers[2] = lb3;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUsedServersAndMultipleTiersAndAggregatedServers() throws ClassNotFoundException, ConfigurationException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 3);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 4);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(2 * TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 5);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
//		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1)));
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2)));
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3)));
		
		LoadBalancer[] loadBalancers = new LoadBalancer[3];
		loadBalancers[0] = lb1;
		loadBalancers[1] = lb2;
		loadBalancers[2] = lb3;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(1, (int)plan.get(MachineType.M1_SMALL));
		
		File output = new File(JECheckpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}

	//FIXME:
	@Test
	public void testFindPlanWithMixedUsedServersAndMultipleTiers() throws ClassNotFoundException, ConfigurationException{
		
		//First tier machines
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 20);
		
		MachineDescriptor descriptor4 = new MachineDescriptor(4, false, MachineType.M1_SMALL, 0);
		descriptor4.setFinishTimeInMillis(3 * TimeUnit.DAY.getMillis());
		Machine machine4 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine4.getDescriptor()).andReturn(descriptor4);
		EasyMock.expect(machine4.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis());
		
		//Second tier machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.T1_MICRO, 0);
		descriptor2.setFinishTimeInMillis(2 * TimeUnit.DAY.getMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TimeUnit.HOUR.getMillis() * 22);
		
		//Third tier machines
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis() / 5);
		
		Machine machine5 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor5 = new MachineDescriptor(5, false, MachineType.M1_SMALL, 0);
		descriptor5.setFinishTimeInMillis(TimeUnit.DAY.getMillis());
		EasyMock.expect(machine5.getDescriptor()).andReturn(descriptor5);
		EasyMock.expect(machine5.getTotalTimeUsed()).andReturn(TimeUnit.DAY.getMillis());
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0, 1);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.T1_MICRO, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.68, 0.24, 1820, 2800, 10));
//		types.add(new TypeProvider(1, MachineType.M1_LARGE, 0.34, 0.12, 910, 1400, 10));
//		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		PowerMock.mockStaticPartial(JECheckpointer.class, "loadSimulationInfo", "loadProviders");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.readProviders()).andReturn(providers);
		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getRelativePower(MachineType.C1_XLARGE)).andReturn(1d).times(2);
//		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(1d);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(1d);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_XLARGE)).andReturn(1d);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1, machine4)));
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2)));
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3, machine5)));
		
		LoadBalancer[] loadBalancers = new LoadBalancer[3];
		loadBalancers[0] = lb1;
		loadBalancers[1] = lb2;
		loadBalancers[2] = lb3;
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, machine4, machine5, lb1, lb2, lb3, simulator, dps);
		
		JECheckpointer.loadData();
		JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(2, plan.size());
		assertEquals(2, (int)plan.get(MachineType.M1_SMALL));
		assertEquals(1, (int)plan.get(MachineType.T1_MICRO));
		
		PowerMock.verifyAll();
	}
}