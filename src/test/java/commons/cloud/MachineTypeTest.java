/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class MachineTypeTest {

	@Test
	public void testValueOfWithExistentValue() {
		assertEquals(MachineType.SMALL, MachineType.valueOf(MachineType.SMALL.name()));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testValueOfWithInexistentValue() {
		MachineType.valueOf("unknown");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testValueOfWithEmptyValue() {
		MachineType.valueOf("");
	}

	@Test(expected=NullPointerException.class)
	public void testValueOfWithNullValue() {
		MachineType.valueOf(null);
	}

}
