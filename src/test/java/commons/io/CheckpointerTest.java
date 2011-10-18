package commons.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Test;

import planning.util.MachineUsageData;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.components.LoadBalancer;
import commons.util.SimulationInfo;

public class CheckpointerTest {

	@Test
	public void testHasCheckpointTrue() throws Exception {
		File simulationDump = new File(Checkpointer.SIMULATION_DUMP);
		new FileOutputStream(simulationDump);

		assertTrue(Checkpointer.hasCheckpoint());
		simulationDump.delete();
	}

	@Test
	public void testHasCheckpointFalse() throws Exception {
		// file does not exist
		assertFalse(Checkpointer.hasCheckpoint());
	}

	@Test
	public void testSaveForSimulationInfo() throws Exception {
		File simulationInfo = new File(Checkpointer.SIMULATION_DUMP);
		simulationInfo.delete();
		assertFalse(simulationInfo.exists());

		Checkpointer.save(new SimulationInfo(2, 9), null, null, null);
		assertTrue(simulationInfo.exists());
		simulationInfo.delete();
	}

	@Test
	public void testSaveForUsers() throws Exception {
		File users = new File(Checkpointer.USERS_DUMP);
		users.delete();
		assertFalse(users.exists());

		Checkpointer.save(null, new User[] {}, null, null);
		assertTrue(users.exists());
		users.delete();
	}

	@Test
	public void testSaveForProviders() throws Exception {
		File providers = new File(Checkpointer.PROVIDERS_DUMP);
		providers.delete();
		assertFalse(providers.exists());

		Checkpointer.save(null, null, new Provider[] {}, null);
		assertTrue(providers.exists());
		providers.delete();
	}

	@Test
	public void testSaveForApplication() throws Exception {
		File loadBalancerFile = new File(Checkpointer.APPLICATION_DUMP);
		loadBalancerFile.delete();
		assertFalse(loadBalancerFile.exists());

		Checkpointer.save(null, null, null, new LoadBalancer[] {});
		assertTrue(loadBalancerFile.exists());
		loadBalancerFile.delete();
	}

	@Test
	public void testDumpMachineData() throws Exception {
		File machineData = new File(Checkpointer.MACHINE_DATA_DUMP);
		machineData.delete();
		assertFalse(machineData.exists());

		Checkpointer.dumpMachineData(new MachineUsageData());
		assertTrue(machineData.exists());
		machineData.delete();
	}

	@Test(expected = IOException.class)
	public void testLoadSimulationInfoWithoutFile() throws Exception {
		Checkpointer.loadSimulationInfo();
	}

	@Test(expected = IOException.class)
	public void testLoadSimulationInfoWithInvalidFile() throws Exception {
		File simulationInfo = new File(Checkpointer.SIMULATION_DUMP);

		String invalid = "invalid content";
		new FileWriter(simulationInfo).write(invalid);
		Checkpointer.loadSimulationInfo();
		simulationInfo.delete();
	}
	
	@Test
	public void testLoadSimulationInfoWithValidFile() throws Exception {
		SimulationInfo info = new SimulationInfo(2, 9);
		Checkpointer.save(info, null, null, null);

		assertEquals(info, Checkpointer.loadSimulationInfo());
	}
	
	@Test(expected = IOException.class)
	public void testLoadSimulationInfoPermissionDenied() throws Exception {
		File simulationInfo = new File(Checkpointer.SIMULATION_DUMP);
		
		Checkpointer.save(new SimulationInfo(2, 9), null, null, null);
		simulationInfo.setReadable(false);
		Checkpointer.loadSimulationInfo();
		simulationInfo.delete();
	}

	@Test(expected = IOException.class)
	public void testLoadApplicationWithoutFile() throws Exception {
		Checkpointer.loadApplication();
	}

	@Test(expected = IOException.class)
	public void testLoadApplicationWithInvalidFile() throws Exception {
		File loadBalancerFile = new File(Checkpointer.APPLICATION_DUMP);

		String invalid = "invalid content";
		new FileWriter(loadBalancerFile).write(invalid);
		Checkpointer.loadApplication();
		
		loadBalancerFile.delete();
	}
	
	@Test
	public void testLoadApplicationWithValidFile() throws Exception {
		File loadBalancerFile = new File(Checkpointer.APPLICATION_DUMP);
		
		LoadBalancer loadBalancer = EasyMock.createMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getTier()).andReturn(1).times(1);
		EasyMock.replay(loadBalancer);
	
		LoadBalancer[] loadBalancers = new LoadBalancer[]{loadBalancer};
		
		Checkpointer.save(null, null, null, loadBalancers);
		assertEquals(loadBalancers[0].getTier(), Checkpointer.loadApplication()[0].getTier());
		
		loadBalancerFile.delete();
		EasyMock.verify(loadBalancer);
	}
	
	@Test(expected = IOException.class)
	public void testLoadApplicationPermissionDenied() throws Exception {
		File loadBalancerFile = new File(Checkpointer.APPLICATION_DUMP);
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		Checkpointer.save(null, null, null, loadBalancers);
		loadBalancerFile.setReadable(false);
		Checkpointer.loadApplication();
		
		loadBalancerFile.delete();
	}
	
	@Test(expected = IOException.class)
	public void testLoadProvidersWithoutFile() throws Exception {
		Checkpointer.loadProviders();
	}

	@Test(expected = IOException.class)
	public void testLoadProvidersWithInvalidFile() throws Exception {
		File providersFile = new File(Checkpointer.PROVIDERS_DUMP);

		String invalid = "invalid content";
		new FileWriter(providersFile).write(invalid);
		Checkpointer.loadProviders();
		
		providersFile.delete();
	}
	
	@Test
	public void testLoadProvidersWithValidFile() throws Exception {
		File providerFile = new File(Checkpointer.PROVIDERS_DUMP);
		
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getId()).andReturn(1).times(1);
		EasyMock.replay(provider);
	
		Provider[] providers = new Provider[]{provider};
		
		Checkpointer.save(null, null, providers, null);
		assertEquals(providers[0].getId(), Checkpointer.loadProviders()[0].getId());
		
		providerFile.delete();
		EasyMock.verify(provider);
	}
	
	@Test(expected = IOException.class)
	public void testLoadProvidersPermissionDenied() throws Exception {
		File providerFile = new File(Checkpointer.PROVIDERS_DUMP);
		Provider[] providers = new Provider[]{};
		
		Checkpointer.save(null, null, providers, null);
		providerFile.setReadable(false);
		Checkpointer.loadProviders();
		
		providerFile.delete();
	}
	
	@Test(expected = IOException.class)
	public void testLoadUsersWithoutFile() throws Exception {
		Checkpointer.loadUsers();
	}

	@Test(expected = IOException.class)
	public void testLoadUsersWithInvalidFile() throws Exception {
		File usersFile = new File(Checkpointer.USERS_DUMP);

		String invalid = "invalid content";
		new FileWriter(usersFile).write(invalid);
		Checkpointer.loadProviders();
		
		usersFile.delete();
	}
	
	@Test
	public void testLoadUsersWithValidFile() throws Exception {
		File usersFile = new File(Checkpointer.USERS_DUMP);
		
		User user = EasyMock.createMock(User.class);
		EasyMock.expect(user.getId()).andReturn(1).times(1);
		EasyMock.replay(user);
	
		User[] users = new User[]{user};
		
		Checkpointer.save(null, users, null, null);
		assertEquals(users[0].getId(), Checkpointer.loadUsers()[0].getId());
		
		usersFile.delete();
		EasyMock.verify(user);
	}
	
	@Test(expected = IOException.class)
	public void testLoadUsersPermissionDenied() throws Exception {
		File userFiles = new File(Checkpointer.USERS_DUMP);
		User[] users = new User[]{};
		
		Checkpointer.save(null, users, null, null);
		userFiles.setReadable(false);
		Checkpointer.loadProviders();
		
		userFiles.delete();
	}
	
	@Test
	public void testClearAllFiles() throws Exception {
		SimulationInfo info = new SimulationInfo(2, 9);
		LoadBalancer[] application = new LoadBalancer[]{};
		Provider[] providers = new Provider[]{};
		User[] users = new User[]{};
		
		Checkpointer.save(info, users, providers, application);
		Checkpointer.dumpMachineData(new MachineUsageData());
		
		assertTrue(new File(Checkpointer.SIMULATION_DUMP).exists());
		assertTrue(new File(Checkpointer.APPLICATION_DUMP).exists());
		assertTrue(new File(Checkpointer.PROVIDERS_DUMP).exists());
		assertTrue(new File(Checkpointer.USERS_DUMP).exists());
		assertTrue(new File(Checkpointer.MACHINE_DATA_DUMP).exists());
		
		Checkpointer.clear();
		assertFalse(new File(Checkpointer.SIMULATION_DUMP).exists());
		assertFalse(new File(Checkpointer.APPLICATION_DUMP).exists());
		assertFalse(new File(Checkpointer.PROVIDERS_DUMP).exists());
		assertFalse(new File(Checkpointer.USERS_DUMP).exists());
		assertFalse(new File(Checkpointer.MACHINE_DATA_DUMP).exists());
	}

}