package commons.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import commons.sim.jeevent.JEEventScheduler;
import commons.util.Triple;


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
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 1d * HOUR_IN_MILLIS;
		triple.secondValue = 5d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine1ID, triple);
		
		Long machine2ID = 2l;
		Triple<Double, Double, Double> triple2 = new Triple<Double, Double, Double>();
		triple2.firstValue = 0d;
		triple2.secondValue = 5d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine2ID, triple2);
		
		Long machine3ID = 3l;
		Triple<Double, Double, Double> triple3 = new Triple<Double, Double, Double>();
		triple3.firstValue = 3d * HOUR_IN_MILLIS;
		triple3.secondValue = 18d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine3ID, triple3);
		
		Long machine4ID = 4l;
		Triple<Double, Double, Double> triple4 = new Triple<Double, Double, Double>();
		triple4.firstValue = 0d;
		triple4.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine4ID, triple4);
		
		Long machine5ID = 5l;
		Triple<Double, Double, Double> triple5 = new Triple<Double, Double, Double>();
		triple5.firstValue = 5d * HOUR_IN_MILLIS;
		triple5.secondValue = 17.5d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine5ID, triple5);
		
		Long machine6ID = 6l;
		Triple<Double, Double, Double> triple6 = new Triple<Double, Double, Double>();
		triple6.firstValue = 0.5d * HOUR_IN_MILLIS;
		triple6.secondValue = 15.73d * HOUR_IN_MILLIS;
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
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = -5d * HOUR_IN_MILLIS;
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
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = 2d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine1ID, triple);
		
		Long machine2ID = 2l;
		Triple<Double, Double, Double> triple2 = new Triple<Double, Double, Double>();
		triple2.firstValue = 1d * HOUR_IN_MILLIS;
		triple2.secondValue = 2d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine2ID, triple2);
		
		Long machine3ID = 3l;
		Triple<Double, Double, Double> triple3 = new Triple<Double, Double, Double>();
		triple3.firstValue = 0d;
		triple3.secondValue = 15d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine3ID, triple3);
		
		Long machine4ID = 450l;
		Triple<Double, Double, Double> triple4 = new Triple<Double, Double, Double>();
		triple4.firstValue = 1d * HOUR_IN_MILLIS;
		triple4.secondValue = 2.2d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine4ID, triple4);
		
		Long machine5ID = 5l;
		Triple<Double, Double, Double> triple5 = new Triple<Double, Double, Double>();
		triple5.firstValue = 2d * HOUR_IN_MILLIS;
		triple5.secondValue = 7.14d * HOUR_IN_MILLIS;
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
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = -6d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine1ID, triple);
		
		try{
			provider.calculateCost(0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
		}
		
		//FIXME: Double value explodes with large value!
		triple = new Triple<Double, Double, Double>();
		triple.firstValue = 1000d * HOUR_IN_MILLIS;
		triple.secondValue = 400d * HOUR_IN_MILLIS;
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
		Triple<Double, Double, Double> triple = new Triple<Double, Double, Double>();
		triple.firstValue = 0d;
		triple.secondValue = 10d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine1ID, triple);
		
		Long machine2ID = 2l;
		Triple<Double, Double, Double> triple2 = new Triple<Double, Double, Double>();
		triple2.firstValue = 10d * HOUR_IN_MILLIS;
		triple2.secondValue = 30d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine2ID, triple2);
		
		Long machine3ID = 3l;
		Triple<Double, Double, Double> triple3 = new Triple<Double, Double, Double>();
		triple3.firstValue = 1d * HOUR_IN_MILLIS;
		triple3.secondValue = 16d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine3ID, triple3);
		
		Long machine4ID = 4l;
		Triple<Double, Double, Double> triple4 = new Triple<Double, Double, Double>();
		triple4.firstValue = 50d * HOUR_IN_MILLIS;
		triple4.secondValue = 65d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine4ID, triple4);
		
		Long machine5ID = 5l;
		Triple<Double, Double, Double> triple5 = new Triple<Double, Double, Double>();
		triple5.firstValue = 0d;
		triple5.secondValue = 15d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine5ID, triple5);
		
		Long machine6ID = 6l;
		Triple<Double, Double, Double> triple6 = new Triple<Double, Double, Double>();
		triple6.firstValue = 11.5d * HOUR_IN_MILLIS;
		triple6.secondValue = 24d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine6ID, triple6);
		
		Long machine7ID = 7l;
		Triple<Double, Double, Double> triple7 = new Triple<Double, Double, Double>();
		triple7.firstValue = 0d;
		triple7.secondValue = 15.23d * HOUR_IN_MILLIS;
		provider.reservedResources.put(machine7ID, triple7);
		
		Long machine8ID = 8l;
		Triple<Double, Double, Double> triple8 = new Triple<Double, Double, Double>();
		triple8.firstValue = 18d * HOUR_IN_MILLIS;
		triple8.secondValue = 36d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine8ID, triple8);
		
		Long machine9ID = 9l;
		Triple<Double, Double, Double> triple9 = new Triple<Double, Double, Double>();
		triple9.firstValue = 0d;
		triple9.secondValue = 78.5d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine9ID, triple9);
		
		Long machine10ID = 10l;
		Triple<Double, Double, Double> triple10 = new Triple<Double, Double, Double>();
		triple10.firstValue = 2.4d * HOUR_IN_MILLIS;
		triple10.secondValue = 3.6d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine10ID, triple10);
		
		Long machine11ID = 11l;
		Triple<Double, Double, Double> triple11 = new Triple<Double, Double, Double>();
		triple11.firstValue = 0d;
		triple11.secondValue = 5.14d * HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine11ID, triple11);
		
		assertEquals( 7 * reservationOneYearFee + 104 * reservedCpuCost + 104 * monitoringCost +
				105 * onDemandCpuCost + 105 * monitoringCost, provider.calculateCost(0), 0.0d);
	}
}