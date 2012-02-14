/**
 * 
 */
package saasim.cloud;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import saasim.sim.components.MachineDescriptor;
import saasim.util.TimeUnit;
import saasim.util.ValidConfigurationTest;


/**
 * Test class for {@link TypeProvider}. Construction is not tested as there is no validation here.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProviderTest extends ValidConfigurationTest {

	private double onDemandCost = 0.085;
	private double reservationCost = 0.3;
	private double oneYearFee = 227.5;
	private double threeYearsFee = 350;
	private double monitoringCost = 1.5;
	
	/**
	 * Test method for {@link saasim.cloud.TypeProvider#getType()}.
	 */
	@Test
	public void testConstructor() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		assertEquals(MachineType.M1_SMALL, type.getType());
		assertEquals(5, type.getReservation());
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#getType()}.
	 */
	@Test
	public void testGetNullType() {
		TypeProvider type = new TypeProvider(0, null, 0, 0, 0, 0, 0);
		assertNull(type.getType());
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#shutdownMachine(saasim.sim.components.MachineDescriptor)}.
	 */
	@Test(expected=NullPointerException.class)
	public void testShutdownNullMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		type.shutdownMachine(null);
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#shutdownMachine(saasim.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentReservedMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.shutdownMachine(new MachineDescriptor(1, true, MachineType.M1_SMALL, 0)));
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#shutdownMachine(saasim.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentOnDemandMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.shutdownMachine(new MachineDescriptor(1, false, MachineType.M1_SMALL, 0)));
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#shutdownMachine(saasim.sim.components.MachineDescriptor)}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testShutdownExistentReservedMachine() throws ConfigurationException {
		
		buildFullConfiguration();
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 1);
		MachineDescriptor descriptor = type.buyMachine(true);
		assertTrue(type.shutdownMachine(descriptor));
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#shutdownMachine(saasim.sim.components.MachineDescriptor)}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testShutdownExistentOnDemandMachine() throws ConfigurationException {
		buildFullConfiguration();

		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 1);
		MachineDescriptor descriptor = type.buyMachine(false);
		assertTrue(type.shutdownMachine(descriptor));
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#buyMachine(boolean)}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testBuyOnDemandMachine() throws ConfigurationException {
		buildFullConfiguration();

		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		MachineDescriptor descriptor = type.buyMachine(false);
		assertNotNull(descriptor);
		assertFalse(descriptor.isReserved());
		assertEquals(MachineType.M1_SMALL, descriptor.getType());
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#buyMachine(boolean)}.
	 */
	@Test
	public void testBuyReservedMachineWithNoLimit() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertNull(type.buyMachine(true));
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#buyMachine(boolean)}.
	 */
	@Test
	public void testBuyReservedMachineWithLimit() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 3);
		assertNotNull(type.buyMachine(true));
		assertNotNull(type.buyMachine(true));
		assertNotNull(type.buyMachine(true));
		assertNull(type.buyMachine(true));
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#canBuy()}.
	 */
	@Test
	public void testCanBuyWithNoLimit() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.canBuy());
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#canBuy()}.
	 */
	@Test
	public void testCanBuyWithLimit() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 1);
		assertTrue(type.canBuy());
	}

	/**
	 * Test method for {@link saasim.cloud.TypeProvider#canBuy()}.
	 */
	@Test
	public void testCanBuyWithLimitChanging() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 2);
		assertTrue(type.canBuy());
		type.buyMachine(true);
		type.buyMachine(true);
		assertFalse(type.canBuy());
	}
	
	@Test
	public void testCalculateUniqueCostWithNoReservation(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 0);
		assertEquals(0.0, type.calculateUniqueCost(), 0.0);
	}
	
	@Test
	public void testCalculateUniqueCostWithReservation(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 3);
		assertEquals(227.5 * 3, type.calculateUniqueCost(), 0.0);
	}
	
	@Test
	public void testCalculateCostWithNoMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		
		assertEquals(0, type.calculateMachinesCost(0, monitoringCost).cost, 0.0001);
	}
	
	@Test
	public void testCalculateCostWithOndemandRunningMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		
		MachineDescriptor descriptor = type.buyMachine(false);
		descriptor.setStartTimeInMillis(0);
		
		TypeProviderEntry entry = type.calculateMachinesCost(TimeUnit.HALF_HOUR.getMillis(), monitoringCost);
		assertEquals(1 * (monitoringCost + onDemandCost), entry.cost, 0.0001);
	}
	
	@Test
	public void testCalculateCostWithOndemandFinishedMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		
		MachineDescriptor descriptor = type.buyMachine(false);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(TimeUnit.HOUR.getMillis()/2);
		type.shutdownMachine(descriptor);
		
		TypeProviderEntry entry = type.calculateMachinesCost(TimeUnit.HOUR.getMillis(), monitoringCost);
		assertEquals(1 * (monitoringCost + onDemandCost), entry.cost, 0.0001);
	}
	
	@Test
	public void testCalculateCostWithReservedRunningMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		
		MachineDescriptor descriptor = type.buyMachine(true);
		descriptor.setStartTimeInMillis(0);
		
		TypeProviderEntry entry = type.calculateMachinesCost(TimeUnit.HOUR.getMillis()/2, monitoringCost);
		assertEquals(1 * (monitoringCost + reservationCost), entry.cost, 0.0001);
	}

	@Test
	public void testCalculateCostWithReservedFinishedMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		
		MachineDescriptor descriptor = type.buyMachine(true);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(TimeUnit.HOUR.getMillis()/2);
		type.shutdownMachine(descriptor);
		
		TypeProviderEntry entry = type.calculateMachinesCost(TimeUnit.HOUR.getMillis(), monitoringCost);
		
		assertEquals(1 * (monitoringCost + reservationCost), entry.cost, 0.0001);
	}

	@Test
	public void testGetTotalEmptyTransferences(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		
		assertArrayEquals(new long[]{0,0}, type.getTotalTransferences());
	}
	
	@Test
	public void testGetTotalTransferences(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		
		MachineDescriptor onDemandRunning = type.buyMachine(false);
		MachineDescriptor onDemandFinished = type.buyMachine(false);
		MachineDescriptor reservedRunning = type.buyMachine(true);
		MachineDescriptor reservedFinished = type.buyMachine(true);
		
		onDemandRunning.updateTransference(11, 11);
		onDemandFinished.updateTransference(13, 13);
		reservedRunning.updateTransference(17, 17);
		reservedFinished.updateTransference(19, 19);
		type.shutdownMachine(onDemandFinished);
		type.shutdownMachine(reservedFinished);
		
		assertArrayEquals(new long[]{11+13+17+19, 11+13+17+19}, type.getTotalTransferences());
	}
}
