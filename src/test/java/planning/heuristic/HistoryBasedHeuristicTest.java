package planning.heuristic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.DPS;
import provisioning.Monitor;
import provisioning.util.DPSFactory;
import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.UtilityResult;
import commons.sim.SimpleSimulator;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorFactory;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.apache.log4j.*")
@PrepareForTest({SimulatorFactory.class, DPSFactory.class})
public class HistoryBasedHeuristicTest extends ValidConfigurationTest {
		
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}
	
	@Test
	public void testFindPlanWithoutServers(){
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>());
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{lb1};
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class), EasyMock.isA(Monitor.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expect(dps.calculateUtility()).andReturn(new UtilityResult(0, 0));
		PowerMock.mockStatic(DPSFactory.class);
		EasyMock.expect(DPSFactory.createDPS()).andReturn(dps);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replay(DPSFactory.class);
		PowerMock.replayAll(lb1, simulator, dps);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic();
		heuristic.findPlan(null, null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUtilisedServersAndOneTier(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor.setFinishTimeInMillis(200);
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(100l);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor).times(2);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor2.setFinishTimeInMillis(999);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(555l);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2).times(2);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor3.setFinishTimeInMillis(200);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(200l);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3).times(2);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{lb1};
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class), EasyMock.isA(Monitor.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expect(dps.calculateUtility()).andReturn(new UtilityResult(0, 0));
		PowerMock.mockStatic(DPSFactory.class);
		EasyMock.expect(DPSFactory.createDPS()).andReturn(dps);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replay(DPSFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic();
		heuristic.findPlan(null, null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(3, (int)plan.get(MachineType.SMALL));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUtilisedServersAndOneTier(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor.setFinishTimeInMillis(200);
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(99l);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getNumberOfCores()).andReturn(1);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor2.setFinishTimeInMillis(999);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(400l);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getNumberOfCores()).andReturn(1);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor3.setFinishTimeInMillis(200);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(2l);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getNumberOfCores()).andReturn(1);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{lb1};
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class), EasyMock.isA(Monitor.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expect(dps.calculateUtility()).andReturn(new UtilityResult(0, 0));
		PowerMock.mockStatic(DPSFactory.class);
		EasyMock.expect(DPSFactory.createDPS()).andReturn(dps);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replay(DPSFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic();
		heuristic.findPlan(null, null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithMixedUtilisedServersAndOneTier(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.MEDIUM, 0);
		descriptor.setFinishTimeInMillis(200);
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(101l);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor).times(2);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor2.setFinishTimeInMillis(999);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(400l);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getNumberOfCores()).andReturn(1);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(1, false, MachineType.XLARGE, 0);
		descriptor3.setFinishTimeInMillis(333);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(199l);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3).times(2);

		ArrayList<Machine> machines = new ArrayList<Machine>();
		machines.add(machine1);
		machines.add(machine2);
		machines.add(machine3);
		
		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(machines);
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{lb1};
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class), EasyMock.isA(Monitor.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expect(dps.calculateUtility()).andReturn(new UtilityResult(0, 0));
		PowerMock.mockStatic(DPSFactory.class);
		EasyMock.expect(DPSFactory.createDPS()).andReturn(dps);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replay(DPSFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, simulator, dps);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic();
		heuristic.findPlan(null, null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(2, plan.size());
		assertEquals(1, (int)plan.get(MachineType.MEDIUM));
		assertEquals(1, (int)plan.get(MachineType.XLARGE));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithWellUtilisedServersAndMultipleTiers(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor.setFinishTimeInMillis(200);
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(100l);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor).times(2);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor2.setFinishTimeInMillis(999);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(555l);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2).times(2);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor3.setFinishTimeInMillis(200);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(200l);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3).times(2);

		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1)));
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2)));
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3)));
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{lb1, lb2, lb3};
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class), EasyMock.isA(Monitor.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expect(dps.calculateUtility()).andReturn(new UtilityResult(0, 0));
		PowerMock.mockStatic(DPSFactory.class);
		EasyMock.expect(DPSFactory.createDPS()).andReturn(dps);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replay(DPSFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic();
		heuristic.findPlan(null, null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		assertEquals(3, (int)plan.get(MachineType.SMALL));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithUnderUtilisedServersAndMultipleTiers(){
		
		//First machine
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor.setFinishTimeInMillis(99);
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(7l);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getNumberOfCores()).andReturn(1);
		
		//Second machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor2.setFinishTimeInMillis(2150);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(179l);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getNumberOfCores()).andReturn(1);
		
		//Third machine
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(1, false, MachineType.SMALL, 0);
		descriptor3.setFinishTimeInMillis(10000);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(200l);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getNumberOfCores()).andReturn(1);

		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1)));
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2)));
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3)));
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{lb1, lb2, lb3};
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class), EasyMock.isA(Monitor.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expect(dps.calculateUtility()).andReturn(new UtilityResult(0, 0));
		PowerMock.mockStatic(DPSFactory.class);
		EasyMock.expect(DPSFactory.createDPS()).andReturn(dps);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replay(DPSFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, lb1, lb2, lb3, simulator, dps);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic();
		heuristic.findPlan(null, null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(0, plan.size());
		
		PowerMock.verifyAll();
	}

	@Test
	public void testFindPlanWithMixedUtilisedServersAndMultipleTiers(){
		
		//First tier machines
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.HIGHCPU, 0);
		descriptor.setFinishTimeInMillis(100);
		Machine machine1 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine1.getTotalTimeUsed()).andReturn(67l);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor).times(2);
		
		MachineDescriptor descriptor4 = new MachineDescriptor(4, false, MachineType.XLARGE, 0);
		descriptor4.setFinishTimeInMillis(333);
		Machine machine4 = EasyMock.createStrictMock(Machine.class);
		EasyMock.expect(machine4.getTotalTimeUsed()).andReturn(7l);
		EasyMock.expect(machine4.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine4.getNumberOfCores()).andReturn(1);
		
		//Second tier machine
		Machine machine2 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.LARGE, 0);
		descriptor2.setFinishTimeInMillis(2150);
		EasyMock.expect(machine2.getTotalTimeUsed()).andReturn(1798l);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2).times(2);
		
		//Third tier machines
		Machine machine3 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.MEDIUM, 0);
		descriptor3.setFinishTimeInMillis(10);
		EasyMock.expect(machine3.getTotalTimeUsed()).andReturn(2l);
		EasyMock.expect(machine3.getDescriptor()).andReturn(descriptor3);
		EasyMock.expect(machine3.getNumberOfCores()).andReturn(1);
		
		Machine machine5 = EasyMock.createStrictMock(Machine.class);
		MachineDescriptor descriptor5 = new MachineDescriptor(5, false, MachineType.HIGHCPU, 0);
		descriptor5.setFinishTimeInMillis(10);
		EasyMock.expect(machine5.getTotalTimeUsed()).andReturn(8l);
		EasyMock.expect(machine5.getDescriptor()).andReturn(descriptor5);
		EasyMock.expect(machine5.getNumberOfCores()).andReturn(1);
		EasyMock.expect(machine5.getDescriptor()).andReturn(descriptor5).times(2);

		//Load balancer
		LoadBalancer lb1 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb2 = EasyMock.createStrictMock(LoadBalancer.class);
		LoadBalancer lb3 = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(lb1.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine1, machine4)));
		EasyMock.expect(lb2.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine2)));
		EasyMock.expect(lb3.getServers()).andReturn(new ArrayList<Machine>(Arrays.asList(machine3, machine5)));
		
		LoadBalancer[] loadBalancers = new LoadBalancer[]{lb1, lb2, lb3};
		
		//Simulator
		SimpleSimulator simulator = EasyMock.createStrictMock(SimpleSimulator.class);
		simulator.start();
		EasyMock.expect(simulator.getTiers()).andReturn(loadBalancers);
		
		PowerMock.mockStatic(SimulatorFactory.class);
		EasyMock.expect(SimulatorFactory.buildSimulator(EasyMock.isA(JEEventScheduler.class), EasyMock.isA(Monitor.class))).andReturn(simulator);
		
		//Provisioning system
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.registerConfigurable(simulator);
		EasyMock.expect(dps.calculateUtility()).andReturn(new UtilityResult(0, 0));
		PowerMock.mockStatic(DPSFactory.class);
		EasyMock.expect(DPSFactory.createDPS()).andReturn(dps);

		PowerMock.replay(SimulatorFactory.class);
		PowerMock.replay(DPSFactory.class);
		PowerMock.replayAll(machine1, machine2, machine3, machine4, machine5, lb1, lb2, lb3, simulator, dps);
		
		HistoryBasedHeuristic heuristic = new HistoryBasedHeuristic();
		heuristic.findPlan(null, null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(2, plan.size());
		assertEquals(2, (int)plan.get(MachineType.HIGHCPU));
		assertEquals(1, (int)plan.get(MachineType.LARGE));
		
		PowerMock.verifyAll();
	}
	
}
