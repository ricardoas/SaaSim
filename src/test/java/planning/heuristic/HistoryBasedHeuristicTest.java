package planning.heuristic;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import planning.util.MachineUsageData;
import planning.util.PlanIOHandler;
import provisioning.DPS;
import provisioning.util.DPSFactory;
import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.TypeProvider;
import commons.cloud.User;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.io.TickSize;
import commons.io.WorkloadParser;
import commons.sim.SimpleSimulator;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorFactory;
import commons.sim.util.SimulatorProperties;
import commons.util.SimulationInfo;

@Ignore
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.apache.log4j.*")
@PrepareForTest({SimulatorFactory.class, DPSFactory.class, Configuration.class, PlanIOHandler.class})
public class HistoryBasedHeuristicTest extends ValidConfigurationTest{
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
		Checkpointer.clear();
	}
	
	@After
	public void tearDown(){
		Checkpointer.clear();
	}
	
	@Test
	public void testFindPlanWithoutServersAndOneDayFinished(){
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>());
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(lb1, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithoutServersAndMoreSimulationDaysNeeded(){

		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(2l);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>());
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(lb1, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		assertTrue(new File(Checkpointer.MACHINE_DATA_DUMP).exists());
		assertTrue(new File(Checkpointer.CHECKPOINT_FILE).exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUsedServersAndOneTier(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis());
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis() / 2);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 2);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TickSize.DAY.getTickInMillis() / 2);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 2);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(3, (int)plan.get(MachineType.M1_SMALL));
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUsedServersAndOneTier(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 6);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 5);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 10);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUsedServersAndOneTierAndServersAggregation(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 6);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 5);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 8);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));//0.4787037037037037 minimum utilisation
		types.add(new TypeProvider(1, MachineType.C1_MEDIUM, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(1, (int)plan.get(MachineType.M1_XLARGE));
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithMixedUsedServersAndOneTier(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.C1_MEDIUM, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 2);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 3);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_XLARGE, 0);
		descriptor3.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 3);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		types.add(new TypeProvider(1, MachineType.C1_MEDIUM, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_XLARGE)).andReturn(3d);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);

		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = lb1;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(2, plan.size());
		assertEquals(1, (int)plan.get(MachineType.C1_MEDIUM));
		assertEquals(1, (int)plan.get(MachineType.M1_XLARGE));
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUsedServersAndMoreThanOneDay() throws IOException, ClassNotFoundException{
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_LARGE, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 2);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 2);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_XLARGE, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 4);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 4);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis());
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		SimulationInfo simulationInfo2 = new SimulationInfo(2, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_LARGE, 0.085, 0.03, 227.50, 350, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		//Machine usage data to be used in the second day
		Map<MachineType, Map<Long, Double>> machineUsagePerType = new HashMap<MachineType, Map<Long,Double>>();
		Map<Long, Double> value = new HashMap<Long, Double>();
		value.put(1l, TickSize.DAY.getTickInMillis() * 2.0);
		machineUsagePerType.put(MachineType.M1_LARGE, value);
		
		Map<Long, Double> value2 = new HashMap<Long, Double>();
		value2.put(2l, TickSize.DAY.getTickInMillis() * 4.0);
		machineUsagePerType.put(MachineType.M1_XLARGE, value2);
		
		Map<Long, Double> value3 = new HashMap<Long, Double>();
		value3.put(3l, TickSize.DAY.getTickInMillis() * 1.0);
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
		
		//Configuration
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo2);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(2l).times(3);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(providers).times(3);
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
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
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall().times(2);
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator).times(2);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expectLastCall().times(2);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
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
		
		output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUsedServersAndMultipleTiers(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_LARGE, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 3);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_XLARGE, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() * 4);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis());
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_LARGE, 0.085, 0.03, 227.50, 350, 10));
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
//		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(1d);
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
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps, scheduler, parser);
		
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
	public void testFindPlanWithUnderUsedServersAndMultipleTiers(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 10);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 9);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(2 * TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 6);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
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
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUsedServersAndMultipleTiersAndAggregatedServers(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 3);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 0);
		descriptor2.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 4);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(2 * TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 5);
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.085, 0.03, 227.50, 350, 10));
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
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
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps, scheduler, parser);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic(scheduler, dps, loadBalancers);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(1, (int)plan.get(MachineType.M1_SMALL));
		
		File output = new File(Checkpointer.MACHINE_DATA_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}

	@Test
	public void testFindPlanWithMixedUsedServersAndMultipleTiers(){
		
		//First tier machines
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		descriptor.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 20);
		
		MachineDescriptor descriptor4 = new MachineDescriptor(4, false, MachineType.M1_SMALL, 0);
		descriptor4.setFinishTimeInMillis(3 * TickSize.DAY.getTickInMillis());
		Machine machine4 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine4.getDescriptor()).andReturn(descriptor4);
		EasyMock.expect(machine4.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis());
		
		//Second tier machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.T1_MICRO, 0);
		descriptor2.setFinishTimeInMillis(2 * TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(TickSize.HOUR.getTickInMillis() * 22);
		
		//Third tier machines
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 0);
		descriptor3.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis() / 5);
		
		Machine machine5 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor5 = new MachineDescriptor(5, false, MachineType.M1_SMALL, 0);
		descriptor5.setFinishTimeInMillis(TickSize.DAY.getTickInMillis());
		EasyMock.expect(machine5.getDescriptor()).andReturn(descriptor5);
		EasyMock.expect(machine5.getTotalTimeUsed()).andReturn(TickSize.DAY.getTickInMillis());
		
		SimulationInfo simulationInfo = new SimulationInfo(1, 0);
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(1, MachineType.T1_MICRO, 0.17, 0.06, 455, 700, 10));
		types.add(new TypeProvider(1, MachineType.M1_SMALL, 0.68, 0.24, 1820, 2800, 10));
//		types.add(new TypeProvider(1, MachineType.M1_LARGE, 0.34, 0.12, 910, 1400, 10));
//		types.add(new TypeProvider(1, MachineType.M1_XLARGE, 0.68, 0.24, 1820, 2800, 10));
		
		Provider provider = new Provider(1, "p1", 10, 20, 0.15, new long[]{0}, new double[]{0, 0}, new long[]{0}, new double[]{0, 0}, 
				types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(Checkpointer.loadSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{provider}).times(2);
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
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		
		WorkloadParser parser = EasyMock.createStrictMock(WorkloadParser.class);
		parser.applyError(0.0);
		EasyMock.expectLastCall();
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		EasyMock.expect(simulator.getParser()).andReturn(parser);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, machine4, machine5, lb1, lb2, lb3, simulator, dps, scheduler, parser);
		
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
