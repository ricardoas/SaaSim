/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Test;

import commons.sim.components.MachineDescriptor;

/**
 * Test class for {@link MachineType}. Construction is not tested as there is no validation here.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MachineTypeTest {

	/**
	 * Test method for {@link commons.cloud.MachineType#getType()}.
	 */
	@Test
	public void testGetType() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 0);
		assertEquals(MachineTypeValue.SMALL, type.getType());
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#getType()}.
	 */
	@Test
	public void testGetNullType() {
		MachineType type = new MachineType(null, 0, 0, 0, 0, 0);
		assertNull(type.getType());
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownNullMachine() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 0);
		type.shutdownMachine(null);
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentMachine() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.shutdownMachine(new MachineDescriptor(1, false, MachineTypeValue.SMALL)));
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownExistentMachine() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 1);
		MachineDescriptor descriptor = type.buyMachine();
		assertTrue(type.shutdownMachine(descriptor));
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#buyMachine()}.
	 */
	@Test
	public void testBuyMachineWithNoLimit() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 0);
		assertNull(type.buyMachine());
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#buyMachine()}.
	 */
	@Test
	public void testBuyMachineWithLimit() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 3);
		assertNotNull(type.buyMachine());
		assertNotNull(type.buyMachine());
		assertNotNull(type.buyMachine());
		assertNull(type.buyMachine());
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#canBuy()}.
	 */
	@Test
	public void testCanBuyWithNoLimit() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 0);
		assertFalse(type.canBuy());
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#canBuy()}.
	 */
	@Test
	public void testCanBuyWithLimit() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 1);
		assertTrue(type.canBuy());
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#canBuy()}.
	 */
	@Test
	public void testCanBuyWithLimitChanging() {
		MachineType type = new MachineType(MachineTypeValue.SMALL, 0, 0, 0, 0, 2);
		assertTrue(type.canBuy());
		type.buyMachine();
		type.buyMachine();
		assertFalse(type.canBuy());
	}

}
