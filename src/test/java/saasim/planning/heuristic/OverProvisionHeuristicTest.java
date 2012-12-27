package saasim.planning.heuristic;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.io.TimeBasedWorkloadParser;
import saasim.io.WorkloadParser;
import saasim.io.WorkloadParserFactory;
import saasim.planning.util.PlanIOHandler;
import saasim.provisioning.DPS;
import saasim.provisioning.util.DPSFactory;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.EventScheduler;
import saasim.sim.schedulingheuristics.RoundRobinHeuristic;
import saasim.sim.util.ApplicationFactory;
import saasim.sim.util.SaaSAppProperties;
import saasim.sim.util.SimulatorProperties;
import saasim.util.MockedConfigurationTest;
import saasim.util.SimulationInfo;
import saasim.util.TimeUnit;


@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkloadParserFactory.class, Configuration.class, ApplicationFactory.class, DPSFactory.class, EventCheckpointer.class})
public class OverProvisionHeuristicTest extends MockedConfigurationTest {
	
	@Before
	public void setUp(){
		EventCheckpointer.clear();
		new File(PlanIOHandler.NUMBER_OF_MACHINES_FILE).delete();
	}
	
	@After
	public void tearDown(){
		EventCheckpointer.clear();
		new File(PlanIOHandler.NUMBER_OF_MACHINES_FILE).delete();
	}
	
	@Test
	public void testFindPlanWithWorkloadWithEmptyWorkload() throws ConfigurationException, ClassNotFoundException{

		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0, 1);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getString(SaaSAppProperties.APPLICATION_FACTORY)).andReturn("commons.sim.util.SimpleApplicationFactory");
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(new SimulationInfo(1, 0, 1));
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(new ArrayList<Request>());
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
		assertEquals(0, value);
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}

	@Test
	public void testFindPlanWithRequestsThatDoNotOverlapAndDifferentArrivalTimes() throws Exception{
		
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
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0, 1);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(new SimulationInfo(1, 0, 1));
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
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
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithRequestsThatDoNotOverlapAndSameArrivalTimes() throws Exception{

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
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0, 1);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(new SimulationInfo(1, 0, 1));
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
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
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithRequestsThatOverlapAndDifferentArrivalTimes() throws Exception{
		
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
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0, 1);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(new SimulationInfo(1, 0, 1));
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
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
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This test is similar to {@link OverProvisionHeuristicTest#testFindPlanWithRequestsThatOverlapAndDifferentArrivalTimes}. The main
	 * difference is that the overlap occurs from one minute to the other one.
	 * @throws Exception
	 */
	@Test
	public void testFindPlanWithRequestsThatOverlapAndDifferentArrivalTimes2() throws Exception{
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(57l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(50l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(180l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(82l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(300l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(59900l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(600l);
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
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0, 1);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(new SimulationInfo(1, 0, 1));
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
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
		assertEquals(1382d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(153.5556d, (Double)field.get(heuristic), 0.001d);
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testFindPlanWithRequestsThatOverlapAndSameArrivalTimes() throws Exception{
		
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
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0, 1);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(new SimulationInfo(1, 0, 1));
		EasyMock.expect(config.getParserPageSize()).andReturn(TimeUnit.MINUTE);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
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
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
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
	public void testFindPlanWithRequestsThatOverlapAndSameArrivalTimes2() throws Exception{

		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(157l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(500l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(180l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(500l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(400l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(270l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(400l);
		Request request5 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(367l);
		EasyMock.expect(request5.getTotalMeanToProcess()).andReturn(300l);
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
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		SimulationInfo simulationInfo = new SimulationInfo(0, 0, 1);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(9);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(simulationInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(new SimulationInfo(1, 0, 1));
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0);
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstIntervalRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
		assertEquals(2, value, 0.0001);
		
		//Checking some values ...
		Field field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(7, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(9l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(3170d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(352.22222222d, (Double)field.get(heuristic), 0.00001d);
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
		assertFalse(output.exists());
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This test is similar to {@link OverProvisionHeuristicTest#testFindPlanWithRequestsThatOverlapAndSameArrivalTimesAndDifferentSLA}. The
	 * main difference is that this test considers more than one day running.
	 * @throws Exception
	 */
	@Test
	public void testFindPlanWithRequestsThatOverlapAndMoreThanOneDay() throws Exception{
		
		//First day requests
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(157l);
		EasyMock.expect(request.getTotalMeanToProcess()).andReturn(500l);
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(180l);
		EasyMock.expect(request2.getTotalMeanToProcess()).andReturn(500l);
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(250l);
		EasyMock.expect(request3.getTotalMeanToProcess()).andReturn(400l);
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(270l);
		EasyMock.expect(request4.getTotalMeanToProcess()).andReturn(400l);
		Request request5 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(367l);
		EasyMock.expect(request5.getTotalMeanToProcess()).andReturn(300l);
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
		
		List<Request> firstDayRequests = new ArrayList<Request>();
		firstDayRequests.add(request);
		firstDayRequests.add(request2);
		firstDayRequests.add(request3);
		firstDayRequests.add(request4);
		firstDayRequests.add(request5);
		firstDayRequests.add(request6);
		firstDayRequests.add(request7);
		firstDayRequests.add(request8);
		firstDayRequests.add(request9);
		
		//Second day requests
		Request request10 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request10.getArrivalTimeInMillis()).andReturn(86400000+157l);
		EasyMock.expect(request10.getTotalMeanToProcess()).andReturn(200l);
		Request request11 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request11.getArrivalTimeInMillis()).andReturn(86400000+180l);
		EasyMock.expect(request11.getTotalMeanToProcess()).andReturn(82l);
		Request request12 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request12.getArrivalTimeInMillis()).andReturn(86400000+250l);
		EasyMock.expect(request12.getTotalMeanToProcess()).andReturn(300l);
		Request request13 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request13.getArrivalTimeInMillis()).andReturn(86400000+270l);
		EasyMock.expect(request13.getTotalMeanToProcess()).andReturn(40l);
		Request request14 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request14.getArrivalTimeInMillis()).andReturn(86400000+467l);
		EasyMock.expect(request14.getTotalMeanToProcess()).andReturn(50l);
		Request request15 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request15.getArrivalTimeInMillis()).andReturn(86400000+501l);
		EasyMock.expect(request15.getTotalMeanToProcess()).andReturn(60l);
		Request request16 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request16.getArrivalTimeInMillis()).andReturn(86400000+610l);
		EasyMock.expect(request16.getTotalMeanToProcess()).andReturn(700l);
		
		List<Request> secondDayRequests = new ArrayList<Request>();
		secondDayRequests.add(request10);
		secondDayRequests.add(request11);
		secondDayRequests.add(request12);
		secondDayRequests.add(request13);
		secondDayRequests.add(request14);
		secondDayRequests.add(request15);
		secondDayRequests.add(request16);
		
		//Simulation components
		DPS monitor = EasyMock.createStrictMock(DPS.class);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		EasyMock.expect(monitor.isOptimal()).andReturn(false);
		monitor.registerConfigurable(EasyMock.isA(OverProvisionHeuristic.class));
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.calculateUniqueCost()).andReturn(0d);
		EasyMock.expect(provider.getName()).andReturn("p1");
		Provider[] providers = new Provider[]{provider};
		
		SimulationInfo initialInfo = new SimulationInfo(0, 0, 2);
		SimulationInfo firstDayCompletedInfo = new SimulationInfo(1, 0, 2);
		SimulationInfo secondDayCompletedInfo = new SimulationInfo(2, 0, 2);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders");
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(12);
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(config.getBoolean(SimulatorProperties.MACHINE_SESSION_AFFINITY, false)).andReturn(false);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l).times(2);
		EasyMock.expect(config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS)).andReturn(1);
		Class<?>[] classes = new Class<?>[]{Class.forName(RoundRobinHeuristic.class.getCanonicalName())};
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(classes);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER)).andReturn(new int[]{1});
//		EasyMock.expect(config.readProviders()).andReturn(providers);
//		EasyMock.expect(config.readUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(initialInfo).times(2);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(firstDayCompletedInfo).times(3);
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(secondDayCompletedInfo);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(2l);
		EasyMock.expect(config.getString(SimulatorProperties.PLANNING_TYPE)).andReturn("M1_SMALL").times(3);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_ERROR)).andReturn(0.0).times(2);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(firstDayRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		EasyMock.expect(parser.hasNext()).andReturn(true);
		EasyMock.expect(parser.next()).andReturn(secondDayRequests);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		parser.close();
		PowerMock.replay(TimeBasedWorkloadParser.class);
		
		PowerMock.replayAll(config, parser, monitor, provider, request, request2, request3, request4, request5,
				request6, request7, request8, request9, request10, request11, request12, request13, request14, 
				request15, request16);
		
		EventCheckpointer.loadData();
		
		EventScheduler scheduler = Configuration.getInstance().getScheduler();
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		//First day
		OverProvisionHeuristic heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		Map<MachineType, Integer> plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		int value = plan.get(MachineType.M1_SMALL);
		assertEquals(2, value, 0.0001);
		
		Field field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(7, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(9l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(3170d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(352.22222222d, (Double)field.get(heuristic), 0.00001d);
		
		File output = new File(EventCheckpointer.CHECKPOINT_FILE);
		assertTrue(output.exists());
		
		//Second day
		EventCheckpointer.loadData();
		
		scheduler = Configuration.getInstance().getScheduler();
		loadBalancers = new LoadBalancer[]{};
		
		heuristic = new OverProvisionHeuristic(scheduler, monitor, loadBalancers);
		heuristic.setWorkloadParser(parser);
		heuristic.findPlan(null, null);
		
		plan = heuristic.getPlan(null);
		assertNotNull(plan);
		assertEquals(1, plan.size());
		value = plan.get(MachineType.M1_SMALL);
		assertEquals(2, value, 0.0001);
		
		//Checking some values ...
		field = OverProvisionHeuristic.class.getDeclaredField("maximumNumberOfServers");
		field.setAccessible(true);
		assertEquals(7, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("numberOfRequests");
		field.setAccessible(true);
		assertEquals(7l, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("totalProcessingTime");
		field.setAccessible(true);
		assertEquals(1432d, field.get(heuristic));
		field = OverProvisionHeuristic.class.getDeclaredField("requestsMeanDemand");
		field.setAccessible(true);
		assertEquals(278.39682539d, (Double)field.get(heuristic), 0.00001d);
		
		PowerMock.verifyAll();
	}
}
