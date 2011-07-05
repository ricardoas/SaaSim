package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Test;


public class ProviderTest {
	
	private String name = "prov";
	public static final int HOUR = 1;

	@Test
	public void calculateCostForReservedResources(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		String transferInLimits = "";
		String transferInCosts = "";
		String transferOutLimits = "";
		String transferOutCosts = "";
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		provider.reservedResources.add(new Resource(0 * HOUR, 4 * HOUR));
		provider.reservedResources.add(new Resource(2 * HOUR, 7 * HOUR));
		provider.reservedResources.add(new Resource(120 * HOUR, 135 * HOUR));
		provider.reservedResources.add(new Resource(120 * HOUR, 135 * HOUR));
		provider.reservedResources.add(new Resource(121.5 * HOUR, 134 * HOUR));
		provider.reservedResources.add(new Resource(189.1 * HOUR, 204.33 * HOUR));
		
		assertEquals(600 + 68 * reservedCpuCost + 68 * monitoringCost, provider.calculateCost(68, 0), 0.0d);
	}
	
	@Test
	public void calculateCostForReservedResourcesWithInvalidDuration(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		String transferInLimits = "";
		String transferInCosts = "";
		String transferOutLimits = "";
		String transferOutCosts = "";
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		provider.reservedResources.add(new Resource(9 * HOUR, 4 * HOUR));
		
		try{
			provider.calculateCost(68, 0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void calculateCostOnDemandResources(){
		double onDemandCpuCost = 0.85;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.35;
		double reservationOneYearFee = 99.123;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		String transferInLimits = "";
		String transferInCosts = "";
		String transferOutLimits = "";
		String transferOutCosts = "";
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		provider.onDemandResources.add(new Resource(1 * HOUR, 3 * HOUR));
		provider.onDemandResources.add(new Resource(6 * HOUR, 7 * HOUR));
		provider.onDemandResources.add(new Resource(120 * HOUR, 135 * HOUR));
		provider.onDemandResources.add(new Resource(133.8 * HOUR, 135 * HOUR));
		provider.onDemandResources.add(new Resource(378.01 * HOUR, 383.15 * HOUR));
		
		assertEquals( 26 * onDemandCpuCost + 26 * monitoringCost, provider.calculateCost(26, 0), 0.0d);
	}
	
	@Test
	public void calculateCostOnDemandResourcesWithInvalidDuration(){
		double onDemandCpuCost = 0.85;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.35;
		double reservationOneYearFee = 99.123;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		String transferInLimits = "";
		String transferInCosts = "";
		String transferOutLimits = "";
		String transferOutCosts = "";
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		provider.onDemandResources.add(new Resource(999 * HOUR, 383.15 * HOUR));
		
		try{
			provider.calculateCost(26, 0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void calculateCostForReservedAndOnDemandResources(){
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
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		provider.reservedResources.add(new Resource(0 * HOUR, 10 * HOUR));
		provider.reservedResources.add(new Resource(1 * HOUR, 21 * HOUR));
		provider.reservedResources.add(new Resource(120 * HOUR, 135 * HOUR));
		provider.reservedResources.add(new Resource(120 * HOUR, 135 * HOUR));
		provider.reservedResources.add(new Resource(120 * HOUR, 135 * HOUR));
		provider.reservedResources.add(new Resource(121.5 * HOUR, 134 * HOUR));
		provider.reservedResources.add(new Resource(189.1 * HOUR, 204.33 * HOUR));
		
		provider.onDemandResources.add(new Resource(121 * HOUR, 139 * HOUR));
		provider.onDemandResources.add(new Resource(122 * HOUR, 200.5 * HOUR));
		provider.onDemandResources.add(new Resource(133.8 * HOUR, 135 * HOUR));
		provider.onDemandResources.add(new Resource(378.01 * HOUR, 383.15 * HOUR));
		
		assertEquals( 7 * reservationOneYearFee + 104 * reservedCpuCost + 104 * monitoringCost +
				105 * onDemandCpuCost + 105 * monitoringCost, provider.calculateCost(209, 0), 0.0d);
	}
}