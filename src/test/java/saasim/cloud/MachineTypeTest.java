package saasim.cloud;

import static org.junit.Assert.assertEquals;
import static saasim.cloud.MachineType.*;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import saasim.cloud.MachineType;
import saasim.util.CleanConfigurationTest;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class MachineTypeTest extends CleanConfigurationTest {

	@Test
	public void testValueOfWithExistentValue() {
		assertEquals(MachineType.M1_SMALL, MachineType.valueOf(MachineType.M1_SMALL.name()));
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
	
	@Test
	public void testOrderByCores(){
		MachineType[] values = MachineType.values();
		Arrays.sort(values, new Comparator<MachineType>() {
			@Override
			public int compare(MachineType o1, MachineType o2) {
				return o1.getNumberOfCores() - o2.getNumberOfCores();
			}
		});
		
		Assert.assertArrayEquals(new MachineType[] { M1_SMALL, T1_MICRO,
				M1_LARGE, C1_MEDIUM, M2_XLARGE, M1_XLARGE, M2_2XLARGE, 
				M2_4XLARGE, C1_XLARGE }, values);
	}

	@Test
	public void testOrderByPower(){
		MachineType[] values = MachineType.values();
		Arrays.sort(values, new Comparator<MachineType>() {
			@Override
			public int compare(MachineType o1, MachineType o2) {
				return Double.compare(o1.getPower(), o2.getPower());
			}
		});
		
		Assert.assertArrayEquals(new MachineType[] { M1_SMALL, M1_LARGE, 
				M1_XLARGE, T1_MICRO, C1_MEDIUM, C1_XLARGE, M2_XLARGE, 
				M2_2XLARGE, M2_4XLARGE}, values);
	}

}
