package commons.cloud;

import static org.junit.Assert.*;

import org.junit.*;


public class UtilityFunctionTest {
	
	private Provider provider;
	private UtilityFunction utility;

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
	}
	
	/**
	 * This method verifies the extra receipt calculation considering a float extra
	 * cpu time
	 */
	@Test
	public void extraReceiptCalcWithExtraCpuResourcesUsed(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 11.5d;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d;
		
		assertEquals(0.45, utility.calcExtraReceipt(contract, user), 0.0);
	}
	
	/**
	 * This method verifies the extra receipt calculation considering an integer extra
	 * cpu time
	 */
	@Test
	public void extraReceiptCalcWithExtraCpuResourcesUsed2(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 5d;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d;
		
		
		assertEquals(6.3, utility.calcExtraReceipt(contract, user), 0.0);
	}
	
	@Test
	public void extraReceiptCalcWithInvalidCPU(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 5d;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = -12d;
		
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
		Double cpuLimit = 5d;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d;
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
		user.consumedCpu = 209d;
		
		//Adding resources
		Machine mach1 = new Machine(1);
		mach1.totalProcessed = 10 * ProviderTest.HOUR;
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(2);
		mach2.totalProcessed = 20 * ProviderTest.HOUR;
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(3);
		mach3.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(4);
		mach4.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(5);
		mach5.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(6);
		mach6.totalProcessed = 12.5 * ProviderTest.HOUR;
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(7);
		mach7.totalProcessed = 15.23 * ProviderTest.HOUR;
		provider.reservedResources.add(mach7);
		
		Machine mach8 = new Machine(8);
		mach8.totalProcessed = 18 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach8);
		Machine mach9 = new Machine(9);
		mach9.totalProcessed = 78.5 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach9);
		Machine mach10 = new Machine(10);
		mach10.totalProcessed = 1.2 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach10);
		Machine mach11 = new Machine(11);
		mach11.totalProcessed = 5.14 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach11);
		
		assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost +
				105 * provider.onDemandCpuCost + 105 * provider.monitoringCost, utility.calculateCost(user.consumedTransference, provider), 0.0);
	}
	
	@Test
	public void calculateCostForReservedResources(){
		User user = new User("us1");
		user.consumedCpu = 209d;
		
		//Adding resources
		Machine mach1 = new Machine(1);
		mach1.totalProcessed = 10 * ProviderTest.HOUR;
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(2);
		mach2.totalProcessed = 20 * ProviderTest.HOUR;
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(3);
		mach3.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(4);
		mach4.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(5);
		mach5.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(6);
		mach6.totalProcessed = 12.5 * ProviderTest.HOUR;
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(7);
		mach7.totalProcessed = 15.23 * ProviderTest.HOUR;
		provider.reservedResources.add(mach7);
		
		assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost,
				utility.calculateCost(user.consumedTransference, provider), 0.0);
	}
	
	@Test
	public void calculateTotalReceipt(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 11.5d;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d;
		
		assertEquals(0.45 + price + setupCost, utility.calculateTotalReceipt(contract, user), 0.0);
	}
	
	@Test
	public void calculateTotalReceipt2(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 1d;
		Double extraCpuCost = 0.085d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 199d;
		
		assertEquals(16.83 + price + setupCost, utility.calculateTotalReceipt(contract, user), 0.0);
	}
	
	@Test
	public void calculateUtility(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 11.5d;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 104d;
		
		//Adding resources
		Machine mach1 = new Machine(1);
		mach1.totalProcessed = 10 * ProviderTest.HOUR;
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(2);
		mach2.totalProcessed = 20 * ProviderTest.HOUR;
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(3);
		mach3.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(4);
		mach4.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(5);
		mach5.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(6);
		mach6.totalProcessed = 12.5 * ProviderTest.HOUR;
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(7);
		mach7.totalProcessed = 15.23 * ProviderTest.HOUR;
		provider.reservedResources.add(mach7);
		
		double cost = 7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost;
		double receipt = (user.consumedCpu - cpuLimit)*extraCpuCost + setupCost + price; 
		
		assertEquals(receipt-cost, utility.calculateUtility(contract, user, provider), 0.0);
	}
	
	@Test
	public void calculateUtility2(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 1d;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 209d;
		
		//Adding resources
		Machine mach1 = new Machine(1);
		mach1.totalProcessed = 10 * ProviderTest.HOUR;
		provider.reservedResources.add(mach1);
		Machine mach2 = new Machine(2);
		mach2.totalProcessed = 20 * ProviderTest.HOUR;
		provider.reservedResources.add(mach2);
		Machine mach3 = new Machine(3);
		mach3.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach3);
		Machine mach4 = new Machine(4);
		mach4.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach4);
		Machine mach5 = new Machine(5);
		mach5.totalProcessed = 15 * ProviderTest.HOUR;
		provider.reservedResources.add(mach5);
		Machine mach6 = new Machine(6);
		mach6.totalProcessed = 12.5 * ProviderTest.HOUR;
		provider.reservedResources.add(mach6);
		Machine mach7 = new Machine(7);
		mach7.totalProcessed = 15.23 * ProviderTest.HOUR;
		provider.reservedResources.add(mach7);
		
		Machine mach8 = new Machine(8);
		mach8.totalProcessed = 18 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach8);
		Machine mach9 = new Machine(9);
		mach9.totalProcessed = 78.5 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach9);
		Machine mach10 = new Machine(10);
		mach10.totalProcessed = 1.2 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach10);
		Machine mach11 = new Machine(11);
		mach11.totalProcessed = 5.14 * ProviderTest.HOUR;
		provider.onDemandResources.add(mach11);
		
		double cost = 7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost 
						+ 105 * provider.onDemandCpuCost + 105 * provider.monitoringCost;
		double receipt = (user.consumedCpu - cpuLimit)*extraCpuCost + setupCost + price; 
		
		assertEquals(receipt-cost, utility.calculateUtility(contract, user, provider), 0.0);
	}
}
