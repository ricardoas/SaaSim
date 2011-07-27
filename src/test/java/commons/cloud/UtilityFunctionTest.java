package commons.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;


public class UtilityFunctionTest {
	
	private Provider provider;
	private static final int HOUR_IN_MILLIS = 1000 * 60 * 60;
	private UtilityFunction utility;
	private JEEventScheduler scheduler;

	@Before
	public void setUp(){
		double onDemandCpuCost = 0.12;
		int onDemandLimit = 30;
		int reservationLimit = 20;
		double reservedCpuCost = 0.085;
		double reservationOneYearFee = 227.50;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		String transferInLimits = "";
		String transferInCosts = "";
		String transferOutLimits = "";
		String transferOutCosts = "";
		
		provider = new Provider("prov", onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		utility = new UtilityFunction();
		
		scheduler = new JEEventScheduler();
	}
	
	/**
	 * This method verifies the extra receipt calculation considering a float extra
	 * cpu time
	 */
	@Test
	public void extraReceiptCalcWithExtraCpuResourcesUsed(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 11.5d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d * HOUR_IN_MILLIS;//Partial hour is billed as a full hour
		
		assertEquals(0.9, utility.calcExtraReceipt(contract, user), 0.0);
	}
	
	/**
	 * This method verifies the extra receipt calculation considering an integer extra
	 * cpu time
	 */
	@Test
	public void extraReceiptCalcWithExtraCpuResourcesUsed2(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 5d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d * HOUR_IN_MILLIS;
		
		
		assertEquals(6.3, utility.calcExtraReceipt(contract, user), 0.0);
	}
	
	@Test
	public void extraReceiptCalcWithInvalidCPU(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 5d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = -12d * HOUR_IN_MILLIS;
		
		try{
			utility.calcExtraReceipt(contract, user);
			fail("Invalid consumed cpu!");
		}catch(RuntimeException e){
			
		}
	}
	
	@Test
	public void extraReceiptCalcWithInvalidTransference(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 5d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d * HOUR_IN_MILLIS;
		user.consumedTransference = -.01d;
		
		try{
			utility.calcExtraReceipt(contract, user);
			fail("Invalid consumed transference!");
		}catch(RuntimeException e){
			
		}
	}
	
	@Test
	public void calculateCostForOnDemandAndReservedResources(){
		User user = new User("us1");
		
		//Adding resources
		Machine mach1 = new Machine(scheduler, 1);
		mach1.setTotalProcessed(10 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(scheduler, 2);
		mach2.setTotalProcessed(20 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(scheduler, 3);
		mach3.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(scheduler, 4);
		mach4.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(scheduler, 5);
		mach5.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(scheduler, 6);
		mach6.setTotalProcessed(12.5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(scheduler, 7);
		mach7.setTotalProcessed(15.23 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach7);
		
		Machine mach8 = new Machine(scheduler, 8);
		mach8.setTotalProcessed(18 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach8);
		Machine mach9 = new Machine(scheduler, 9);
		mach9.setTotalProcessed(78.5 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach9);
		Machine mach10 = new Machine(scheduler, 10);
		mach10.setTotalProcessed(1.2 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach10);
		Machine mach11 = new Machine(scheduler, 11);
		mach11.setTotalProcessed(5.14 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach11);
		
		assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost +
				105 * provider.onDemandCpuCost + 105 * provider.monitoringCost, utility.calculateCost(user.consumedTransference, provider), 0.0);
	}
	
	@Test
	public void calculateCostForReservedResources(){
		User user = new User("us1");
		user.consumedCpu = 209d * HOUR_IN_MILLIS;
		
		//Adding resources
		Machine mach1 = new Machine(scheduler, 1);
		mach1.setTotalProcessed(10 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(scheduler, 2);
		mach2.setTotalProcessed(20 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(scheduler, 3);
		mach3.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(scheduler, 4);
		mach4.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(scheduler, 5);
		mach5.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(scheduler, 6);
		mach6.setTotalProcessed(12.5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(scheduler, 7);
		mach7.setTotalProcessed(15.23 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach7);
		
		assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost,
				utility.calculateCost(user.consumedTransference, provider), 0.0);
	}
	
	@Test
	public void calculateTotalReceipt(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 11.5d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d * HOUR_IN_MILLIS;//partial hour is billed as a full hour
		
		assertEquals(0.9 + price + setupCost, utility.calculateTotalReceipt(contract, user), 0.0);
	}
	
	@Test
	public void calculateTotalReceipt2(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 1d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.085d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 199d * HOUR_IN_MILLIS;
		
		assertEquals(16.83 + price + setupCost, utility.calculateTotalReceipt(contract, user), 0.0);
	}
	
	@Test
	public void calculateUtility(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 11.5d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 104d * HOUR_IN_MILLIS;
		
		//Adding resources
		Machine mach1 = new Machine(scheduler, 1);
		mach1.setTotalProcessed(10 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(scheduler, 2);
		mach2.setTotalProcessed(20 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(scheduler, 3);
		mach3.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(scheduler, 4);
		mach4.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(scheduler, 5);
		mach5.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(scheduler, 6);
		mach6.setTotalProcessed(12.5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(scheduler, 7);
		mach7.setTotalProcessed(15.23 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach7);
		
		double cost = 7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost;
		double receipt = Math.ceil((user.consumedCpu - cpuLimit)/HOUR_IN_MILLIS) * extraCpuCost + setupCost + price; 
		
		assertEquals(receipt-cost, utility.calculateUtility(contract, user, provider), 0.0);
	}
	
	@Test
	public void calculateUtility2(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 1d * HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 209d * HOUR_IN_MILLIS;
		
		//Adding resources
		Machine mach1 = new Machine(scheduler, 1);
		mach1.setTotalProcessed(10 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(scheduler, 2);
		mach2.setTotalProcessed(20 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(scheduler, 3);
		mach3.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(scheduler, 4);
		mach4.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(scheduler, 5);
		mach5.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(scheduler, 6);
		mach6.setTotalProcessed(12.5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(scheduler, 7);
		mach7.setTotalProcessed(15.23 * HOUR_IN_MILLIS);
		provider.reservedResources.add(mach7);
		
		Machine mach8 = new Machine(scheduler, 8);
		mach8.setTotalProcessed(18 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach8);
		Machine mach9 = new Machine(scheduler, 9);
		mach9.setTotalProcessed(78.5 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach9);
		Machine mach10 = new Machine(scheduler, 10);
		mach10.setTotalProcessed(1.2 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach10);
		Machine mach11 = new Machine(scheduler, 11);
		mach11.setTotalProcessed(5.14 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(mach11);
		
		double cost = 7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost 
						+ 105 * provider.onDemandCpuCost + 105 * provider.monitoringCost;
		double receipt = Math.ceil((user.consumedCpu - cpuLimit)/HOUR_IN_MILLIS) * extraCpuCost + setupCost + price; 
		
		assertEquals(receipt-cost, utility.calculateUtility(contract, user, provider), 0.0);
	}
}
