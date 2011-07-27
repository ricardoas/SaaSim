package commons.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;


public class ProviderTest {
	
	private String name = "prov";
	private static final int HOUR_IN_MILLIS = 1000 * 60 * 60;
	private JEEventScheduler scheduler;
	
	@Before
	public void setUp(){
		scheduler = new JEEventScheduler();
	}

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
		Machine machine1 = new Machine(scheduler, 1);
		machine1.setTotalProcessed(4 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine1);
		Machine machine2 = new Machine(scheduler, 2);
		machine2.setTotalProcessed(5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine2);
		Machine machine3 = new Machine(scheduler, 3);
		machine3.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine3);
		Machine machine4 = new Machine(scheduler, 4);
		machine4.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine4);
		Machine machine5 = new Machine(scheduler, 5);
		machine5.setTotalProcessed(12.5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine5);
		Machine machine6 = new Machine(scheduler, 6);
		machine6.setTotalProcessed(15.23 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine6);
		
		assertEquals(600 + 68 * reservedCpuCost + 68 * monitoringCost, provider.calculateCost(0), 0.0d);
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
		Machine machine1 = new Machine(scheduler, 1);
		machine1.setTotalProcessed(-5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine1);
		
		try{
			provider.calculateCost(0);
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
		Machine machine1 = new Machine(scheduler, 1);
		machine1.setTotalProcessed(2 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine1);
		Machine machine2 = new Machine(scheduler, 2);
		machine2.setTotalProcessed(1 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine2);
		Machine machine3 = new Machine(scheduler, 3);
		machine3.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine3);
		Machine machine4 = new Machine(scheduler, 1);
		machine4.setTotalProcessed(1.2 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine4);
		Machine machine5 = new Machine(scheduler, 5);
		machine5.setTotalProcessed(5.14 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine5);
		
		assertEquals( 26 * onDemandCpuCost + 26 * monitoringCost, provider.calculateCost(0), 0.0d);
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
		Machine machine1 = new Machine(scheduler, 1);
		machine1.setTotalProcessed(-6 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine1);
		
		try{
			provider.calculateCost(0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
		}
		
		//FIXME: Double value explodes with large value!
		machine1.setTotalProcessed(-600 * HOUR_IN_MILLIS);
		provider.onDemandResources.clear();
		provider.onDemandResources.add(machine1);
		
		try{
			provider.calculateCost(0);
		}catch(RuntimeException e){
			fail("Invalid resource consumption!");
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
		Machine machine1 = new Machine(scheduler, 1);
		machine1.setTotalProcessed(10 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine1);
		Machine machine2 = new Machine(scheduler, 2);
		machine2.setTotalProcessed(20 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine2);
		Machine machine3 = new Machine(scheduler, 3);
		machine3.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine3);
		Machine machine4 = new Machine(scheduler, 4);
		machine4.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine4);
		Machine machine5 = new Machine(scheduler, 5);
		machine5.setTotalProcessed(15 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine5);
		Machine machine6 = new Machine(scheduler, 6);
		machine6.setTotalProcessed(12.5 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine6);
		Machine machine7 = new Machine(scheduler, 7);
		machine7.setTotalProcessed(15.23 * HOUR_IN_MILLIS);
		provider.reservedResources.add(machine7);
		
		Machine machine8 = new Machine(scheduler, 8);
		machine8.setTotalProcessed(18 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine8);
		Machine machine9 = new Machine(scheduler, 9);
		machine9.setTotalProcessed(78.5 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine9);
		Machine machine10 = new Machine(scheduler, 10);
		machine10.setTotalProcessed(1.2 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine10);
		Machine machine11 = new Machine(scheduler, 11);
		machine11.setTotalProcessed(5.14 * HOUR_IN_MILLIS);
		provider.onDemandResources.add(machine11);
		
		assertEquals( 7 * reservationOneYearFee + 104 * reservedCpuCost + 104 * monitoringCost +
				105 * onDemandCpuCost + 105 * monitoringCost, provider.calculateCost(0), 0.0d);
	}
}