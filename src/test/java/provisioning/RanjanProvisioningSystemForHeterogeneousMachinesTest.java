package provisioning;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import util.MockedConfigurationTest;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.TypeProvider;
import commons.cloud.User;
import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.io.Checkpointer;
import commons.io.WorkloadParser;
import commons.io.WorkloadParserFactory;
import commons.sim.SimpleSimulator;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, WorkloadParserFactory.class, Checkpointer.class})
public class RanjanProvisioningSystemForHeterogeneousMachinesTest extends MockedConfigurationTest {

	private RanjanProvisioningSystemForHeterogeneousMachines dps;

	@Test(expected=NullPointerException.class)
	public void testWithEmptyStatistics(){
		//Mocks
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		
		PowerMock.replayAll();
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		this.dps.evaluateNumberOfServersForNextInterval(null);
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies that an utilization that is not so much greater than the target
	 * utilization (0.66) indicates that a small number of servers should be added
	 */
	@Test
	public void testEvaluateNumberOfServersWithASingleServer(){
		//Creating simulated statistics
		double averageUtilisation = 0.7;
		long totalRequestsArrivals = 10;
		long totalRequestsCompletions = 10;
		int totalNumberOfServers = 1;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		
		PowerMock.replayAll();
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added
	 */
	@Test
	public void testEvaluateNumberOfServersWithMultipleServers(){
		//Creating simulated statistics
		double averageUtilisation = 1.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		
		PowerMock.replayAll();
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		assertEquals(14, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies that an utilization that is lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * large number of servers should be removed
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization(){
		//Creating simulated statistics
		double averageUtilisation = 0.3;
		long totalRequestsArrivals = 333;
		long totalRequestsCompletions = 279;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});

		PowerMock.replayAll();
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		assertEquals(-9, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies that an utilization that is so much lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * minimal amount of servers should remain in the infrastructure
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization2(){
		//Creating simulated statistics
		double averageUtilisation = 0.01;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		
		PowerMock.replayAll();
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		assertEquals(-19, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies a scenario where no demand has occurred
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization3(){
		//Creating simulated statistics
		double averageUtilisation = 0.0;
		long totalRequestsArrivals = 0;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		
		PowerMock.replayAll();
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		
		assertEquals(-20, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies a scenario where no machines were available and a high demand
	 * has arrived
	 */
	@Test
	public void testEvaluateNumberOfServersWithoutMachinesInLastTurn(){
		//Creating simulated statistics
		double averageUtilisation = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 0;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		
		PowerMock.replayAll();		
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenarios verifies that after evaluating that a machine should be added, the RANJAN provisioning
	 * system creates a machine and adds it to simulator.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testEvaluateUtilisationWithOneServerToBeAdded() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_SINGLE_WORKLOAD_FILE);
		
		int reservationLimit = 1;
		int onDemandLimit = 1;

		double averageUtilisation = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 0;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		EasyMock.replay(scheduler);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.addServer(0, new MachineDescriptor(0, true, MachineType.C1_MEDIUM, 0), true);
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 100, 180, reservationLimit));//Machine available
		
		Provider provider = new Provider(0, "1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(providers);
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		
		PowerMock.replayAll(config, parser);
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		this.dps.registerConfigurable(configurable);
		this.dps.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
		
		assertFalse(provider.canBuyMachine(true, MachineType.M1_SMALL));
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added. Different machine types exist in the provider in
	 * order to supply the demand.
	 * @throws ConfigurationException 
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateUtilisationWithOnDemandAndReservedServersToBeAdded() throws ConfigurationException{

		//Configuration mock
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);

		int reservationLimit = 5;
		int onDemandLimit = 10;
		
		double averageUtilisation = 1.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;

		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.replay(scheduler);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);

		Capture<MachineDescriptor> [] descriptor = new Capture[9];
		for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = new Capture<MachineDescriptor>();
		}
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [3]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [4]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [5]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [6]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [7]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [8]), EasyMock.anyBoolean());
		EasyMock.replay(configurable);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 100, 180, 0));//On-demand machines available
		
		Provider provider = new Provider(0, "1", onDemandLimit, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(providers);
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
//		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d).times(10);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_SMALL)).andReturn(1d).times(10);
		
		PowerMock.replayAll(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		this.dps.registerConfigurable(configurable);

		this.dps.sendStatistics(0, statistics, 0);
		
		for (int i = 0; i < 5; i++) {
			assertEquals(MachineType.C1_MEDIUM, descriptor[i].getValue().getType());
			assertTrue(descriptor[i].getValue().isReserved());
		}
		for (int i = 5; i < 9; i++) {
			assertEquals(MachineType.M1_SMALL, descriptor[i].getValue().getType());
			assertFalse(descriptor[i].getValue().isReserved());
		}
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added. The reserved machine type supplies all the demand
	 * with a lower number of machines than the number of machines purchased at 
	 * {@link provisioning.RanjanProvisioningSystemForHeterogeneousMachinesTest#testEvaluateUtilisationWithOnDemandAndReservedServersToBeAdded()}
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateUtilisationWithOnlyReservedServersToBeAdded(){
		//Configuration mock
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		
		int reservationLimit = 5;
		int onDemandLimit = 10;
		
		double averageUtilisation = 1.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;

		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.replay(scheduler);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);

		Capture<MachineDescriptor> [] descriptor = new Capture[3];
		for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = new Capture<MachineDescriptor>();
		}
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		EasyMock.replay(configurable);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_XLARGE, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		
		Provider provider = new Provider(0, "1", onDemandLimit, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(providers);
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
//		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(4d).times(8);
		
		PowerMock.replayAll(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		this.dps.registerConfigurable(configurable);

		this.dps.sendStatistics(0, statistics, 0);
		
		for (int i = 0; i < 3; i++) {
			assertEquals(MachineType.M1_XLARGE, descriptor[i].getValue().getType());
			assertTrue(descriptor[i].getValue().isReserved());
		}

		PowerMock.verifyAll();
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added. The reserved machine type supplies all the demand
	 * with a lower number of machines than the number of machines purchased at 
	 * {@link provisioning.RanjanProvisioningSystemForHeterogeneousMachinesTest#testEvaluateUtilisationWithOnDemandAndReservedServersToBeAdded()}
	 * @throws Exception 
	 */
	@Test
	public void testEvaluateUtilisationWithOnlyReservedServersToBeAdded2(){
		//Configuration mock
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		
		int reservationLimit = 5;
		int onDemandLimit = 10;
		
		double averageUtilisation = 1.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;

		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.replay(scheduler);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);

		Capture<MachineDescriptor> [] descriptor = new Capture[5];
		for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = new Capture<MachineDescriptor>();
		}
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [3]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [4]), EasyMock.anyBoolean());
		EasyMock.replay(configurable);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_XLARGE, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		
		Provider provider = new Provider(0, "1", 0, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(providers);
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
//		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(4d).times(7);
//		EasyMock.expect(config.getRelativePower(MachineType.M1_SMALL)).andReturn(1d).times(5);
		
		PowerMock.replayAll(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		this.dps.registerConfigurable(configurable);

		this.dps.sendStatistics(0, statistics, 0);
		
		for (int i = 0; i < 3; i++) {
			assertEquals(MachineType.M1_XLARGE, descriptor[i].getValue().getType());
			assertTrue(descriptor[i].getValue().isReserved());
		}

		PowerMock.verifyAll();
	}
	
	/**
	 * This scenarios verifies that after evaluating that nineteen machines should be removed, some calls
	 * to simulator are performed.
	 * @throws ConfigurationException 
	 * @throws Exception 
	 */
	@Test
	public void handleEvaluateUtilisationWithServersToRemove() throws ConfigurationException{

		Configuration.buildInstance(PropertiesTesting.VALID_SINGLE_WORKLOAD_FILE);

		int reservationLimit = 3;
		int onDemandLimit = 10;

		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.removeServer(0, false);
		EasyMock.expectLastCall().times(19);
		
		EasyMock.replay(configurable);
		
		//Configuration mock
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.1, 0.05, 100, 180, 0));//Reserved machines available
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 100, 180, 0));//On-demand machines available
		
		Provider provider = new Provider(0, "1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(providers);
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		
		PowerMock.replayAll(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		//Creating dps
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		this.dps.registerConfigurable(configurable);
		
		//Buying some on-demand machines
		this.dps.buyMachine(provider, MachineType.M1_SMALL, false);
		this.dps.buyMachine(provider, MachineType.M1_SMALL, false);
		this.dps.buyMachine(provider, MachineType.M1_SMALL, false);
		this.dps.buyMachine(provider, MachineType.M1_SMALL, false);
		this.dps.buyMachine(provider, MachineType.M1_SMALL, false);
		this.dps.buyMachine(provider, MachineType.M1_SMALL, false);
		this.dps.buyMachine(provider, MachineType.C1_MEDIUM, false);
		this.dps.buyMachine(provider, MachineType.C1_MEDIUM, false);
		this.dps.buyMachine(provider, MachineType.C1_MEDIUM, false);
		this.dps.buyMachine(provider, MachineType.C1_MEDIUM, false);
		
		this.dps.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testReportQueuedRequest(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(0).times(2);
		
		Contract contract = EasyMock.createStrictMock(Contract.class);

		//Configuration mock
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		
		User user = EasyMock.createStrictMock(User.class);
		user.reportLostRequest(request);
		User[] users = new User[1];
		users[0] = user;
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(users);
		
		PowerMock.replayAll(request, contract, user);
		
		//Creating dps
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		this.dps.requestQueued(0, request, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testReportFinishedRequest(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(0).times(2);
		
		Contract contract = EasyMock.createStrictMock(Contract.class);

		//Configuration mock
		PowerMock.mockStaticPartial(Checkpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Checkpointer.loadProviders()).andReturn(new Provider[]{});
		
		User user = EasyMock.createStrictMock(User.class);
		user.reportFinishedRequest(request);
		User[] users = new User[1];
		users[0] = user;
		EasyMock.expect(Checkpointer.loadUsers()).andReturn(users);
		
		PowerMock.replayAll(request, contract, user);
		
		//Creating dps
		RanjanProvisioningSystemForHeterogeneousMachines dps = new RanjanProvisioningSystemForHeterogeneousMachines();
		dps.requestFinished(request);
		
		PowerMock.verifyAll();
	}
}
