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
import commons.config.Configuration;
import commons.sim.AccountingSystem;
import commons.sim.Simulator;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JECheckpointer;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.SimulationInfo;

public class CheckpointerTest extends ValidConfigurationTest{
	
	private static final File machineDataFile = new File(JECheckpointer.MACHINE_DATA_DUMP);
	private static final File file = new File(JECheckpointer.CHECKPOINT_FILE);
	
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

		assertTrue(JECheckpointer.hasCheckpoint());
	}

	@Test
	public void testHasCheckpointFalse() {
		assertFalse(JECheckpointer.hasCheckpoint());
	}

	@Test
	public void testHasCheckpointWithUnreadableFile() throws IOException {
		assert file.createNewFile();
		assert file.setWritable(false);
		
		assertFalse(JECheckpointer.hasCheckpoint());
	}

	@Test
	public void testSaveOnlySimulationInfo() {
		file.delete();
		assertFalse(file.exists());

		Configuration.getInstance().getScheduler();
		JECheckpointer.save(new SimulationInfo(2, 9), null, null, null);
		assertTrue(JECheckpointer.hasCheckpoint());
	}

	@Test
	public void testSaveOnlyUsers() {
		file.delete();
		assertFalse(file.exists());

		Configuration.getInstance().getScheduler();
		JECheckpointer.save(null, new User[] {}, null, null);
		assertTrue(file.exists());
	}

	@Test
	public void testSaveOnlyProviders() {
		file.delete();
		assertFalse(file.exists());

		JECheckpointer.save(null, null, new Provider[] {}, null);
		assertTrue(file.exists());
	}

	@Test
	public void testSaveOnlyApplication() {
		file.delete();
		assertFalse(file.exists());

		JECheckpointer.save(null, null, null, new LoadBalancer[] {});
		assertTrue(file.exists());
	}

	@Test
	public void testDumpMachineData() throws IOException {
		assert !machineDataFile.exists() || machineDataFile.delete();

		JECheckpointer.dumpMachineData(new MachineUsageData());
		assertTrue(machineDataFile.exists());
	}

	@Test(expected=ConfigurationRuntimeException.class)
	public void testLoadDataWithoutFile() throws ConfigurationException {
		assert !file.exists();
		JECheckpointer.loadData();
	}

	@Test
	public void testLoadDataWithInvalidFile() throws IOException, ConfigurationException {
		String invalid = "invalid content";
		FileWriter writer = new FileWriter(file);
		writer.write(invalid);
		writer.close();
		try{
			JECheckpointer.loadData();
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
		Simulator applicationBefore = Configuration.getInstance().getApplication();
		Provider[] providersBefore = Configuration.getInstance().getProviders();
		JEEventScheduler schedulerBefore = Configuration.getInstance().getScheduler();
		SimulationInfo simulationInfoBefore = Configuration.getInstance().getSimulationInfo();
		User[] usersBefore = Configuration.getInstance().getUsers();
		JECheckpointer.save();
		
		buildFullConfiguration();
		
		simulationInfoBefore.addDay();
		
//		assertEquals(accountingSystemBefore, Checkpointer.loadAccountingSystem());
		assertEquals(applicationBefore, Configuration.getInstance().getApplication());
		assertArrayEquals(providersBefore, Configuration.getInstance().getProviders());
		assertEquals(schedulerBefore, Configuration.getInstance().getScheduler());
		assertEquals(simulationInfoBefore, Configuration.getInstance().getSimulationInfo());
		assertArrayEquals(usersBefore, Configuration.getInstance().getUsers());
	}
	
	@Test
	public void testLoadDataWithPermissionDenied() throws IOException {
		assert file.createNewFile() && file.setReadable(false);
		
		Configuration.getInstance().getApplication();
	}
	
	@Test
	public void testClearAllFiles() throws IOException {
		assert file.createNewFile();
		assert machineDataFile.createNewFile();
		
		JECheckpointer.clear();
		
		assertFalse(file.exists());
		assertFalse(machineDataFile.exists());
	}
}
