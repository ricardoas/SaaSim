/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import util.CleanConfigurationTest;

import commons.sim.components.MachineDescriptor;

/**
 * Test class for {@link TypeProvider}. Construction is not tested as there is no validation here.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProviderTest extends CleanConfigurationTest {

	private static final long HOUR_IN_MILLIS = 3600000;

	private double onDemandCost = 0.085;
	private double reservationCost = 0.3;
	private double oneYearFee = 227.5;
	private double threeYearsFee = 350;
	private double monitoringCost = 1.5;

	/**
	 * Test method for {@link commons.cloud.TypeProvider#getType()}.
	 */
	@Test
	public void testConstructor() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		assertEquals(MachineType.M1_SMALL, type.getType());
		assertEquals(5, type.getReservation());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#getType()}.
	 */
	@Test
	public void testGetNullType() {
		TypeProvider type = new TypeProvider(0, null, 0, 0, 0, 0, 0);
		assertNull(type.getType());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test(expected=NullPointerException.class)
	public void testShutdownNullMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		type.shutdownMachine(null);
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentReservedMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.shutdownMachine(new MachineDescriptor(1, true, MachineType.M1_SMALL, 0)));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentOnDemandMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.shutdownMachine(new MachineDescriptor(1, false, MachineType.M1_SMALL, 0)));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownExistentReservedMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 1);
		MachineDescriptor descriptor = type.buyMachine(true);
		assertTrue(type.shutdownMachine(descriptor));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownExistentOnDemandMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 1);
		MachineDescriptor descriptor = type.buyMachine(false);
		assertTrue(type.shutdownMachine(descriptor));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#buyMachine(boolean)}.
	 */
	@Test
	public void testBuyOnDemandMachine() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		MachineDescriptor descriptor = type.buyMachine(false);
		assertNotNull(descriptor);
		assertFalse(descriptor.isReserved());
		assertEquals(MachineType.M1_SMALL, descriptor.getType());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#buyMachine(boolean)}.
	 */
	@Test
	public void testBuyReservedMachineWithNoLimit() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertNull(type.buyMachine(true));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#buyMachine(boolean)}.
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
	 * Test method for {@link commons.cloud.TypeProvider#canBuy()}.
	 */
	@Test
	public void testCanBuyWithNoLimit() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.canBuy());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#canBuy()}.
	 */
	@Test
	public void testCanBuyWithLimit() {
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, 0, 0, 0, 0, 1);
		assertTrue(type.canBuy());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#canBuy()}.
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
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost(0, MachineType.M1_SMALL, 0, 0, 0, 0, 0);
		EasyMock.replay(entry);
		
		type.calculateMachinesCost(entry, 0, monitoringCost);
		
		EasyMock.verify(entry);
	}
	
	@Test
	public void testCalculateCostWithOndemandRunningMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost(0, MachineType.M1_SMALL, 1, 1 * onDemandCost, 0, 0, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(false);
		descriptor.setStartTimeInMillis(0);
		
		type.calculateMachinesCost(entry, HOUR_IN_MILLIS/2, monitoringCost);
		
		EasyMock.verify(entry);
	}
	
	@Test
	public void testCalculateCostWithOndemandFinishedMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost(0, MachineType.M1_SMALL, 1, 1 * onDemandCost, 0, 0, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(false);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(HOUR_IN_MILLIS/2);
		type.shutdownMachine(descriptor);
		
		type.calculateMachinesCost(entry, HOUR_IN_MILLIS, monitoringCost);
		
		EasyMock.verify(entry);
	}
	
	@Test
	public void testCalculateCostWithReservedRunningMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost(0, MachineType.M1_SMALL, 0, 0, 1, 1 * reservationCost, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(true);
		descriptor.setStartTimeInMillis(0);
		
		type.calculateMachinesCost(entry, HOUR_IN_MILLIS/2, monitoringCost);
		
		EasyMock.verify(entry);
	}

	@Test
	public void testCalculateCostWithReservedFinishedMachineUsed(){
		TypeProvider type = new TypeProvider(0, MachineType.M1_SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost(0, MachineType.M1_SMALL, 0, 0, 1, 1 * reservationCost, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(true);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(HOUR_IN_MILLIS/2);
		type.shutdownMachine(descriptor);
		
		type.calculateMachinesCost(entry, HOUR_IN_MILLIS, monitoringCost);
		
		EasyMock.verify(entry);
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
