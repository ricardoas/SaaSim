/**
 * 
 */
package commons.sim.components;

import static org.junit.Assert.*;

import org.junit.Test;

import util.CleanConfigurationTest;

import commons.cloud.MachineType;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class MachineDescriptorTest extends CleanConfigurationTest {

	private MachineDescriptor small = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
	private MachineDescriptor otherSmall = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
	private MachineDescriptor large = new MachineDescriptor(0, false, MachineType.M1_LARGE, 0);
	
	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#hashCode()}.
	 */
	@Test
	public void testHashCodeForDifferentMachinesWithSameID() {
		assertFalse(small.hashCode() == large.hashCode());
	}

	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#hashCode()}.
	 */
	@Test
	public void testHashCodeForMachinesWithDifferentIDs() {
		assertFalse(small.hashCode() == otherSmall.hashCode());
	}

	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#hashCode()}.
	 */
	@Test
	public void testHashCodeForSameMachines() {
		assertTrue(small.hashCode() == new MachineDescriptor(0, false, MachineType.M1_SMALL, 0).hashCode());
	}

	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsDifferentMachinesWithSameID() {
		assertTrue(small.equals(large));
	}

	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsMachinesWithDifferentIDs() {
		assertFalse(small.equals(otherSmall));
	}

	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsSameMachines() {
		assertEquals(small, new MachineDescriptor(0, false, MachineType.M1_SMALL, 0));
	}

	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#updateTransference(long, long)}.
	 */
	@Test
	public void testUpdateTransference() {
		MachineDescriptor machine = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
		assertEquals(0, machine.getInTransference());
		assertEquals(0, machine.getOutTransference());
		machine.updateTransference(1000, 0);
		assertEquals(1000, machine.getInTransference());
		assertEquals(0, machine.getOutTransference());
		machine.updateTransference(0, 1000);
		assertEquals(1000, machine.getInTransference());
		assertEquals(1000, machine.getOutTransference());
		machine.updateTransference(500, 250);
		assertEquals(1500, machine.getInTransference());
		assertEquals(1250, machine.getOutTransference());
	}

	/**
	 * Test method for {@link commons.sim.components.MachineDescriptor#reset(long)}.
	 */
	@Test
	public void testReset() {
		MachineDescriptor machine = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
		assertEquals(0, machine.getInTransference());
		assertEquals(0, machine.getOutTransference());
		assertEquals(0, machine.getStartTimeInMillis());
		machine.setStartTimeInMillis(86400000);
		machine.updateTransference(500, 250);
		assertEquals(500, machine.getInTransference());
		assertEquals(250, machine.getOutTransference());
		assertEquals(86400000, machine.getStartTimeInMillis());
		machine.reset(0);
		assertEquals(0, machine.getInTransference());
		assertEquals(0, machine.getOutTransference());
		assertEquals(0, machine.getStartTimeInMillis());
	}

}
