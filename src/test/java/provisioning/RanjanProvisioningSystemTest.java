package provisioning;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.util.WorkloadParserFactory;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.TypeProvider;
import commons.cloud.User;
import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.io.WorkloadParser;
import commons.sim.AccountingSystem;
import commons.sim.SimpleSimulator;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.provisioningheuristics.RanjanStatistics;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SaaSUsersProperties;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, WorkloadParserFactory.class})
public class RanjanProvisioningSystemTest {
	
	private RanjanProvisioningSystem dps;

	@Test
	public void testWithEmptyStatistics(){
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		try{
			this.dps.evaluateNumberOfServersForNextInterval(null);
			fail("Null statistics!");
		}catch(NullPointerException e){
		}
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This scenario verifies that an utilization that is not so much greater than the target
	 * utilization (0.66) indicates that a small number of servers should be added
	 */
	@Test
	public void testEvaluateNumberOfServersWithASingleServer(){
		//Creating simulated statistics
		double totalUtilization = 0.7;
		long totalRequestsArrivals = 10;
		long totalRequestsCompletions = 10;
		long totalNumberOfServers = 1;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added
	 */
	@Test
	public void testEvaluateNumberOfServersWithMultipleServers(){
		//Creating simulated statistics
		double totalUtilization = 3.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		long totalNumberOfServers = 3;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		assertEquals(14, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This scenario verifies that an utilization that is lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * large number of servers should be removed
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization(){
		//Creating simulated statistics
		double totalUtilization = 6;
		long totalRequestsArrivals = 333;
		long totalRequestsCompletions = 279;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		assertEquals(-9, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This scenario verifies that an utilization that is so much lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * minimal amount of servers should remain in the infrastructure
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization2(){
		//Creating simulated statistics
		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		assertEquals(-19, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This scenario verifies a scenario where no demand has occurred
	 */
	@Test
	public void testEvaluateNumberOfServersConsideringMultipleServersWithLowUtilization3(){
		//Creating simulated statistics
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 0;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		
		assertEquals(-20, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This scenario verifies a scenario where no machines were available and a high demand
	 * has arrived
	 */
	@Test
	public void testEvaluateNumberOfServersWithoutPreviousMachines(){
		//Creating simulated statistics
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 0;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		
		assertEquals(1, this.dps.evaluateNumberOfServersForNextInterval(statistics));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This scenarios verifies that after evaluating that a machine should be added, the RANJAN provisioning
	 * system creates a machine and adds it to simulator.
	 */
	@Test
	public void testEvaluateUtilisationWithOneServerToBeAdded(){
		int reservationLimit = 1;
		int onDemandLimit = 1;

		double totalUtilisation = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 0;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilisation, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		EasyMock.replay(scheduler);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.addServer(0, new MachineDescriptor(0, false, MachineType.SMALL), true);
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		ArrayList<Provider> providers = new ArrayList<Provider>();
		ArrayList<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(MachineType.SMALL, 1, 1, 100, 240, reservationLimit));
		Provider provider = new Provider("1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
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
		this.dps.evaluateUtilisation(0, statistics, 0);
		
		PowerMock.verifyAll();
		
//		assertFalse(provider.canBuyMachine(true, MachineType.SMALL));
//		assertTrue(provider.canBuyMachine(false, MachineType.SMALL));
	}
	
	@Ignore@Test
	public void testEvaluateUtilisationWithOneServerToBeAddedAndLimitsReached(){
		int reservationLimit = 1;
		int onDemandLimit = 1;

		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 0;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.replay(scheduler);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(6);
		
		ArrayList<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		EasyMock.expect(config.getStringArray(SaaSUsersProperties.SAAS_USER_WORKLOAD)).andReturn(new String[]{PropertiesTesting.VALID_WORKLOAD});
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		this.dps = new RanjanProvisioningSystem();
		AccountingSystem system = new AccountingSystem();
//		system.buyMachine();
//		system.buyMachine();
//		this.dps.setAccountingSystem(system);
		
		this.dps.registerConfigurable(configurable);

		this.dps.evaluateUtilisation(0, statistics, 0);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(scheduler, configurable, config);
	}
	
	/**
	 * This scenarios verifies that after evaluating that nineteen machines should be removed, some calls
	 * to simulator are performed.
	 */
	@Ignore@Test
	public void handleEventEvaluateUtilizationWithServersToRemove(){
		int reservationLimit = 3;
		int onDemandLimit = 3;

		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		configurable.removeServer(0, false);
		EasyMock.expectLastCall().times(19);
		
		EasyMock.replay(configurable);
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(6);
		
		ArrayList<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER)).andReturn(new int[]{0});
		EasyMock.expect(config.getStringArray(SaaSUsersProperties.SAAS_USER_WORKLOAD)).andReturn(new String[]{PropertiesTesting.VALID_WORKLOAD});
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Creating some machines to be removed
		AccountingSystem accountingSystem = new AccountingSystem();
//		accountingSystem.buyMachine();
//		accountingSystem.buyMachine();
//		accountingSystem.buyMachine();
//		accountingSystem.buyMachine();
//		accountingSystem.buyMachine();
//		accountingSystem.buyMachine();
		
		this.dps = new RanjanProvisioningSystem();
		this.dps.registerConfigurable(configurable);
//		this.dps.setAccountingSystem(accountingSystem);
		
		this.dps.evaluateUtilisation(0, statistics, 0);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(configurable, config);
	}
}