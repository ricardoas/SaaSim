package saasim.provisioning;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.TypeProvider;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.config.PropertiesTesting;
import saasim.io.WorkloadParser;
import saasim.io.WorkloadParserFactory;
import saasim.provisioning.RanjanProvisioningSystem;
import saasim.sim.SimpleSimulator;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.jeevent.JECheckpointer;
import saasim.sim.jeevent.JEEventScheduler;
import saasim.sim.provisioningheuristics.MachineStatistics;
import saasim.sim.util.SaaSAppProperties;
import saasim.sim.util.SaaSUsersProperties;
import saasim.util.ValidConfigurationTest;


public class RanjanProvisioningSystemTest extends ValidConfigurationTest {
	private RanjanProvisioningSystem dps;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullRanjanConfiguration();
		this.dps = new RanjanProvisioningSystem();
	}
	
	@Test(expected=NullPointerException.class)
	public void testWithEmptyStatistics() {
		this.dps.evaluateNumberOfServersForNextInterval(null);
	}
	
	/**
	 * This scenario verifies that an utilization that is not so much greater than the target
	 * utilization (0.66) indicates that a small number of servers should be added
	 */
	@Test
	public void testEvaluateNumberOfServersWithASingleServer() {
		//Creating simulated statistics
		double totalUtilization = 0.7;
		long totalRequestsArrivals = 10;
		long totalRequestsCompletions = 10;
		int totalNumberOfServers = 1;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added 
	 */
	@Test
	public void testEvaluateNumberOfServersWithMultipleServers() {
		//Creating simulated statistics
		double totalUtilization = 3.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		int totalNumberOfServers = 3;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		assertEquals(47, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	}
	
	/**
	 * This scenario verifies that an utilization that is lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * large number of servers should be removed
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization() {
		//Creating simulated statistics
		double totalUtilization = 6;
		long totalRequestsArrivals = 333;
		long totalRequestsCompletions = 279;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);

		assertEquals(198, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	}
	
	/**
	 * This scenario verifies that an utilization that is so much lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * minimal amount of servers should remain in the infrastructure
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization2() {
		//Creating simulated statistics
		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);

		assertEquals(-13, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	}
	
	/**
	 * This scenario verifies a scenario where no demand has occurred. In this case, 
	 * an AssertionError occurs, because isn't possible the values of number of 
	 * servers and/or total requests completions are zero.
	 */
	@Test(expected=AssertionError.class)
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization3() {
		//Creating simulated statistics
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 0;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);

		this.dps.evaluateNumberOfServersForNextInterval(statistics);
	}
	
	/**
	 * This scenario verifies a scenario where no machines were available and a high demand
	 * has arrived. In this case, an AssertionError occurs, because isn't possible the values 
	 * of number of servers and/or total requests completions are zero.
	 */
	@Test(expected=AssertionError.class)
	public void testEvaluateNumberOfServersWithoutPreviousMachines() {
		//Creating simulated statistics
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 0;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
	}
	
	/**
	 * This scenarios verifies that after evaluating that a machine should be added, the RANJAN provisioning
	 * system creates a machine and adds it to simulator.
	 * @throws ConfigurationException 
	 */
	@SuppressWarnings("unchecked")
	@Ignore @Test
	public void testEvaluateUtilisationWithOneServerToBeAdded() throws ConfigurationException{
		//Configuration.buildInstance(PropertiesTesting.VALID_SINGLE_WORKLOAD_FILE);
		int reservationLimit = 1;
		int onDemandLimit = 1;

		double totalUtilisation = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 0;
		MachineStatistics statistics = new MachineStatistics(totalUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleSimulator configurable = EasyMock.createStrictMock(SimpleSimulator.class);
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.addMachine(0, new MachineDescriptor(0, false, MachineType.M1_SMALL, 0), true);
		
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		Provider[] providers = new Provider[1];
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 1, 1, 100, 240, reservationLimit));
		
		Provider provider = new Provider(0, "1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers[0] = provider;
		
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		WorkloadParser<List<Request>> parser = EasyMock.createStrictMock(WorkloadParser.class);
		PowerMock.mockStatic(WorkloadParserFactory.class);
		EasyMock.expect(WorkloadParserFactory.getWorkloadParser()).andReturn(parser);
		PowerMock.replay(WorkloadParserFactory.class);
		EasyMock.replay(parser);
		
		this.dps = new RanjanProvisioningSystem();
		this.dps.registerConfigurable(configurable);
		this.dps.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
		//assertFalse(provider.canBuyMachine(true, MachineType.SMALL));		
		//assertTrue(provider.canBuyMachine(false, MachineType.SMALL));
	}
	
	@SuppressWarnings("unchecked")
	@Ignore @Test
	public void testEvaluateUtilisationWithOneServerToBeAddedAndLimitsReached(){
		int reservationLimit = 1;
		int onDemandLimit = 1;

		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		int totalNumberOfServers = 0;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.replay(scheduler);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(6);
		
		Provider[] providers = new Provider[1];
		Provider provider = new Provider(0, "1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers[0] = provider;
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		EasyMock.expect(config.getStringArray(SaaSUsersProperties.SAAS_USER_WORKLOAD)).andReturn(new String[]{PropertiesTesting.WORKLOAD});
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		//AccountingSystem system = new AccountingSystem(0, 1);
		//system.buyMachine();
		//system.buyMachine();
		//this.dps.setAccountingSystem(system);
		
		this.dps.registerConfigurable(configurable);
		this.dps.sendStatistics(0, statistics, 0);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(scheduler, configurable, config);
	}
	
	/**
	 * This scenarios verifies that after evaluating that nineteen machines should be removed, some calls
	 * to simulator are performed.
	 */
	@SuppressWarnings("unchecked")
	@Ignore @Test
	public void handleEventEvaluateUtilizationWithServersToRemove(){
		int reservationLimit = 3;
		int onDemandLimit = 3;

		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		int totalNumberOfServers = 20;
		MachineStatistics statistics = new MachineStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.removeMachine(0, false);
		EasyMock.expectLastCall().times(19);
		
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(6);
		
		Provider[] providers = new Provider[1];
		Provider provider = new Provider(0, "1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers[0] = provider;
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(providers);
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{});
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		EasyMock.expect(config.getStringArray(SaaSUsersProperties.SAAS_USER_WORKLOAD)).andReturn(new String[]{PropertiesTesting.WORKLOAD});
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Creating some machines to be removed
		//AccountingSystem accountingSystem = new AccountingSystem(0, 1);
		//accountingSystem.buyMachine();
		//accountingSystem.buyMachine();
		//accountingSystem.buyMachine();
		//accountingSystem.buyMachine();
		//accountingSystem.buyMachine();
		//accountingSystem.buyMachine();
		
		this.dps = new RanjanProvisioningSystem();
		this.dps.registerConfigurable(configurable);
		//this.dps.setAccountingSystem(accountingSystem);
		this.dps.sendStatistics(0, statistics, 0);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(configurable, config);
	}
}