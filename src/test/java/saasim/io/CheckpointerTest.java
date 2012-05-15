package saasim.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.planning.util.MachineUsageData;
import saasim.sim.AccountingSystem;
import saasim.sim.Simulator;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.EventScheduler;
import saasim.util.SimulationInfo;
import saasim.util.ValidConfigurationTest;


public class CheckpointerTest extends ValidConfigurationTest{
	
	private static final File machineDataFile = new File(EventCheckpointer.MACHINE_DATA_DUMP);
	private static final File file = new File(EventCheckpointer.CHECKPOINT_FILE);
	
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

		assertTrue(EventCheckpointer.hasCheckpoint());
	}

	@Test
	public void testHasCheckpointFalse() {
		assertFalse(EventCheckpointer.hasCheckpoint());
	}

	@Test
	public void testHasCheckpointWithUnreadableFile() throws IOException {
		assert file.createNewFile();
		assert file.setWritable(false);
		
		assertFalse(EventCheckpointer.hasCheckpoint());
	}

	@Test
	public void testSaveOnlySimulationInfo() {
		file.delete();
		assertFalse(file.exists());

		Configuration.getInstance().getScheduler();
//		EventCheckpointer.save(new SimulationInfo(2, 9), null, null, null);
		assertTrue(EventCheckpointer.hasCheckpoint());
	}

	@Test
	public void testSaveOnlyUsers() {
		file.delete();
		assertFalse(file.exists());

		Configuration.getInstance().getScheduler();
//		EventCheckpointer.save(null, new User[] {}, null, null);
		assertTrue(file.exists());
	}

	@Test
	public void testSaveOnlyProviders() {
		file.delete();
		assertFalse(file.exists());

//		EventCheckpointer.save(null, null, new Provider[] {}, null);
		assertTrue(file.exists());
	}

	@Test
	public void testSaveOnlyApplication() {
		file.delete();
		assertFalse(file.exists());

//		EventCheckpointer.save(null, null, null, new LoadBalancer[] {});
		assertTrue(file.exists());
	}

	@Test
	public void testDumpMachineData() throws IOException {
		assert !machineDataFile.exists() || machineDataFile.delete();

		EventCheckpointer.dumpMachineData(new MachineUsageData());
		assertTrue(machineDataFile.exists());
	}

	@Test(expected=ConfigurationRuntimeException.class)
	public void testLoadDataWithoutFile() throws ConfigurationException {
		assert !file.exists();
//		EventCheckpointer.loadData();
	}

	@Test
	public void testLoadDataWithInvalidFile() throws IOException, ConfigurationException {
		String invalid = "invalid content";
		FileWriter writer = new FileWriter(file);
		writer.write(invalid);
		writer.close();
		try{
//			EventCheckpointer.loadData();
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
		Simulator applicationBefore = Configuration.getInstance().getSimulator();
		Provider[] providersBefore = Configuration.getInstance().getProviders();
		EventScheduler schedulerBefore = Configuration.getInstance().getScheduler();
		SimulationInfo simulationInfoBefore = Configuration.getInstance().getSimulationInfo();
		User[] usersBefore = Configuration.getInstance().getUsers();
//		EventCheckpointer.save();
		
		buildFullConfiguration();
		
		simulationInfoBefore.addDay();
		
//		assertEquals(accountingSystemBefore, Checkpointer.loadAccountingSystem());
		assertEquals(applicationBefore, Configuration.getInstance().getSimulator());
		assertArrayEquals(providersBefore, Configuration.getInstance().getProviders());
		assertEquals(schedulerBefore, Configuration.getInstance().getScheduler());
		assertEquals(simulationInfoBefore, Configuration.getInstance().getSimulationInfo());
		assertArrayEquals(usersBefore, Configuration.getInstance().getUsers());
	}
	
	@Test
	public void testLoadDataWithPermissionDenied() throws IOException {
		assert file.createNewFile() && file.setReadable(false);
		
		Configuration.getInstance().getSimulator();
	}
	
	@Test
	public void testClearAllFiles() throws IOException {
		assert file.createNewFile();
		assert machineDataFile.createNewFile();
		
		EventCheckpointer.clear();
		
		assertFalse(file.exists());
		assertFalse(machineDataFile.exists());
	}
}
