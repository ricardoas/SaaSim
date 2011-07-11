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
		provider.reservedResources.add(new Resource(0, 10 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(1, 21 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(121.5, 134 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(189.1, 204.33 * ProviderTest.HOUR));
		
		provider.onDemandResources.add(new Resource(121, 139 * ProviderTest.HOUR));
		provider.onDemandResources.add(new Resource(122, 200.5 * ProviderTest.HOUR));
		provider.onDemandResources.add(new Resource(133.8, 135 * ProviderTest.HOUR));
		provider.onDemandResources.add(new Resource(378.01, 383.15 * ProviderTest.HOUR));
		
		assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost +
				105 * provider.onDemandCpuCost + 105 * provider.monitoringCost, utility.calculateCost(user, provider), 0.0);
	}
	
	@Test
	public void calculateCostForReservedResources(){
		User user = new User("us1");
		user.consumedCpu = 209d;
		
		//Adding resources
		provider.reservedResources.add(new Resource(0, 10 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(1, 21 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(121.5, 134 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(189.1, 204.33 * ProviderTest.HOUR));
		
		assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost,
				utility.calculateCost(user, provider), 0.0);
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
		provider.reservedResources.add(new Resource(0, 10 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(1, 21 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(121.5, 134 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(189.1, 204.33 * ProviderTest.HOUR));
		
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
		provider.reservedResources.add(new Resource(0, 10 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(1, 21 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(120, 135 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(121.5, 134 * ProviderTest.HOUR));
		provider.reservedResources.add(new Resource(189.1, 204.33 * ProviderTest.HOUR));
		
		provider.onDemandResources.add(new Resource(121, 139 * ProviderTest.HOUR));
		provider.onDemandResources.add(new Resource(122, 200.5 * ProviderTest.HOUR));
		provider.onDemandResources.add(new Resource(133.8, 135 * ProviderTest.HOUR));
		provider.onDemandResources.add(new Resource(378.01, 383.15 * ProviderTest.HOUR));
		
		double cost = 7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost 
						+ 105 * provider.onDemandCpuCost + 105 * provider.monitoringCost;
		double receipt = (user.consumedCpu - cpuLimit)*extraCpuCost + setupCost + price; 
		
		assertEquals(receipt-cost, utility.calculateUtility(contract, user, provider), 0.0);
	}
}
