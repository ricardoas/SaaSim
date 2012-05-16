package saasim.provisioning;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.TypeProvider;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.io.WorkloadParser;
import saasim.io.WorkloadParserFactory;
import saasim.sim.DynamicConfigurable;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.provisioningheuristics.MachineStatistics;
import saasim.sim.util.SaaSAppProperties;
import saasim.util.ValidConfigurationTest;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, WorkloadParserFactory.class, EventCheckpointer.class})
public class RanjanProvisioningSystemForHeterogeneousMachinesTest extends ValidConfigurationTest {
	//FIXME Classified like a ValidConfigurationTest, but still containing use of PowerMock

	private RanjanProvisioningSystemForHeterogeneousMachines dps;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines(null, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testWithEmptyStatistics(){
		this.dps.evaluateNumberOfServersForNextInterval(null);
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
		
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
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
		
		assertEquals(14, this.dps.evaluateNumberOfServersForNextInterval(statistics));
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
		
		assertEquals(-9, this.dps.evaluateNumberOfServersForNextInterval(statistics));
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
		
		assertEquals(-19, this.dps.evaluateNumberOfServersForNextInterval(statistics));
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
		
		assertEquals(-20, this.dps.evaluateNumberOfServersForNextInterval(statistics));
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
		
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	}
	
	/**
	 * This scenarios verifies that after evaluating that a machine should be added, the RANJAN provisioning
	 * system creates a machine and adds it to simulator.
	 * @throws ConfigurationException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluateUtilisationWithOneServerToBeAdded() throws ConfigurationException{
		int reservationLimit = 1;
		int onDemandLimit = 1;

		double averageUtilisation = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 0;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, new MachineDescriptor(0, true, MachineType.C1_MEDIUM, 0), true);
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 100, 180, reservationLimit));//Machine available
		
		Provider provider = new Provider(0, "1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(0l);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		
		PowerMock.replayAll(config);
		
		//Creating a new dps
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines(null, null);
		this.dps.registerConfigurable(new DynamicConfigurable[]{configurable});
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
	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluateUtilisationWithOnDemandAndReservedServersToBeAdded() throws ConfigurationException{
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);

		int reservationLimit = 5;
		int onDemandLimit = 10;
		
		double averageUtilisation = 1.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);

		Capture<MachineDescriptor> [] descriptor = new Capture[9];
		for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = new Capture<MachineDescriptor>();
		}
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [3]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [4]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [5]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [6]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [7]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [8]), EasyMock.anyBoolean());
		EasyMock.replay(configurable);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 100, 180, 0));//On-demand machines available
		
		Provider provider = new Provider(0, "1", onDemandLimit, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(0l);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		
		PowerMock.replayAll(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		//Creating a new dps
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines(null, null);
		this.dps.registerConfigurable(new DynamicConfigurable[]{configurable});
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
	 * {@link saasim.provisioning.RanjanProvisioningSystemForHeterogeneousMachinesTest#testEvaluateUtilisationWithOnDemandAndReservedServersToBeAdded()}
	 * @throws ConfigurationException 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluateUtilisationWithOnlyReservedServersToBeAdded() throws ConfigurationException{
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		int reservationLimit = 5;
		int onDemandLimit = 10;
		
		double averageUtilisation = 1.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);

		Capture<MachineDescriptor> [] descriptor = new Capture[3];
		for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = new Capture<MachineDescriptor>();
		}
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		EasyMock.replay(configurable);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_XLARGE, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		
		Provider provider = new Provider(0, "1", onDemandLimit, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(0l);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		
		PowerMock.replayAll(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		//Creating a new dps
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines(null, null);
		this.dps.registerConfigurable(new DynamicConfigurable[]{configurable});
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
	 * {@link saasim.provisioning.RanjanProvisioningSystemForHeterogeneousMachinesTest#testEvaluateUtilisationWithOnDemandAndReservedServersToBeAdded()}
	 * @throws ConfigurationException 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluateUtilisationWithOnlyReservedServersToBeAdded2() throws ConfigurationException{
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		int reservationLimit = 5;
		int onDemandLimit = 0;
		
		double averageUtilisation = 1.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);

		Capture<MachineDescriptor> [] descriptor = new Capture[5];
		for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = new Capture<MachineDescriptor>();
		}
		configurable.setMonitor(EasyMock.isA(Monitor.class));
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [3]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [4]), EasyMock.anyBoolean());
		EasyMock.replay(configurable);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_XLARGE, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 100, 180, reservationLimit));//Reserved machines available
		
		Provider provider = new Provider(0, "1", onDemandLimit, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(0l);
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		
		PowerMock.replayAll(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		//Creating a new dps
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines(null, null);
		this.dps.registerConfigurable(new DynamicConfigurable[]{configurable});
		this.dps.sendStatistics(0, statistics, 0);
		
		for (int i = 0; i < 3; i++) {
			assertEquals(MachineType.M1_XLARGE, descriptor[i].getValue().getType());
			assertTrue(descriptor[i].getValue().isReserved());
		}

		PowerMock.verifyAll();
	}
	
	@Test
	public void testReportQueuedRequest() throws ConfigurationException{
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(0).times(2);
		
		//Configuration mock
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{});
		
		User user = EasyMock.createStrictMock(User.class);
		user.reportLostRequest(request);
		User[] users = new User[1];
		users[0] = user;
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(users);
		
		PowerMock.replayAll(request, user);
		
		//Creating dps
		this.dps = new RanjanProvisioningSystemForHeterogeneousMachines(null, null);
		this.dps.requestQueued(0, request, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testReportFinishedRequest() throws ConfigurationException{
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(0);
		EasyMock.expect(request.getResponseTimeInMillis()).andReturn(0l);
		EasyMock.expect(request.getSaasClient()).andReturn(0);
		
		//Configuration mock
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadProviders", "loadUsers");
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{});
		
		User user = EasyMock.createStrictMock(User.class);
		user.reportFinishedRequest(request);
		User[] users = new User[1];
		users[0] = user;
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(users);
		
		PowerMock.replayAll(request, user);
		
		//Creating dps
		RanjanProvisioningSystemForHeterogeneousMachines dps = new RanjanProvisioningSystemForHeterogeneousMachines(null, null);
		dps.requestFinished(request);
		
		PowerMock.verifyAll();
	}
}
