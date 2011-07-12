package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Test;


public class ProviderTest {
	
	private String name = "prov";
	public static final int HOUR = 1;

	@Test
	public void providerWithInvalidCpuCost(){
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
		
		try{
			new Provider(name, -0.09, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidCpuLimit(){
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
		
		try{
			new Provider(name, onDemandCpuCost, -99999, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, 0, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidReservedCpuLimit(){
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
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, 0, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, -777, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidReservedCpuCost(){
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
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					-reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					-13.9787656, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidReservationFee(){
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
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, -reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, 0, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		}catch(RuntimeException e){
			fail("Invalid provider!");
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, -reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, 0, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		}catch(RuntimeException e){
			fail("Invalid provider!");
		}
	}
	
	@Test
	public void providerWithInvalidMonitoringCost(){
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
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, -monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, 0, reservationThreeYearsFee, -0.0000001, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
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
		Machine machine1 = new Machine(1);
		machine1.totalProcessed = 4 * HOUR;
		provider.reservedResources.add(machine1);
		Machine machine2 = new Machine(2);
		machine2.totalProcessed = 5 * HOUR;
		provider.reservedResources.add(machine2);
		Machine machine3 = new Machine(3);
		machine3.totalProcessed = 15 * HOUR;
		provider.reservedResources.add(machine3);
		Machine machine4 = new Machine(4);
		machine4.totalProcessed = 15 * HOUR;
		provider.reservedResources.add(machine4);
		Machine machine5 = new Machine(5);
		machine5.totalProcessed = 12.5 * HOUR;
		provider.reservedResources.add(machine5);
		Machine machine6 = new Machine(6);
		machine6.totalProcessed = 15.23 * HOUR;
		provider.reservedResources.add(machine6);
		
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
		Machine machine1 = new Machine(1);
		machine1.totalProcessed = -5 * HOUR;
		provider.reservedResources.add(machine1);
		
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
		Machine machine1 = new Machine(1);
		machine1.totalProcessed = 2 * HOUR;
		provider.onDemandResources.add(machine1);
		Machine machine2 = new Machine(2);
		machine2.totalProcessed = 1 * HOUR;
		provider.onDemandResources.add(machine2);
		Machine machine3 = new Machine(3);
		machine3.totalProcessed = 15 * HOUR;
		provider.onDemandResources.add(machine3);
		Machine machine4 = new Machine(1);
		machine4.totalProcessed = 1.2 * HOUR;
		provider.onDemandResources.add(machine4);
		Machine machine5 = new Machine(5);
		machine5.totalProcessed = 5.14 * HOUR;
		provider.onDemandResources.add(machine5);
		
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
		Machine machine1 = new Machine(1);
		machine1.totalProcessed = -600 * HOUR;
		provider.onDemandResources.add(machine1);
		
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
		Machine machine1 = new Machine(1);
		machine1.totalProcessed = 10 * HOUR;
		provider.reservedResources.add(machine1);
		Machine machine2 = new Machine(2);
		machine2.totalProcessed = 20 * HOUR;
		provider.reservedResources.add(machine2);
		Machine machine3 = new Machine(3);
		machine3.totalProcessed = 15 * HOUR;
		provider.reservedResources.add(machine3);
		Machine machine4 = new Machine(4);
		machine4.totalProcessed = 15 * HOUR;
		provider.reservedResources.add(machine4);
		Machine machine5 = new Machine(5);
		machine5.totalProcessed = 15 * HOUR;
		provider.reservedResources.add(machine5);
		Machine machine6 = new Machine(6);
		machine6.totalProcessed = 12.5 * HOUR;
		provider.reservedResources.add(machine6);
		Machine machine7 = new Machine(7);
		machine7.totalProcessed = 15.23 * HOUR;
		provider.reservedResources.add(machine7);
		
		Machine machine8 = new Machine(8);
		machine8.totalProcessed = 18 * HOUR;
		provider.onDemandResources.add(machine8);
		Machine machine9 = new Machine(9);
		machine9.totalProcessed = 78.5 * HOUR;
		provider.onDemandResources.add(machine9);
		Machine machine10 = new Machine(10);
		machine10.totalProcessed = 1.2 * HOUR;
		provider.onDemandResources.add(machine10);
		Machine machine11 = new Machine(11);
		machine11.totalProcessed = 5.14 * HOUR;
		provider.onDemandResources.add(machine11);
		
		assertEquals( 7 * reservationOneYearFee + 104 * reservedCpuCost + 104 * monitoringCost +
				105 * onDemandCpuCost + 105 * monitoringCost, provider.calculateCost(209, 0), 0.0d);
	}
}