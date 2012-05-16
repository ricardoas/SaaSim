package saasim.provisioning;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
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
import saasim.io.GEISTWorkloadParser;
import saasim.io.WorkloadParser;
import saasim.sim.AccountingSystem;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.provisioningheuristics.MachineStatistics;
import saasim.sim.util.SaaSAppProperties;
import saasim.sim.util.SimulatorProperties;
import saasim.util.SimulationInfo;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, EventCheckpointer.class})
public class OptimalProvisioningSystemForHeterogeneousMachinesTest {
	
	@Test
	public void testConstructor() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ConfigurationException{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).times(3);

		PowerMock.replayAll(config);
		
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		Field field = OptimalProvisioningSystemForHeterogeneousMachines.class.getDeclaredField("parsers");
		field.setAccessible(true);
		assertEquals(1, ((WorkloadParser[])field.get(op)).length);
		
		field = OptimalProvisioningSystemForHeterogeneousMachines.class.getDeclaredField("currentTick");
		field.setAccessible(true);
		assertEquals(new Long(1000 * 60 * 60), (Long)field.get(op));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testIsOptimal() throws ConfigurationException{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).times(3);

		PowerMock.replayAll(config);
		
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		assertTrue(op.isOptimal());
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithoutRequests() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		
		Provider provider = EasyMock.createMock(Provider.class);
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info);
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		GEISTWorkloadParser parser = EasyMock.createMock(GEISTWorkloadParser.class);
		EasyMock.expect(parser.hasNext()).andReturn(false);
		
		PowerMock.expectNew(GEISTWorkloadParser.class, PropertiesTesting.WORKLOAD).andReturn(parser);
		
		MachineStatistics statistics = EasyMock.createMock(MachineStatistics.class);
		
		PowerMock.replayAll(config, provider, user, accounting, parser, statistics);
		
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithSequentialRequestsAndReservedResources() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.ONE_SERVER_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(true).times(2);
		EasyMock.expect(provider.buyMachine(true, MachineType.M1_SMALL)).andReturn(machineDescriptor);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithSequentialRequestsAndOnDemandResourcesWithoutRisk() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.ONE_SERVER_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.T1_MICRO)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(false, MachineType.M1_SMALL)).andReturn(true).times(2);
		EasyMock.expect(provider.buyMachine(false, MachineType.M1_SMALL)).andReturn(machineDescriptor);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithSequentialRequestsAndOnDemandResourcesWithRisk() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.ONE_SERVER_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(1.0);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.T1_MICRO)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(false, MachineType.M1_SMALL)).andReturn(true);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithSequentialRequestsAndOnDemandResourcesWithtRisk2() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.ONE_SERVER_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.1);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.T1_MICRO)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(false, MachineType.M1_SMALL)).andReturn(true).times(2);
		EasyMock.expect(provider.buyMachine(false, MachineType.M1_SMALL)).andReturn(machineDescriptor);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithParallelRequestsAndReservedResources() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.FOUR_SERVERS_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(true).times(5);
		EasyMock.expect(provider.buyMachine(true, MachineType.M1_SMALL)).andReturn(machineDescriptor).times(4);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		EasyMock.expectLastCall().times(4);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithParallelRequestsAndOnDemandResourcesWithoutRisk() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.FOUR_SERVERS_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.T1_MICRO)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(false, MachineType.M1_SMALL)).andReturn(true).times(5);
		EasyMock.expect(provider.buyMachine(false, MachineType.M1_SMALL)).andReturn(machineDescriptor).times(4);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		EasyMock.expectLastCall().times(4);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithParallelRequestsAndOnDemandResourcesWithRisk() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.FOUR_SERVERS_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.5);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.T1_MICRO)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(false, MachineType.M1_SMALL)).andReturn(true).times(3);
		EasyMock.expect(provider.buyMachine(false, MachineType.M1_SMALL)).andReturn(machineDescriptor).times(2);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		EasyMock.expectLastCall().times(2);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithParallelRequestsAndMixedResourcesWithoutRisk() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.FOUR_SERVERS_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(true).times(3);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(false);
		EasyMock.expect(provider.buyMachine(true, MachineType.M1_SMALL)).andReturn(machineDescriptor).times(3);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.T1_MICRO)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(false, MachineType.M1_SMALL)).andReturn(true).times(2);
		EasyMock.expect(provider.buyMachine(false, MachineType.M1_SMALL)).andReturn(machineDescriptor);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		EasyMock.expectLastCall().times(4);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest({Configuration.class, EventCheckpointer.class, OptimalProvisioningSystemForHeterogeneousMachines.class})
	public void testSendStatisticsWithParallelRequestsAndMixedResourcesWithRisk() throws Exception{
		SimulationInfo info = new SimulationInfo(0, 0, 2);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getWorkloads()).andReturn(new String[]{PropertiesTesting.FOUR_SERVERS_WORKLOAD});
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(8000l).times(2);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.3);
		
		MachineDescriptor machineDescriptor = EasyMock.createMock(MachineDescriptor.class);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_MEDIUM)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.C1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_LARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(true).times(3);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_SMALL)).andReturn(false);
		EasyMock.expect(provider.buyMachine(true, MachineType.M1_SMALL)).andReturn(machineDescriptor).times(3);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M1_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_2XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.M2_4XLARGE)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(true, MachineType.T1_MICRO)).andReturn(false);
		EasyMock.expect(provider.canBuyMachine(false, MachineType.M1_SMALL)).andReturn(true).times(2);
		EasyMock.expect(provider.buyMachine(false, MachineType.M1_SMALL)).andReturn(machineDescriptor);
		
		SimpleMultiTierApplication configurable = EasyMock.createMock(SimpleMultiTierApplication.class);
		configurable.addMachine(0, machineDescriptor, true);
		EasyMock.expectLastCall().times(4);
		
		User user = EasyMock.createMock(User.class);
		AccountingSystem accounting = EasyMock.createMock(AccountingSystem.class);
		
		PowerMock.mockStaticPartial(EventCheckpointer.class, "loadSimulationInfo", "loadProviders", "loadUsers", "loadAccountingSystem");
		EasyMock.expect(Configuration.getInstance().getSimulationInfo()).andReturn(info).anyTimes();
		EasyMock.expect(Configuration.getInstance().getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(Configuration.getInstance().getUsers()).andReturn(new User[]{user});
		Configuration r = Configuration.getInstance();
		EasyMock.expect(new AccountingSystem(new User[]{},new Provider[]{})).andReturn(accounting);

		PowerMock.replayAll(config, provider, user, accounting, machineDescriptor, configurable);
		
		MachineStatistics statistics = new MachineStatistics(0.5, 100, 100, 0);
		OptimalProvisioningSystemForHeterogeneousMachines op = new OptimalProvisioningSystemForHeterogeneousMachines(null, null);
		op.configurable = configurable;
		op.sendStatistics(0, statistics, 0);
		
		PowerMock.verifyAll();
	}
}
