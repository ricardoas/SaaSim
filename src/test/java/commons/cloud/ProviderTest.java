package commons.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import commons.sim.jeevent.JEEventScheduler;
import commons.util.Triple;


public class ProviderTest {
	
	private String name = "prov";
	private static final long HOUR_IN_MILLIS = 1000 * 60 * 60;
	private JEEventScheduler scheduler;
	
	@Before
	public void setUp(){
		scheduler = new JEEventScheduler();
	}

	@Test
	public void providerWithInvalidCpuCost(){
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
		Long machine1ID = 1l;
		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
		triple.firstValue = 1 * HOUR_IN_MILLIS;
		triple.secondValue = 5 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine1ID, triple);
		
		Long machine2ID = 2l;
		Triple<Long, Long, Double> triple2 = new Triple<Long, Long, Double>();
		triple2.firstValue = 0l;
		triple2.secondValue = 5 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine2ID, triple2);
		
		Long machine3ID = 3l;
		Triple<Long, Long, Double> triple3 = new Triple<Long, Long, Double>();
		triple3.firstValue = 3 * HOUR_IN_MILLIS;
		triple3.secondValue = 18 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine3ID, triple3);
		
		Long machine4ID = 4l;
		Triple<Long, Long, Double> triple4 = new Triple<Long, Long, Double>();
		triple4.firstValue = 0l;
		triple4.secondValue = 15 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine4ID, triple4);
		
		Long machine5ID = 5l;
		Triple<Long, Long, Double> triple5 = new Triple<Long, Long, Double>();
		triple5.firstValue = 5 * HOUR_IN_MILLIS;
		triple5.secondValue = (long)(17.5d * HOUR_IN_MILLIS);
		provider.reservedResources.put(machine5ID, triple5);
		
		Long machine6ID = 6l;
		Triple<Long, Long, Double> triple6 = new Triple<Long, Long, Double>();
		triple6.firstValue = (long)(0.5d * HOUR_IN_MILLIS);
		triple6.secondValue = (long)(15.73d * HOUR_IN_MILLIS);
		provider.reservedResources.put(machine6ID, triple6);
		
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
		Long machine1ID = 1000l;
		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
		triple.firstValue = 0l;
		triple.secondValue = (long)(-5d * HOUR_IN_MILLIS);
		provider.reservedResources.put(machine1ID, triple);
		
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
		Long machine1ID = 1l;
		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
		triple.firstValue = 0l;
		triple.secondValue = 2 * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine1ID, triple);
		
		Long machine2ID = 2l;
		Triple<Long, Long, Double> triple2 = new Triple<Long, Long, Double>();
		triple2.firstValue = 1 * HOUR_IN_MILLIS;
		triple2.secondValue = 2 * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine2ID, triple2);
		
		Long machine3ID = 3l;
		Triple<Long, Long, Double> triple3 = new Triple<Long, Long, Double>();
		triple3.firstValue = 0l;
		triple3.secondValue = 15 * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine3ID, triple3);
		
		Long machine4ID = 450l;
		Triple<Long, Long, Double> triple4 = new Triple<Long, Long, Double>();
		triple4.firstValue = 1 * HOUR_IN_MILLIS;
		triple4.secondValue = (long)(2.2d * HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine4ID, triple4);
		
		Long machine5ID = 5l;
		Triple<Long, Long, Double> triple5 = new Triple<Long, Long, Double>();
		triple5.firstValue = 2 * HOUR_IN_MILLIS;
		triple5.secondValue = (long)(7.14d * HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine5ID, triple5);
		
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
		Long machine1ID = 1l;
		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
		triple.firstValue = 0l;
		triple.secondValue = (long)(-6d * HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine1ID, triple);
		
		try{
			provider.calculateCost(0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
		}
		
		//FIXME: Double value explodes with large value!
		triple = new Triple<Long, Long, Double>();
		triple.firstValue = 1000 * HOUR_IN_MILLIS;
		triple.secondValue = 400 * HOUR_IN_MILLIS;
		provider.onDemandResources.clear();
		provider.onDemandResources.put(machine1ID, triple);
		
		try{
			provider.calculateCost(0);
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
		Long machine1ID = 1l;
		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
		triple.firstValue = 0l;
		triple.secondValue = 10 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine1ID, triple);
		
		Long machine2ID = 2l;
		Triple<Long, Long, Double> triple2 = new Triple<Long, Long, Double>();
		triple2.firstValue = 10 * HOUR_IN_MILLIS;
		triple2.secondValue = 30 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine2ID, triple2);
		
		Long machine3ID = 3l;
		Triple<Long, Long, Double> triple3 = new Triple<Long, Long, Double>();
		triple3.firstValue = 1 * HOUR_IN_MILLIS;
		triple3.secondValue = 16 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine3ID, triple3);
		
		Long machine4ID = 4l;
		Triple<Long, Long, Double> triple4 = new Triple<Long, Long, Double>();
		triple4.firstValue = 50 * HOUR_IN_MILLIS;
		triple4.secondValue = 65 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine4ID, triple4);
		
		Long machine5ID = 5l;
		Triple<Long, Long, Double> triple5 = new Triple<Long, Long, Double>();
		triple5.firstValue = 0l;
		triple5.secondValue = 15 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine5ID, triple5);
		
		Long machine6ID = 6l;
		Triple<Long, Long, Double> triple6 = new Triple<Long, Long, Double>();
		triple6.firstValue = (long)(11.5d * HOUR_IN_MILLIS);
		triple6.secondValue = 24 * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine6ID, triple6);
		
		Long machine7ID = 7l;
		Triple<Long, Long, Double> triple7 = new Triple<Long, Long, Double>();
		triple7.firstValue = 0l;
		triple7.secondValue = (long)(15.23d * HOUR_IN_MILLIS);
		provider.reservedResources.put(machine7ID, triple7);
		
		Long machine8ID = 8l;
		Triple<Long, Long, Double> triple8 = new Triple<Long, Long, Double>();
		triple8.firstValue = 18 * HOUR_IN_MILLIS;
		triple8.secondValue = 36 * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine8ID, triple8);
		
		Long machine9ID = 9l;
		Triple<Long, Long, Double> triple9 = new Triple<Long, Long, Double>();
		triple9.firstValue = 0l;
		triple9.secondValue = (long)(78.5d * HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine9ID, triple9);
		
		Long machine10ID = 10l;
		Triple<Long, Long, Double> triple10 = new Triple<Long, Long, Double>();
		triple10.firstValue = (long)(2.4d * HOUR_IN_MILLIS);
		triple10.secondValue = (long)(3.6d * HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine10ID, triple10);
		
		Long machine11ID = 11l;
		Triple<Long, Long, Double> triple11 = new Triple<Long, Long, Double>();
		triple11.firstValue = 0l;
		triple11.secondValue = (long)(5.14d * HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine11ID, triple11);
		
		assertEquals( 7 * reservationOneYearFee + 104 * reservedCpuCost + 104 * monitoringCost +
				105 * onDemandCpuCost + 105 * monitoringCost, provider.calculateCost(0), 0.0d);
	}
}