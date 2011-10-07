package planning.heuristic;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import planning.util.PlanIOHandler;
import provisioning.DPS;
import provisioning.util.DPSFactory;
import provisioning.util.WorkloadParserFactory;
import util.MockedConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.io.TickSize;
import commons.io.TimeBasedWorkloadParser;
import commons.io.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.ApplicationFactory;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;
import commons.util.SimulationInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkloadParserFactory.class, Configuration.class, ApplicationFactory.class, DPSFactory.class})
public class OverProvisionHeuristicTest extends MockedConfigurationTest {
	
	@Before
	public void setUp(){
		cleanDumpFiles();
	}
	
	@After
	public void tearDown(){
		cleanDumpFiles();
	}
	
	private void cleanDumpFiles() {
		new File(Checkpointer.MACHINE_DATA_DUMP).delete();
		new File(Checkpointer.MACHINES_DUMP).delete();
		new File(Checkpointer.PROVIDERS_DUMP).delete();
		new File(Checkpointer.SIMULATION_DUMP).delete();
		new File(Checkpointer.USERS_DUMP).delete();
		new File(PlanIOHandler.NUMBER_OF_MACHINES_FILE).delete();
	}
	
	@Test
	public void testFindPlanWithWorkloadWithEmptyWorkload() throws Exception{

		long sla = 8000l;
		
		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(9);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla).times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("p1");
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(new ArrayList<Request>());
		EasyMock.expect(parser.hasNext()).andReturn(false);
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.SMALL);
		assertEquals(0, value);
		
		File output = new File(Checkpointer.SIMULATION_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}

	@Test
	public void testFindPlanWithRequestsThatDoNotOverlapAndDifferentArrivalTimes() throws Exception{
		long sla = 8000l;
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(57l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(100l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(180l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(82l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(30l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(310l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(40l);
		Request request5 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(60467l);
		EasyMock.expect(request5.getTotalMeanToProcess()).andReturn(50l);
		Request request6 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(60501l);
		EasyMock.expect(request6.getTotalMeanToProcess()).andReturn(60l);
		Request request7 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request7.getArrivalTimeInMillis()).andReturn(60610l);
		EasyMock.expect(request7.getTotalMeanToProcess()).andReturn(70l);
		Request request8 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request8.getArrivalTimeInMillis()).andReturn(60705l);
		EasyMock.expect(request8.getTotalMeanToProcess()).andReturn(80l);
		Request request9 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request9.getArrivalTimeInMillis()).andReturn(60801l);
		EasyMock.expect(request9.getTotalMeanToProcess()).andReturn(90l);
		
		List<Request> firstIntervalRequests = new ArrayList<Request>();
		firstIntervalRequests.add(request);
		firstIntervalRequests.add(request2);
		firstIntervalRequests.add(request3);
		firstIntervalRequests.add(request4);
		
		List<Request> secondIntervalRequests = new ArrayList<Request>();
		secondIntervalRequests.add(request5);
		secondIntervalRequests.add(request6);
		secondIntervalRequests.add(request7);
		secondIntervalRequests.add(request8);
		secondIntervalRequests.add(request9);
		
		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla).times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("p1");
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.SMALL);
		assertEquals(1, value, 0.0001);
		
		//Checking some values ...
		Field field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(1, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(9l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(602d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(66.888889d, (Double)field.get(heuristic), 0.00001d);
		
		File output = new File(Checkpointer.SIMULATION_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithRequestsThatDoNotOverlapAndSameArrivalTimes() throws Exception{
		long sla = 8000l;
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(57l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(100l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(90l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(82l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(30l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(310l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(40l);
		Request request5 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(60467l);
		EasyMock.expect(request5.getTotalMeanToProcess()).andReturn(50l);
		Request request6 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(60501l);
		EasyMock.expect(request6.getTotalMeanToProcess()).andReturn(60l);
		Request request7 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request7.getArrivalTimeInMillis()).andReturn(60599l);
		EasyMock.expect(request7.getTotalMeanToProcess()).andReturn(70l);
		Request request8 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request8.getArrivalTimeInMillis()).andReturn(60705l);
		EasyMock.expect(request8.getTotalMeanToProcess()).andReturn(80l);
		Request request9 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request9.getArrivalTimeInMillis()).andReturn(60801l);
		EasyMock.expect(request9.getTotalMeanToProcess()).andReturn(90l);
		
		List<Request> firstIntervalRequests = new ArrayList<Request>();
		firstIntervalRequests.add(request);
		firstIntervalRequests.add(request2);
		firstIntervalRequests.add(request3);
		firstIntervalRequests.add(request4);
		
		List<Request> secondIntervalRequests = new ArrayList<Request>();
		secondIntervalRequests.add(request5);
		secondIntervalRequests.add(request6);
		secondIntervalRequests.add(request7);
		secondIntervalRequests.add(request8);
		secondIntervalRequests.add(request9);
		
		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla).times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("p1");
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.SMALL);
		assertEquals(1, value, 0.0001);
		
		//Checking some values ...
		Field field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(2, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(9l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(602d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(66.888889d, (Double)field.get(heuristic), 0.00001d);
		
		File output = new File(Checkpointer.SIMULATION_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithRequestsThatOverlapAndDifferentArrivalTimes() throws Exception{
		long sla = 8000l;
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(57l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(200l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(180l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(82l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(300l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(310l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(40l);
		Request request5 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(60467l);
		EasyMock.expect(request5.getTotalMeanToProcess()).andReturn(50l);
		Request request6 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(60501l);
		EasyMock.expect(request6.getTotalMeanToProcess()).andReturn(60l);
		Request request7 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request7.getArrivalTimeInMillis()).andReturn(60610l);
		EasyMock.expect(request7.getTotalMeanToProcess()).andReturn(700l);
		Request request8 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request8.getArrivalTimeInMillis()).andReturn(60705l);
		EasyMock.expect(request8.getTotalMeanToProcess()).andReturn(80l);
		Request request9 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request9.getArrivalTimeInMillis()).andReturn(60801l);
		EasyMock.expect(request9.getTotalMeanToProcess()).andReturn(90l);
		
		List<Request> firstIntervalRequests = new ArrayList<Request>();
		firstIntervalRequests.add(request);
		firstIntervalRequests.add(request2);
		firstIntervalRequests.add(request3);
		firstIntervalRequests.add(request4);
		
		List<Request> secondIntervalRequests = new ArrayList<Request>();
		secondIntervalRequests.add(request5);
		secondIntervalRequests.add(request6);
		secondIntervalRequests.add(request7);
		secondIntervalRequests.add(request8);
		secondIntervalRequests.add(request9);
		
		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla).times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("p1");
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.SMALL);
		assertEquals(1, value, 0.0001);
		
		//Checking some values ...
		Field field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(2, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(9l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(1602d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(178d, (Double)field.get(heuristic), 0.00001d);
		
		File output = new File(Checkpointer.SIMULATION_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithRequestsThatOverlapAndSameArrivalTimes() throws Exception{
		long sla = 8000l;
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(157l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(200l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(180l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(82l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(300l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(270l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(40l);
		Request request5 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(60467l);
		EasyMock.expect(request5.getTotalMeanToProcess()).andReturn(50l);
		Request request6 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(60501l);
		EasyMock.expect(request6.getTotalMeanToProcess()).andReturn(60l);
		Request request7 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request7.getArrivalTimeInMillis()).andReturn(60610l);
		EasyMock.expect(request7.getTotalMeanToProcess()).andReturn(700l);
		Request request8 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request8.getArrivalTimeInMillis()).andReturn(60705l);
		EasyMock.expect(request8.getTotalMeanToProcess()).andReturn(80l);
		Request request9 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request9.getArrivalTimeInMillis()).andReturn(60801l);
		EasyMock.expect(request9.getTotalMeanToProcess()).andReturn(90l);
		
		List<Request> firstIntervalRequests = new ArrayList<Request>();
		firstIntervalRequests.add(request);
		firstIntervalRequests.add(request2);
		firstIntervalRequests.add(request3);
		firstIntervalRequests.add(request4);
		
		List<Request> secondIntervalRequests = new ArrayList<Request>();
		secondIntervalRequests.add(request5);
		secondIntervalRequests.add(request6);
		secondIntervalRequests.add(request7);
		secondIntervalRequests.add(request8);
		secondIntervalRequests.add(request9);
		
		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla).times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("p1");
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.SMALL);
		assertEquals(1, value, 0.0001);
		
		//Checking some values ...
		Field field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(3, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(9l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(1602d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(178d, (Double)field.get(heuristic), 0.00001d);
		
		File output = new File(Checkpointer.SIMULATION_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	/**
	 * In this test one collection of requests is used with requests that overlap and arrive in 
	 * same interval. The difference from above test is that the SLA is equals to request mean demand,
	 * so maximumNumberOfServers is reduced only by a factor of 20% and not by queue size
	 * @throws Exception
	 */
	@Test
	public void testFindPlanWithRequestsThatOverlapAndSameArrivalTimesAndDifferentSLA() throws Exception{
		long sla = 178l;//SLA is equals to mean request demand
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(157l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(600l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(180l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(600l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(400l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(270l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(300l);
		Request request5 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(367l);
		EasyMock.expect(request5.getTotalMeanToProcess()).andReturn(200l);
		Request request6 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(401l);
		EasyMock.expect(request6.getTotalMeanToProcess()).andReturn(200l);
		Request request7 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request7.getArrivalTimeInMillis()).andReturn(510l);
		EasyMock.expect(request7.getTotalMeanToProcess()).andReturn(700l);
		Request request8 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request8.getArrivalTimeInMillis()).andReturn(605l);
		EasyMock.expect(request8.getTotalMeanToProcess()).andReturn(80l);
		Request request9 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request9.getArrivalTimeInMillis()).andReturn(701l);
		EasyMock.expect(request9.getTotalMeanToProcess()).andReturn(90l);
		
		List<Request> firstIntervalRequests = new ArrayList<Request>();
		firstIntervalRequests.add(request);
		firstIntervalRequests.add(request2);
		firstIntervalRequests.add(request3);
		firstIntervalRequests.add(request4);
		firstIntervalRequests.add(request5);
		firstIntervalRequests.add(request6);
		firstIntervalRequests.add(request7);
		firstIntervalRequests.add(request8);
		firstIntervalRequests.add(request9);
		
		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla).times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("p1");
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.SMALL);
		assertEquals(3, value, 0.0001);
		
		//Checking some values ...
		Field field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(7, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(9l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(1602d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(178d, (Double)field.get(heuristic), 0.00001d);
		
		File output = new File(Checkpointer.SIMULATION_DUMP);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
//	@Test
//	public void testFindPlanWithWorkloadWithUniqueUsersAndDifferentNumberOfRequestsForMoreThanOneDay() throws Exception{
//		long sla = 8000l;
//		
//		Request request = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request.getUserID()).andReturn(1).times(2);
//		Request request2 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request2.getUserID()).andReturn(2).times(2);
//		Request request3 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request3.getUserID()).andReturn(3).times(2);
//		Request request4 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request4.getUserID()).andReturn(4).times(2);
//		Request request5 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request5.getUserID()).andReturn(5).times(2);
//		Request request6 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request6.getUserID()).andReturn(6).times(2);
//		Request request7 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request7.getUserID()).andReturn(7).times(2);
//		Request request8 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request8.getUserID()).andReturn(8).times(2);
//		Request request9 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request9.getUserID()).andReturn(9).times(2);
//		
//		List<Request> firstIntervalRequests = new ArrayList<Request>();
//		firstIntervalRequests.add(request);
//		firstIntervalRequests.add(request2);
//		firstIntervalRequests.add(request3);
//		firstIntervalRequests.add(request4);
//		
//		List<Request> secondIntervalRequests = new ArrayList<Request>();
//		secondIntervalRequests.add(request5);
//		secondIntervalRequests.add(request6);
//		secondIntervalRequests.add(request7);
//		secondIntervalRequests.add(request8);
//		secondIntervalRequests.add(request9);
//		
//		DPS monitor = EasyMock.createStrictMock(DPS.class);
//		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
//		EasyMock.expectLastCall().times(2);
//		
//		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
//		SimulationInfo secondInfo = new SimulationInfo(1, 0);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(15);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla).times(2);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(2l).times(4);
//		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(3);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(4);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(secondInfo);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(secondInfo).times(3);
//		
//		Provider provider = EasyMock.createMock(Provider.class);
//		EasyMock.expect(provider.getName()).andReturn("p1");
//		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
//		
//		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
//		PowerMock.mockStatic(WorkloadParserFactory.class);
//		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(false);
//		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(false);
//		PowerMock.replay(TimeBasedWorkloadParser.class);
//		
//		PowerMock.replayAll(config, parser, monitor, request, request2, request3, request4, request5,
//				request6, request7, request8, request9);
//		
//		JEEventScheduler scheduler = new JEEventScheduler();
//		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
//		
//		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
//		heuristic.setWorkloadParser(parser);
//		heuristic.findPlan(null, null);
//		
//		Map<MachineType, Integer> plan = heuristic.getPlan(null);
//		assertNotNull(plan);
//		assertEquals(1, plan.size());
//		int value = plan.get(MachineType.SMALL);
//		assertEquals(5 * OverProvisionHeuristic.FACTOR, value, 0.0001);
//		
//		File output = new File(Checkpointer.SIMULATION_DUMP);
//		assertTrue(output.exists());
//		
//		//Running other day in order to finish
//		output.delete();
//		heuristic = new OverProvisionHeuristic(new JEEventScheduler(), monitor, loadBalancers);
//		heuristic.setWorkloadParser(parser);
//		heuristic.findPlan(null, null);
//		
//		plan = heuristic.getPlan(null);
//		assertNotNull(plan);
//		assertEquals(1, plan.size());
//		value = plan.get(MachineType.SMALL);
//		assertEquals(5 * OverProvisionHeuristic.FACTOR, value, 0.0001);
//		
//		output = new File(Checkpointer.SIMULATION_DUMP);
//		assertFalse(output.exists());
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testFindPlanWithWorkloadWithRepeatedUsersAndDifferentNumberOfRequests() throws Exception{
//		long sla = 8000l;
//		
//		Request request = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request.getUserID()).andReturn(1);
//		Request request2 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request2.getUserID()).andReturn(2);
//		Request request3 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request3.getUserID()).andReturn(3);
//		Request request4 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request4.getUserID()).andReturn(4);
//		Request request5 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request5.getUserID()).andReturn(5);
//		Request request6 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request6.getUserID()).andReturn(5);
//		Request request7 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request7.getUserID()).andReturn(7);
//		Request request8 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request8.getUserID()).andReturn(8);
//		Request request9 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request9.getUserID()).andReturn(9);
//		
//		List<Request> firstIntervalRequests = new ArrayList<Request>();
//		firstIntervalRequests.add(request);
//		firstIntervalRequests.add(request2);
//		firstIntervalRequests.add(request3);
//		firstIntervalRequests.add(request4);
//		
//		List<Request> secondIntervalRequests = new ArrayList<Request>();
//		secondIntervalRequests.add(request5);
//		secondIntervalRequests.add(request6);
//		secondIntervalRequests.add(request7);
//		secondIntervalRequests.add(request8);
//		secondIntervalRequests.add(request9);
//		
//		DPS monitor = EasyMock.createStrictMock(DPS.class);
//		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
//		EasyMock.expectLastCall();
//		
//		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(8);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
//		
//		Provider provider = EasyMock.createMock(Provider.class);
//		EasyMock.expect(provider.getName()).andReturn("p1");
//		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
//	
//		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
//		PowerMock.mockStatic(WorkloadParserFactory.class);
//		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(false);
//		PowerMock.replay(TimeBasedWorkloadParser.class);
//		
//		PowerMock.replayAll(config, parser, monitor, request, request2, request3, request4, request5,
//				request6, request7, request8, request9);
//		
//		JEEventScheduler scheduler = new JEEventScheduler();
//		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
//		
//		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
//		heuristic.setWorkloadParser(parser);
//		heuristic.findPlan(null, null);
//		
//		Map<MachineType, Integer> plan = heuristic.getPlan(null);
//		assertNotNull(plan);
//		assertEquals(1, plan.size());
//		int value = plan.get(MachineType.SMALL);
//		assertEquals(Math.ceil(4 * OverProvisionHeuristic.FACTOR), value, 0.0001);
//		
//		File output = new File(Checkpointer.SIMULATION_DUMP);
//		assertFalse(output.exists());
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testFindPlanWithWorkloadWithRepeatedUsersAndDifferentNumberOfRequests2() throws Exception{
//		long sla = 8000l;
//		
//		Request request = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request.getUserID()).andReturn(1);
//		Request request2 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request2.getUserID()).andReturn(1);
//		Request request3 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request3.getUserID()).andReturn(1);
//		Request request4 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request4.getUserID()).andReturn(1);
//		Request request5 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request5.getUserID()).andReturn(5);
//		Request request6 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request6.getUserID()).andReturn(5);
//		Request request7 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request7.getUserID()).andReturn(5);
//		Request request8 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request8.getUserID()).andReturn(9);
//		Request request9 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request9.getUserID()).andReturn(9);
//		
//		List<Request> firstIntervalRequests = new ArrayList<Request>();
//		firstIntervalRequests.add(request);
//		firstIntervalRequests.add(request2);
//		firstIntervalRequests.add(request3);
//		firstIntervalRequests.add(request4);
//		
//		List<Request> secondIntervalRequests = new ArrayList<Request>();
//		secondIntervalRequests.add(request5);
//		secondIntervalRequests.add(request6);
//		secondIntervalRequests.add(request7);
//		secondIntervalRequests.add(request8);
//		secondIntervalRequests.add(request9);
//		
//		DPS monitor = EasyMock.createStrictMock(DPS.class);
//		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
//		EasyMock.expectLastCall();
//		
//		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(8);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
//		
//		Provider provider = EasyMock.createMock(Provider.class);
//		EasyMock.expect(provider.getName()).andReturn("p1");
//		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
//	
//		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
//		PowerMock.mockStatic(WorkloadParserFactory.class);
//		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(false);
//		PowerMock.replay(TimeBasedWorkloadParser.class);
//		
//		PowerMock.replayAll(config, parser, monitor, request, request2, request3, request4, request5,
//				request6, request7, request8, request9);
//		
//		JEEventScheduler scheduler = new JEEventScheduler();
//		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
//		
//		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
//		heuristic.setWorkloadParser(parser);
//		heuristic.findPlan(null, null);
//		
//		Map<MachineType, Integer> plan = heuristic.getPlan(null);
//		assertNotNull(plan);
//		assertEquals(1, plan.size());
//		int value = plan.get(MachineType.SMALL);
//		assertEquals(Math.ceil(2 * OverProvisionHeuristic.FACTOR), value, 0.00001);
//		
//		File output = new File(Checkpointer.SIMULATION_DUMP);
//		assertFalse(output.exists());
//		
//		PowerMock.verifyAll();
//	}
//
//	@Test
//	public void testFindPlanWithWorkloadWithUniqueUsersAndSameNumberOfRequests() throws Exception{
//		long sla = 8000l;
//		
//		Request request = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request.getUserID()).andReturn(1);
//		Request request2 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request2.getUserID()).andReturn(2);
//		Request request3 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request3.getUserID()).andReturn(3);
//		Request request4 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request4.getUserID()).andReturn(4);
//		Request request5 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request5.getUserID()).andReturn(5);
//		Request request6 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request6.getUserID()).andReturn(6);
//		Request request7 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request7.getUserID()).andReturn(7);
//		Request request8 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request8.getUserID()).andReturn(8);
//		
//		List<Request> firstIntervalRequests = new ArrayList<Request>();
//		firstIntervalRequests.add(request);
//		firstIntervalRequests.add(request2);
//		firstIntervalRequests.add(request3);
//		firstIntervalRequests.add(request4);
//		
//		List<Request> secondIntervalRequests = new ArrayList<Request>();
//		secondIntervalRequests.add(request5);
//		secondIntervalRequests.add(request6);
//		secondIntervalRequests.add(request7);
//		secondIntervalRequests.add(request8);
//		
//		DPS monitor = EasyMock.createStrictMock(DPS.class);
//		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
//		EasyMock.expectLastCall();
//		
//		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(8);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
//		
//		Provider provider = EasyMock.createMock(Provider.class);
//		EasyMock.expect(provider.getName()).andReturn("p1");
//		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
//	
//		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
//		PowerMock.mockStatic(WorkloadParserFactory.class);
//		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(false);
//		PowerMock.replay(TimeBasedWorkloadParser.class);
//		
//		PowerMock.replayAll(config, parser, monitor, request, request2, request3, request4, request5,
//				request6, request7, request8);
//		
//		JEEventScheduler scheduler = new JEEventScheduler();
//		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
//		
//		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
//		heuristic.setWorkloadParser(parser);
//		heuristic.findPlan(null, null);
//		
//		Map<MachineType, Integer> plan = heuristic.getPlan(null);
//		assertNotNull(plan);
//		assertEquals(1, plan.size());
//		int value = plan.get(MachineType.SMALL);
//		assertEquals(Math.ceil(4 * OverProvisionHeuristic.FACTOR), value, 0.0001);
//		
//		File output = new File(Checkpointer.SIMULATION_DUMP);
//		assertFalse(output.exists());
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testFindPlanWithWorkloadWithRepeatedUsersAndSameNumberOfRequests() throws Exception{
//		long sla = 8000l;
//		
//		Request request = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request.getUserID()).andReturn(1);
//		Request request2 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request2.getUserID()).andReturn(2);
//		Request request3 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request3.getUserID()).andReturn(3);
//		Request request4 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request4.getUserID()).andReturn(4);
//		Request request5 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request5.getUserID()).andReturn(5);
//		Request request6 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request6.getUserID()).andReturn(6);
//		Request request7 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request7.getUserID()).andReturn(6);
//		Request request8 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request8.getUserID()).andReturn(8);
//		
//		List<Request> firstIntervalRequests = new ArrayList<Request>();
//		firstIntervalRequests.add(request);
//		firstIntervalRequests.add(request2);
//		firstIntervalRequests.add(request3);
//		firstIntervalRequests.add(request4);
//		
//		List<Request> secondIntervalRequests = new ArrayList<Request>();
//		secondIntervalRequests.add(request5);
//		secondIntervalRequests.add(request6);
//		secondIntervalRequests.add(request7);
//		secondIntervalRequests.add(request8);
//		
//		DPS monitor = EasyMock.createStrictMock(DPS.class);
//		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
//		EasyMock.expectLastCall();
//		
//		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(8);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
//		
//		Provider provider = EasyMock.createMock(Provider.class);
//		EasyMock.expect(provider.getName()).andReturn("p1");
//		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
//	
//		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
//		PowerMock.mockStatic(WorkloadParserFactory.class);
//		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(false);
//		PowerMock.replay(TimeBasedWorkloadParser.class);
//		
//		PowerMock.replayAll(config, parser, monitor, request, request2, request3, request4, request5,
//				request6, request7, request8);
//		
//		JEEventScheduler scheduler = new JEEventScheduler();
//		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
//		
//		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
//		heuristic.setWorkloadParser(parser);
//		heuristic.findPlan(null, null);
//		
//		Map<MachineType, Integer> plan = heuristic.getPlan(null);
//		assertNotNull(plan);
//		assertEquals(1, plan.size());
//		int value = plan.get(MachineType.SMALL);
//		assertEquals(Math.ceil(4 * OverProvisionHeuristic.FACTOR), value, 0.00001);
//		
//		File output = new File(Checkpointer.SIMULATION_DUMP);
//		assertFalse(output.exists());
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testFindPlanWithWorkloadWithRepeatedUsersAndSametNumberOfRequests2() throws Exception{
//		long sla = 8000l;
//		
//		Request request = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request.getUserID()).andReturn(1);
//		Request request2 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request2.getUserID()).andReturn(2);
//		Request request3 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request3.getUserID()).andReturn(2);
//		Request request4 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request4.getUserID()).andReturn(2);
//		Request request5 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request5.getUserID()).andReturn(5);
//		Request request6 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request6.getUserID()).andReturn(5);
//		Request request7 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request7.getUserID()).andReturn(7);
//		Request request8 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request8.getUserID()).andReturn(8);
//		
//		List<Request> firstIntervalRequests = new ArrayList<Request>();
//		firstIntervalRequests.add(request);
//		firstIntervalRequests.add(request2);
//		firstIntervalRequests.add(request3);
//		firstIntervalRequests.add(request4);
//		
//		List<Request> secondIntervalRequests = new ArrayList<Request>();
//		secondIntervalRequests.add(request5);
//		secondIntervalRequests.add(request6);
//		secondIntervalRequests.add(request7);
//		secondIntervalRequests.add(request8);
//		
//		DPS monitor = EasyMock.createStrictMock(DPS.class);
//		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
//		EasyMock.expectLastCall();
//		
//		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(8);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(sla);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l).times(2);
//		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("SMALL").times(2);
//		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo).times(3);
//		
//		Provider provider = EasyMock.createMock(Provider.class);
//		EasyMock.expect(provider.getName()).andReturn("p1");
//		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
//	
//		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
//		PowerMock.mockStatic(WorkloadParserFactory.class);
//		WorkloadParserFactory.setScheduler(EasyMock.isA(JEEventScheduler.class));
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.hasNext()).andReturn(true);
//		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
//		EasyMock.expect(parser.hasNext()).andReturn(false);
//		PowerMock.replay(TimeBasedWorkloadParser.class);
//		
//		PowerMock.replayAll(config, parser, monitor, request, request2, request3, request4, request5,
//				request6, request7, request8);
//		
//		JEEventScheduler scheduler = new JEEventScheduler();
//		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
//		
//		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
//		heuristic.setWorkloadParser(parser);
//		heuristic.findPlan(null, null);
//		
//		Map<MachineType, Integer> plan = heuristic.getPlan(null);
//		assertNotNull(plan);
//		assertEquals(1, plan.size());
//		int value = plan.get(MachineType.SMALL);
//		assertEquals(Math.ceil(3 * OverProvisionHeuristic.FACTOR), value, 0.000001);
//		
//		File output = new File(Checkpointer.SIMULATION_DUMP);
//		assertFalse(output.exists());
//		
//		PowerMock.verifyAll();
//	}
}
