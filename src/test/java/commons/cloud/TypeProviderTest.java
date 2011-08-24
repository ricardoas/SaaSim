/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import commons.sim.components.MachineDescriptor;

/**
 * Test class for {@link TypeProvider}. Construction is not tested as there is no validation here.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProviderTest {

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
	public void testGetType() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 0);
		assertEquals(MachineType.SMALL, type.getType());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#getType()}.
	 */
	@Test
	public void testGetNullType() {
		TypeProvider type = new TypeProvider(null, 0, 0, 0, 0, 0);
		assertNull(type.getType());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test(expected=NullPointerException.class)
	public void testShutdownNullMachine() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 0);
		type.shutdownMachine(null);
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentReservedMachine() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.shutdownMachine(new MachineDescriptor(1, true, MachineType.SMALL)));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentOnDemandMachine() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.shutdownMachine(new MachineDescriptor(1, false, MachineType.SMALL)));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownExistentReservedMachine() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 1);
		MachineDescriptor descriptor = type.buyMachine(true);
		assertTrue(type.shutdownMachine(descriptor));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownExistentOnDemandMachine() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 1);
		MachineDescriptor descriptor = type.buyMachine(false);
		assertTrue(type.shutdownMachine(descriptor));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#buyMachine(boolean)}.
	 */
	@Test
	public void testBuyOnDemandMachine() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 0);
		MachineDescriptor descriptor = type.buyMachine(false);
		assertNotNull(descriptor);
		assertFalse(descriptor.isReserved());
		assertEquals(MachineType.SMALL, descriptor.getType());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#buyMachine(boolean)}.
	 */
	@Test
	public void testBuyReservedMachineWithNoLimit() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 0);
		assertNull(type.buyMachine(true));
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#buyMachine(boolean)}.
	 */
	@Test
	public void testBuyReservedMachineWithLimit() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 3);
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
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.canBuy());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#canBuy()}.
	 */
	@Test
	public void testCanBuyWithLimit() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 1);
		assertTrue(type.canBuy());
	}

	/**
	 * Test method for {@link commons.cloud.TypeProvider#canBuy()}.
	 */
	@Test
	public void testCanBuyWithLimitChanging() {
		TypeProvider type = new TypeProvider(MachineType.SMALL, 0, 0, 0, 0, 2);
		assertTrue(type.canBuy());
		type.buyMachine(true);
		type.buyMachine(true);
		assertFalse(type.canBuy());
	}
	
	@Test
	public void testCalculateUniqueCostWithNoReservation(){
		TypeProvider type = new TypeProvider(MachineType.SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 0);
		assertEquals(0.0, type.calculateUniqueCost(), 0.0);
	}
	
	@Test
	public void testCalculateUniqueCostWithReservation(){
		TypeProvider type = new TypeProvider(MachineType.SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 3);
		assertEquals(227.5 * 3, type.calculateUniqueCost(), 0.0);
	}
	
	@Test
	public void testCalculateUniqueCostWithNoMachineUsed(){
		TypeProvider type = new TypeProvider(MachineType.SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost("amazon", MachineType.SMALL, 0, 0, 0, 0, 0);
		EasyMock.replay(entry);
		
		type.calculateMachinesCost(entry, "amazon", 0, monitoringCost);
		
		EasyMock.verify(entry);
	}
	
	@Test
	public void testCalculateUniqueCostWithOndemandRunningMachineUsed(){
		TypeProvider type = new TypeProvider(MachineType.SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost("amazon", MachineType.SMALL, 1, 1 * onDemandCost, 0, 0, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(false);
		descriptor.setStartTimeInMillis(0);
		
		type.calculateMachinesCost(entry, "amazon", HOUR_IN_MILLIS/2, monitoringCost);
		
		EasyMock.verify(entry);
	}
	
	@Test
	public void testCalculateUniqueCostWithOndemandFinishedMachineUsed(){
		TypeProvider type = new TypeProvider(MachineType.SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost("amazon", MachineType.SMALL, 1, 1 * onDemandCost, 0, 0, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(false);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(HOUR_IN_MILLIS/2);
		type.shutdownMachine(descriptor);
		
		type.calculateMachinesCost(entry, "amazon", HOUR_IN_MILLIS, monitoringCost);
		
		EasyMock.verify(entry);
	}
	
	@Test
	public void testCalculateUniqueCostWithReservedRunningMachineUsed(){
		TypeProvider type = new TypeProvider(MachineType.SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost("amazon", MachineType.SMALL, 0, 0, 1, 1 * reservationCost, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(true);
		descriptor.setStartTimeInMillis(0);
		
		type.calculateMachinesCost(entry, "amazon", HOUR_IN_MILLIS/2, monitoringCost);
		
		EasyMock.verify(entry);
	}

	@Test
	public void testCalculateUniqueCostWithReservedFinishedMachineUsed(){
		TypeProvider type = new TypeProvider(MachineType.SMALL, onDemandCost, reservationCost, oneYearFee, threeYearsFee, 5);
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addUsageToCost("amazon", MachineType.SMALL, 0, 0, 1, 1 * reservationCost, 1 * monitoringCost);
		EasyMock.replay(entry);
		
		MachineDescriptor descriptor = type.buyMachine(true);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(HOUR_IN_MILLIS/2);
		type.shutdownMachine(descriptor);
		
		type.calculateMachinesCost(entry, "amazon", HOUR_IN_MILLIS, monitoringCost);
		
		EasyMock.verify(entry);
	}
}
