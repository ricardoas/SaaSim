package commons.cloud;

import static org.junit.Assert.fail;

import org.junit.Test;


public class ContractTest {
	
	private String planName = "p1";
	private double setupCost = 55;
	private double price = 200;
	private double cpuLimit = 10;
	private double extraCpuCost = 0.5;
	

	@Test
	public void contractWithInvalidSetupCost(){
		try{
			new Contract(planName, -10d, price, cpuLimit, extraCpuCost);
			fail("Invalid Contract!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void contractWithInvalidPrice(){
		try{
			new Contract(planName, setupCost, -0.0009, cpuLimit, extraCpuCost);
			fail("Invalid Contract!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void contractWithInvalidCpuLimit(){
		try{
			new Contract(planName, setupCost, price, -9999999d, extraCpuCost);
			fail("Invalid Contract!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void contractWithInvalidExtraCpuCost(){
		try{
			new Contract(planName, setupCost, price, cpuLimit, -1.33333);
			fail("Invalid Contract!");
		}catch(RuntimeException e){
		}
	}

}
