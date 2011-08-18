/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class MachineTypeValueTest {

	@Test
	public void testValueOfWithExistentValue() {
		assertEquals(MachineTypeValue.SMALL, MachineTypeValue.valueOf(MachineTypeValue.SMALL.name()));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testValueOfWithInexistentValue() {
		MachineTypeValue.valueOf("unknown");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testValueOfWithEmptyValue() {
		MachineTypeValue.valueOf("");
	}

	@Test(expected=NullPointerException.class)
	public void testValueOfWithNullValue() {
		MachineTypeValue.valueOf(null);
	}

}
