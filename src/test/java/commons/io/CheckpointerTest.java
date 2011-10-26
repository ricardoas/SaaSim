package commons.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import planning.util.MachineUsageData;
import util.ValidConfigurationTest;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.AccountingSystem;
import commons.sim.Simulator;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.SimulationInfo;

public class CheckpointerTest extends ValidConfigurationTest{
	
	private static final File machineDataFile = new File(Checkpointer.MACHINE_DATA_DUMP);
	private static final File file = new File(Checkpointer.CHECKPOINT_FILE);
	
	@Override
	@Before
	public void setUp() throws Exception{
		super.setUp();
		assert !file.exists() || file.delete();
		assert !machineDataFile.exists() || machineDataFile.delete();
	}

	@AfterClass
	public static void tearDown(){
		assert !file.exists() || file.delete();
		assert !machineDataFile.exists() || machineDataFile.delete();
	}
	
	@Test
	public void testHasCheckpointTrue() throws IOException {
		assert file.createNewFile();

		assertTrue(Checkpointer.hasCheckpoint());
	}

	@Test
	public void testHasCheckpointFalse() {
		assertFalse(Checkpointer.hasCheckpoint());
	}

	@Test
	public void testHasCheckpointWithUnreadableFile() throws IOException {
		assert file.createNewFile();
		assert file.setWritable(false);
		
		assertFalse(Checkpointer.hasCheckpoint());
	}

	@Test
	public void testSaveOnlySimulationInfo() {
		file.delete();
		assertFalse(file.exists());

		Checkpointer.loadScheduler();
		Checkpointer.save(new SimulationInfo(2, 9), null, null, null);
		assertTrue(Checkpointer.hasCheckpoint());
	}

	@Test
	public void testSaveOnlyUsers() {
		file.delete();
		assertFalse(file.exists());

		Checkpointer.loadScheduler();
		Checkpointer.save(null, new User[] {}, null, null);
		assertTrue(file.exists());
	}

	@Test
	public void testSaveOnlyProviders() {
		file.delete();
		assertFalse(file.exists());

		Checkpointer.save(null, null, new Provider[] {}, null);
		assertTrue(file.exists());
	}

	@Test
	public void testSaveOnlyApplication() {
		file.delete();
		assertFalse(file.exists());

		Checkpointer.save(null, null, null, new LoadBalancer[] {});
		assertTrue(file.exists());
	}

	@Test
	public void testDumpMachineData() throws IOException {
		assert !machineDataFile.exists() || machineDataFile.delete();

		Checkpointer.dumpMachineData(new MachineUsageData());
		assertTrue(machineDataFile.exists());
	}

	@Test(expected=ConfigurationRuntimeException.class)
	public void testLoadDataWithoutFile() throws ConfigurationException {
		assert !file.exists();
		Checkpointer.loadData();
	}

	@Test
	public void testLoadDataWithInvalidFile() throws IOException, ConfigurationException {
		String invalid = "invalid content";
		FileWriter writer = new FileWriter(file);
		writer.write(invalid);
		writer.close();
		try{
			Checkpointer.loadData();
			fail("Should fail on loading.");
		}catch(RuntimeException e){
			/* catch exception */
		}
	}
	
	/**
	 * FIXME No smart implementation for {@link AccountingSystem#equals(Object)}
	 * @throws ConfigurationException
	 */
	@Test
	public void testLoadDataWithValidFile() throws ConfigurationException {
		
		buildFullConfiguration();
//		AccountingSystem accountingSystemBefore = Checkpointer.loadAccountingSystem();
		Simulator applicationBefore = Checkpointer.loadApplication();
		Provider[] providersBefore = Checkpointer.loadProviders();
		JEEventScheduler schedulerBefore = Checkpointer.loadScheduler();
		SimulationInfo simulationInfoBefore = Checkpointer.loadSimulationInfo();
		User[] usersBefore = Checkpointer.loadUsers();
		Checkpointer.save();
		
		buildFullConfiguration();
		
		simulationInfoBefore.addDay();
		
//		assertEquals(accountingSystemBefore, Checkpointer.loadAccountingSystem());
		assertEquals(applicationBefore, Checkpointer.loadApplication());
		assertArrayEquals(providersBefore, Checkpointer.loadProviders());
		assertEquals(schedulerBefore, Checkpointer.loadScheduler());
		assertEquals(simulationInfoBefore, Checkpointer.loadSimulationInfo());
		assertArrayEquals(usersBefore, Checkpointer.loadUsers());
	}
	
	@Test
	public void testLoadDataWithPermissionDenied() throws IOException {
		assert file.createNewFile() && file.setReadable(false);
		
		Checkpointer.loadApplication();
	}
	
	@Test
	public void testClearAllFiles() throws IOException {
		assert file.createNewFile();
		assert machineDataFile.createNewFile();
		
		Checkpointer.clear();
		
		assertFalse(file.exists());
		assertFalse(machineDataFile.exists());
	}
}
