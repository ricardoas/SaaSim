/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Test;

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
	 * Test method for {@link commons.cloud.MachineType#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownMachine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#buyMachine()}.
	 */
	@Test
	public void testBuyMachine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.cloud.MachineType#canBuy()}.
	 */
	@Test
	public void testCanBuy() {
		fail("Not yet implemented");
	}

}
