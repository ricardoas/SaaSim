package commons.cloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ContractTest {
	
	private String planName = "p1";
	private double setupCost = 55;
	private double price = 200;
	private long cpuLimit = 10;
	private double extraCpuCost = 0.5;
	private long [] transferenceLimits = {10, 100};
	private double [] transferenceCosts = {0.5, 0,3};
	

	@Test
	public void testCompareTo(){
		Contract c1 = new Contract(planName, 1, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts);
		Contract c2A = new Contract(planName, 2, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts);
		Contract c2B = new Contract(planName, 2, setupCost, price, cpuLimit, extraCpuCost, transferenceLimits, transferenceCosts);
		
		assertEquals(0, c1.compareTo(c1));
		assertEquals(1, c1.compareTo(c2A));
		assertEquals(1, c1.compareTo(c2B));
		assertEquals(-1, c2A.compareTo(c1));
		assertEquals(0, c2A.compareTo(c2A));
		assertEquals(0, c2A.compareTo(c2B));
		assertEquals(-1, c2B.compareTo(c1));
		assertEquals(0, c2B.compareTo(c2A));
		assertEquals(0, c2B.compareTo(c2B));
	}
}
