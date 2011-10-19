package commons.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

import planning.util.MachineUsageData;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.SimulationInfo;

public class CheckpointerTest {

	@After
	public void tearDown(){
		Checkpointer.clear();
	}
	
	@Test
	public void testHasCheckpointTrue() throws FileNotFoundException {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		new FileOutputStream(checkpoint);

		assertTrue(Checkpointer.hasCheckpoint());
	}

	@Test
	public void testHasCheckpointFalse() {
		// file does not exist
		assertFalse(Checkpointer.hasCheckpoint());
	}

	@Test
	public void testSaveOnlySimulationInfo() throws Exception {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		checkpoint.delete();
		assertFalse(checkpoint.exists());

		JEEventScheduler.getInstance();
		Checkpointer.save(new SimulationInfo(2, 9), null, null, null);
		assertTrue(checkpoint.exists());
	}

	@Test
	public void testSaveOnlyUsers() throws Exception {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		checkpoint.delete();
		assertFalse(checkpoint.exists());

		JEEventScheduler.getInstance();
		Checkpointer.save(null, new User[] {}, null, null);
		assertTrue(checkpoint.exists());
	}

	@Test
	public void testSaveOnlyProviders() throws Exception {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		checkpoint.delete();
		assertFalse(checkpoint.exists());

		Checkpointer.save(null, null, new Provider[] {}, null);
		assertTrue(checkpoint.exists());
	}

	@Test
	public void testSaveOnlyApplication() throws Exception {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		checkpoint.delete();
		assertFalse(checkpoint.exists());

		Checkpointer.save(null, null, null, new LoadBalancer[] {});
		assertTrue(checkpoint.exists());
	}

	@Test
	public void testDumpMachineData() throws IOException {
		File machineData = new File(Checkpointer.MACHINE_DATA_DUMP);
		machineData.delete();
		assertFalse(machineData.exists());

		Checkpointer.dumpMachineData(new MachineUsageData());
		assertTrue(machineData.exists());
		machineData.delete();
	}

	@Test
	public void testLoadSimulationInfoWithoutData() throws Exception {
		assertNull(Checkpointer.loadSimulationInfo());
		
	}

	@Test(expected = RuntimeException.class)
	public void testLoadSimulationInfoWithInvalidFile() throws IOException {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);

		String invalid = "invalid content";
		new FileWriter(checkpoint).write(invalid);
		Checkpointer.loadSimulationInfo();
	}
	
	@Test
	public void testLoadSimulationInfoWithValidFile() {
		SimulationInfo info = new SimulationInfo(2, 9);
		Checkpointer.save(info, null, null, null);

		assertEquals(info, Checkpointer.loadSimulationInfo());
		
		Checkpointer.clear();
	}
	
	@Test(expected=RuntimeException.class)
	public void testLoadSimulationInfoPermissionDenied() throws Exception {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		
		Checkpointer.save(new SimulationInfo(2, 9), null, null, null);
		checkpoint.setReadable(false);
		Checkpointer.loadSimulationInfo();
	}

	@Test
	public void testLoadApplicationWithoutFile() {
		assertNull(Checkpointer.loadApplication());
	}

	@Test(expected = RuntimeException.class)
	public void testLoadApplicationWithInvalidFile() throws IOException {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);

		String invalid = "invalid content";
		new FileWriter(checkpoint).write(invalid);
		Checkpointer.loadApplication();
	}
	
	@Test
	public void testLoadApplicationWithValidFile() {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		checkpoint.setReadable(true);
		
		LoadBalancer loadBalancer = EasyMock.createMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getTier()).andReturn(1).times(1);
		EasyMock.replay(loadBalancer);
	
		LoadBalancer[] loadBalancers = new LoadBalancer[]{loadBalancer};
		
		Checkpointer.save(null, null, null, loadBalancers);
		assertEquals(loadBalancers[0].getTier(), Checkpointer.loadApplication()[0].getTier());
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test(expected = RuntimeException.class)
	public void testLoadApplicationWithPermissionDenied() {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		LoadBalancer[] loadBalancers = new LoadBalancer[]{};
		
		Checkpointer.save(null, null, null, loadBalancers);
		checkpoint.setReadable(false);
		Checkpointer.loadApplication();
	}
	
	@Test
	public void testLoadProvidersWithoutFile() {
		assertNull(Checkpointer.loadProviders());
	}

	@Test(expected = RuntimeException.class)
	public void testLoadProvidersWithInvalidFile() throws IOException {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);

		String invalid = "invalid content";
		new FileWriter(checkpoint).write(invalid);
		Checkpointer.loadProviders();
	}
	
	@Test
	public void testLoadProvidersWithValidFile() throws Exception {
		Provider provider = EasyMock.createMock(Provider.class);
		EasyMock.expect(provider.getId()).andReturn(1).times(1);
		EasyMock.replay(provider);
	
		Provider[] providers = new Provider[]{provider};
		
		Checkpointer.save(null, null, providers, null);
		assertEquals(providers[0].getId(), Checkpointer.loadProviders()[0].getId());

		EasyMock.verify(provider);
	}
	
	@Test(expected = RuntimeException.class)
	public void testLoadProvidersPermissionDenied() {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);
		Provider[] providers = new Provider[]{};
		
		Checkpointer.save(null, null, providers, null);
		checkpoint.setReadable(false);
		Checkpointer.loadProviders();
	}
	
	@Test
	public void testLoadUsersWithoutFile() {
		assertNull(Checkpointer.loadUsers());
	}

	@Test(expected = RuntimeException.class)
	public void testLoadUsersWithInvalidFile() throws Exception {
		File checkpoint = new File(Checkpointer.CHECKPOINT_FILE);

		String invalid = "invalid content";
		new FileWriter(checkpoint).write(invalid);
		Checkpointer.loadProviders();
	}
	
	@Test
	public void testLoadUsersWithValidFile() throws Exception {
		User user = EasyMock.createMock(User.class);
		EasyMock.expect(user.getId()).andReturn(1).times(1);
		EasyMock.replay(user);
	
		User[] users = new User[]{user};
		
		Checkpointer.save(null, users, null, null);
		assertEquals(users[0].getId(), Checkpointer.loadUsers()[0].getId());
		
		EasyMock.verify(user);
	}
	
	@Test(expected = RuntimeException.class)
	public void testLoadUsersPermissionDenied() throws Exception {
		File checkpointFile = new File(Checkpointer.CHECKPOINT_FILE);
		User[] users = new User[]{};
		
		Checkpointer.save(null, users, null, null);
		checkpointFile.setReadable(false);
		Checkpointer.loadProviders();
	}
	
	@Test
	public void testClearAllFiles() throws IOException {
		SimulationInfo info = new SimulationInfo(2, 9);
		LoadBalancer[] application = new LoadBalancer[]{};
		Provider[] providers = new Provider[]{};
		User[] users = new User[]{};
		
		Checkpointer.save(info, users, providers, application);
		Checkpointer.dumpMachineData(new MachineUsageData());
		
		assertTrue(new File(Checkpointer.CHECKPOINT_FILE).exists());
		assertTrue(new File(Checkpointer.MACHINE_DATA_DUMP).exists());
		
		Checkpointer.clear();
		assertFalse(new File(Checkpointer.CHECKPOINT_FILE).exists());
		assertFalse(new File(Checkpointer.MACHINE_DATA_DUMP).exists());
	}
}
