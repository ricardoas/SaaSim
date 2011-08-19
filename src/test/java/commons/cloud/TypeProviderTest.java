/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Test;

import commons.sim.components.MachineDescriptor;

/**
 * Test class for {@link TypeProvider}. Construction is not tested as there is no validation here.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TypeProviderTest {

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

}
