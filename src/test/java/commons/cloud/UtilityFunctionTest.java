package commons.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import commons.sim.jeevent.JEEventScheduler;
import commons.util.Triple;


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
		Long mach1ID = 1l;
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = 10d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach1ID, triple);
		
		Long mach2ID = 2l;
		Triple<Double, Double, Double> triple2 = new Triple<Double, Double, Double>();
		triple2.firstValue = 0d;
		triple2.secondValue = 20d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach2ID, triple2);
		
		Long mach3ID = 3l;
		Triple<Double, Double, Double> triple3 = new Triple<Double, Double, Double>();
		triple3.firstValue = 1d * HOUR_IN_MILLIS;
		triple3.secondValue = 16d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach3ID, triple3);
		
		Long mach4ID = 4l;
		Triple<Double, Double, Double> triple4 = new Triple<Double, Double, Double>();
		triple4.firstValue = 15d * HOUR_IN_MILLIS;
		triple4.secondValue = 30d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach4ID, triple4);
		
		Long mach5ID = 5l;
		Triple<Double, Double, Double> triple5 = new Triple<Double, Double, Double>();
		triple5.firstValue = 0d;
		triple5.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach5ID, triple5);
		
		Long mach6ID = 6l;
		Triple<Double, Double, Double> triple6 = new Triple<Double, Double, Double>();
		triple6.firstValue = 12.5d * HOUR_IN_MILLIS;
		triple6.secondValue = 25d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach6ID, triple6);
		
		Long mach7ID = 7l;
		Triple<Double, Double, Double> triple7 = new Triple<Double, Double, Double>();
		triple7.firstValue = 0d;
		triple7.secondValue = 15.23d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach7ID, triple7);
		
		Long mach8ID = 8l;
		Triple<Double, Double, Double> triple8 = new Triple<Double, Double, Double>();
		triple8.firstValue = 0d;
		triple8.secondValue = 18d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach8ID, triple8);
		
		Long mach9ID = 9l;
		Triple<Double, Double, Double> triple9 = new Triple<Double, Double, Double>();
		triple9.firstValue = 0d;
		triple9.secondValue = 78.5d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach9ID, triple9);
		
		Long mach10ID = 10l;
		Triple<Double, Double, Double> triple10 = new Triple<Double, Double, Double>();
		triple10.firstValue = 2.4d * HOUR_IN_MILLIS;
		triple10.secondValue = 3.6d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach10ID, triple10);
		
		Long mach11ID = 11l;
		Triple<Double, Double, Double> triple11 = new Triple<Double, Double, Double>();
		triple11.firstValue = 0d;
		triple11.secondValue = 5.14d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach11ID, triple11);
		
		assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost +
				105 * provider.onDemandCpuCost + 105 * provider.monitoringCost, utility.calculateCost(user.consumedTransference, provider), 0.0);
	}
	
	@Test
	public void calculateCostForReservedResources(){
		User user = new User("us1");
		user.consumedCpu = 209d * HOUR_IN_MILLIS;
		
		//Adding resources
		Long mach1ID = 1l;
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = 10d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach1ID, triple);
		
		Long mach2ID = 2l;
		Triple<Double, Double, Double> triple2 = new Triple<Double, Double, Double>();
		triple2.firstValue = 10d * HOUR_IN_MILLIS;
		triple2.secondValue = 30d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach2ID, triple2);
		
		Long mach3ID = 3l;
		Triple<Double, Double, Double> triple3 = new Triple<Double, Double, Double>();
		triple3.firstValue = 0d;
		triple3.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach3ID, triple3);
		
		Long mach4ID = 4l;
		Triple<Double, Double, Double> triple4 = new Triple<Double, Double, Double>();
		triple4.firstValue = 15d * HOUR_IN_MILLIS;
		triple4.secondValue = 30d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach4ID, triple4);
		
		Long mach5ID = 5l;
		Triple<Double, Double, Double> triple5 = new Triple<Double, Double, Double>();
		triple5.firstValue = 0d;
		triple5.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach5ID, triple5);
		
		Long mach6ID = 6l;
		Triple<Double, Double, Double> triple6 = new Triple<Double, Double, Double>();
		triple6.firstValue = 10d * HOUR_IN_MILLIS;
		triple6.secondValue = 22.5d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach6ID, triple6);
		
		Long mach7ID = 7l;
		Triple<Double, Double, Double> triple7 = new Triple<Double, Double, Double>();
		triple7.firstValue = 0d;
		triple7.secondValue = 15.23d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach7ID, triple7);
		
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
		user.consumedCpu = 108d * HOUR_IN_MILLIS;
		
		//Adding resources
		Long mach1ID = 1l;
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = 10d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach1ID, triple);
		
		Long mach2ID = 2l;
		Triple<Double, Double, Double> triple2 = new Triple<Double, Double, Double>();
		triple2.firstValue = 0d;
		triple2.secondValue = 20d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach2ID, triple2);
		
		Long mach3ID = 3l;
		Triple<Double, Double, Double> triple3 = new Triple<Double, Double, Double>();
		triple3.firstValue = 0d;
		triple3.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach3ID, triple3);
		
		Long mach4ID = 4l;
		Triple<Double, Double, Double> triple4 = new Triple<Double, Double, Double>();
		triple4.firstValue = 0d;
		triple4.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach4ID, triple4);
		
		Long mach5ID = 5l;
		Triple<Double, Double, Double> triple5 = new Triple<Double, Double, Double>();
		triple5.firstValue = 0d;
		triple5.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach5ID, triple5);
		
		Long mach6ID = 6l;
		Triple<Double, Double, Double> triple6 = new Triple<Double, Double, Double>();
		triple6.firstValue = 0d;
		triple6.secondValue = 12.5d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach6ID, triple6);
		
		Long mach7ID = 7l;
		Triple<Double, Double, Double> triple7 = new Triple<Double, Double, Double>();
		triple7.firstValue = 0d;
		triple7.secondValue = 20d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach7ID, triple7);
		
		double cost = 7 * provider.reservationOneYearFee + 108 * provider.reservedCpuCost + 108 * provider.monitoringCost;
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
		user.consumedCpu = 212d * HOUR_IN_MILLIS;
		
		//Adding reserved resources data
		Long mach1ID = 1l;
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = 10d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach1ID, triple);
		
		Long mach2ID = 2l;
		Triple<Double, Double, Double> triple2 = new Triple<Double, Double, Double>();
		triple2.firstValue = 0d;
		triple2.secondValue = 20d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach2ID, triple2);
		
		Long mach3ID = 3l;
		Triple<Double, Double, Double> triple3 = new Triple<Double, Double, Double>();
		triple3.firstValue = 0d;
		triple3.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach3ID, triple3);
		
		Long mach4ID = 4l;
		Triple<Double, Double, Double> triple4 = new Triple<Double, Double, Double>();
		triple4.firstValue = 0d;
		triple4.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach4ID, triple4);
		
		Long mach5ID = 5l;
		Triple<Double, Double, Double> triple5 = new Triple<Double, Double, Double>();
		triple5.firstValue = 0d;
		triple5.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach5ID, triple5);
		
		Long mach6ID = 6l;
		Triple<Double, Double, Double> triple6 = new Triple<Double, Double, Double>();
		triple6.firstValue = 0d;
		triple6.secondValue = 15.5d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach6ID, triple6);
		
		Long mach7ID = 7l;
		Triple<Double, Double, Double> triple7 = new Triple<Double, Double, Double>();
		triple7.firstValue = 0d;
		triple7.secondValue = 15.23d * HOUR_IN_MILLIS;
		provider.reservedResources.put(mach7ID, triple7);
		
		//On-demand resources data
		Long mach8ID = 8l;
		Triple<Double, Double, Double> triple8 = new Triple<Double, Double, Double>();
		triple8.firstValue = 0d;
		triple8.secondValue = 18d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach8ID, triple8);
		
		Long mach9ID = 9l;
		Triple<Double, Double, Double> triple9 = new Triple<Double, Double, Double>();
		triple9.firstValue = 0d;
		triple9.secondValue = 78.5d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach9ID, triple9);
		
		Long mach10ID = 10l;
		Triple<Double, Double, Double> triple10 = new Triple<Double, Double, Double>();
		triple10.firstValue = 0d;
		triple10.secondValue = 1.2d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach10ID, triple10);
		
		Long mach11ID = 11l;
		Triple<Double, Double, Double> triple11 = new Triple<Double, Double, Double>();
		triple11.firstValue = 0d;
		triple11.secondValue = 5.14d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach11ID, triple11);
		
		double cost = 7 * provider.reservationOneYearFee + 107 * provider.reservedCpuCost + 107 * provider.monitoringCost 
						+ 105 * provider.onDemandCpuCost + 105 * provider.monitoringCost;
		double receipt = Math.ceil((user.consumedCpu - cpuLimit)/HOUR_IN_MILLIS) * extraCpuCost + setupCost + price; 
		
		assertEquals(receipt-cost, utility.calculateUtility(contract, user, provider), 0.0);
	}
}
