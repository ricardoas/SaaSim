package planning.heuristic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import planning.util.Summary;
import util.MockedConfigurationTest;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.TypeProvider;
import commons.cloud.User;
import commons.config.Configuration;
import commons.io.TickSize;
import commons.sim.util.SimulatorProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class PlanningFitnessFunctionTest extends MockedConfigurationTest {
	
	private final long DEFAULT_SLA = 8;
	
	@Test
	public void calculatePenaltyWithoutLoss(){
		Contract contract = new Contract("p1", 1, 100d, 555d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(1, contract, 100);
		cloudUsers[1] = new User(2, contract2, 100);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeRequestsLost = 0;
		double requestsThatCouldNotBeAttended = 0;
		double totalRequestsFinished = 100;
		
		assertEquals(0, function.calcPenalties(responseTimeRequestsLost, requestsThatCouldNotBeAttended, totalRequestsFinished), 0.00001);
		PowerMock.verifyAll();
	}
	
	@Test
	public void calculatePenaltyWithResponseTimeLoss(){
		Contract contract = new Contract("p1", 1, 100d, 555d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(1, contract, 100);
		cloudUsers[1] = new User(2, contract2, 100);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeRequestsLost = 30;
		double requestsThatCouldNotBeAttended = 0;
		double totalRequestsFinished = 70;
		
		double expectedPenalty = contract.calculatePenalty(0.2142857) + contract2.calculatePenalty(0.2142857);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeRequestsLost, requestsThatCouldNotBeAttended, totalRequestsFinished), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void calculatePenaltyWithRequestsAtendedLoss(){
		Contract contract = new Contract("p1", 1, 100d, 555d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(1, contract, 100);
		cloudUsers[1] = new User(2, contract2, 100);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeRequestsLost = 0;
		double requestsThatCouldNotBeAttended = 25;
		double totalRequestsFinished = 80;
		
		double expectedPenalty = contract.calculatePenalty(0.15625) + contract2.calculatePenalty(0.15625);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeRequestsLost, requestsThatCouldNotBeAttended, totalRequestsFinished), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void calculatePenaltyWithBothLoss(){
		Contract contract = new Contract("p1", 1, 100d, 555d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(1, contract, 100);
		cloudUsers[1] = new User(2, contract2, 100);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeRequestsLost = 30;
		double requestsThatCouldNotBeAttended = 15;
		double totalRequestsFinished = 60;
		
		double expectedPenalty = contract.calculatePenalty(0.375) + contract2.calculatePenalty(0.375);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeRequestsLost, requestsThatCouldNotBeAttended, totalRequestsFinished), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void calculatePenaltyWithTotalLoss(){
		Contract contract = new Contract("p1", 1, 100d, 555d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(1, contract, 100);
		cloudUsers[1] = new User(2, contract2, 100);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeRequestsLost = 30;
		double requestsThatCouldNotBeAttended = 30;
		double totalRequestsFinished = 0;
		
		double expectedPenalty = contract.calculatePenalty(1) + contract2.calculatePenalty(1);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeRequestsLost, requestsThatCouldNotBeAttended, totalRequestsFinished), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWith2MachineTypesAndNoOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.6, 0.25, 99, 188, 5));
		
		User[] cloudUsers = new User[0];
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> requestsFinishedPerMachineType = new HashMap<MachineType, Double>();
		requestsFinishedPerMachineType.put(MachineType.LARGE, 10d * 31104000);
		requestsFinishedPerMachineType.put(MachineType.MEDIUM, 6d * 31104000);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30 );//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16 );//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 432 + 6480;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(requestsFinishedPerMachineType, meanServiceTimeInMillis, 
				currentPowerPerMachineType, 0, 0), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWith2MachineTypesAndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.2, 0.1, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.LARGE, 5d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.MEDIUM, 3d * 31104000);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 216 + 3240 + 0.6;//One year fee for large + one year fee for medium + large usage + medium usage + on-demand cost
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 21600), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This test is similar to {@link PlanningFitnessFunctionTest#testCalculateCostWith2MachineTypesAndOnDemandResources()}. The main
	 * difference is that in this test the amount of on demand machines requested is above the allowed limit by the provider.
	 */
	@Test
	public void testCalculateCostWith2MachineTypesAndOnDemandResources2(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.2, 0.1, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.LARGE, 5d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.MEDIUM, 3d * 31104000);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 216 + 3240 + 17280;//One year fee for large + one year fee for medium + large usage + medium usage + on-demand cost
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 622080900), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithOnlyOneThroughput(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.2, 0.1, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.LARGE, 3.3333d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8 + 143.9986;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 0), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithOnlyOneThroughputAndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.LARGE, 3.3333d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8 + 143.9986 + 2160;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 5 * 31104000), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		assertEquals(0, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 0), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand2(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.LARGE, 10d);
		finishedRequestsPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 0;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 0), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand2AndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.LARGE, 10d);
		finishedRequestsPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 0;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 10 * 31104000d), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateReceiptWithExtraConsumedCpu(){
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		PowerMock.replayAll(config);
		
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
		long cpuLimitInMillis2 = 500 * 60 * 60 * 1000l;
		
		Contract contract = new Contract("p1", 1, setupCost, price, cpuLimitInMillis, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, cpuLimitInMillis2, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(1, 20, 500, 5, 100));
		data.add(new Summary(1, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(11, 20, 500, 5, 100));
		data.add(new Summary(12, 20, 500, 5, 100));
		data.add(new Summary(13, 20, 500, 5, 100));
		data.add(new Summary(14, 20, 500, 5, 100));
		data.add(new Summary(11, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(1, 20, 500, 5, 100));
		data.add(new Summary(1, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(11, 20, 500, 5, 100));
		data.add(new Summary(12, 20, 500, 5, 100));
		data.add(new Summary(13, 20, 500, 5, 100));
		data.add(new Summary(14, 20, 500, 5, 100));
		data.add(new Summary(11, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		data.add(new Summary(7, 20, 500, 5, 100));
		summaries.put(cloudUsers[0], data);//320 extra cpu-hrs
//		summaries.put(cloudUsers[1], data2);//100 extra cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost + price + 320 * 0.1;
		assertEquals(receipt, function.calcReceipt(31), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateReceiptWithoutDemand(){
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(1);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		PowerMock.replayAll(config);		
		
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
		long cpuLimitInMillis2 = 500 * 60 * 60 * 1000l;
		
		Contract contract = new Contract("p1", 1, setupCost, price, cpuLimitInMillis, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, cpuLimitInMillis2, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		summaries.put(cloudUsers[0], data);//0 cpu-hrs
		
		List<Summary> data2 = new ArrayList<Summary>();
		summaries.put(cloudUsers[1], data2);//0 cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2;
		assertEquals(receipt, function.calcReceipt(31), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateReceiptWithoutExtraCpu(){
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(1);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		PowerMock.replayAll(config);
		
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		long cpuLimitInMillis = 640 * 60 * 60 * 1000l;
		long cpuLimitInMillis2 = 530 * 60 * 60 * 1000l;
		
		Contract contract = new Contract("p1", 1, setupCost, price, cpuLimitInMillis, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, cpuLimitInMillis2, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 40, 500, 5, 100));
		data.add(new Summary(10, 70, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(12, 80, 500, 5, 100));
		data.add(new Summary(13, 90, 500, 5, 100));
		data.add(new Summary(14, 90, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(7, 60, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		summaries.put(cloudUsers[0], data);//640 cpu-hrs
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(5, 40, 500, 5, 100));
		data2.add(new Summary(10, 40, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(12, 80, 500, 5, 100));
		data2.add(new Summary(13, 50, 500, 5, 100));
		data2.add(new Summary(14, 50, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(7, 60, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + price + price2;
		assertEquals(receipt, function.calcReceipt(31), 0.0001);
	}
	
	@Test
	public void testCalculateReceiptWithoutExtraCpuAndMoreThanOneMonth(){
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(1);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		PowerMock.replayAll(config);
		
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		long cpuLimitInMillis = 640 * 60 * 60 * 1000l;
		long cpuLimitInMillis2 = 530 * 60 * 60 * 1000l;
		
		Contract contract = new Contract("p1", 1, setupCost, price, cpuLimitInMillis, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, cpuLimitInMillis2, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 40, 500, 5, 100));
		data.add(new Summary(10, 70, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(12, 80, 500, 5, 100));
		data.add(new Summary(13, 90, 500, 5, 100));
		data.add(new Summary(14, 90, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(7, 60, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 40, 500, 5, 100));
		data.add(new Summary(10, 70, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(12, 80, 500, 5, 100));
		data.add(new Summary(13, 90, 500, 5, 100));
		data.add(new Summary(14, 90, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(7, 60, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		summaries.put(cloudUsers[0], data);//640 cpu-hrs
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(5, 40, 500, 5, 100));
		data2.add(new Summary(10, 40, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(12, 80, 500, 5, 100));
		data2.add(new Summary(13, 50, 500, 5, 100));
		data2.add(new Summary(14, 50, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(7, 60, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(5, 40, 500, 5, 100));
		data2.add(new Summary(10, 40, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(12, 80, 500, 5, 100));
		data2.add(new Summary(13, 50, 500, 5, 100));
		data2.add(new Summary(14, 50, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(7, 60, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + 2 * price + 2 * price2;
		assertEquals(receipt, function.calcReceipt(59), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateReceiptWithExtraCpuAndMoreThanOneMonth(){
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(1);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		PowerMock.replayAll(config);
		
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		long cpuLimitInMillis = 640 * 60 * 60 * 1000l;
		long cpuLimitInMillis2 = 530 * 60 * 60 * 1000l;
		
		Contract contract = new Contract("p1", 1, setupCost, price, cpuLimitInMillis, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, cpuLimitInMillis2, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 40, 500, 5, 100));
		data.add(new Summary(10, 70, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(12, 80, 500, 5, 100));
		data.add(new Summary(13, 90, 500, 5, 100));
		data.add(new Summary(14, 90, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(7, 60, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 10, 500, 5, 100));
		data.add(new Summary(7, 10, 500, 5, 100));
		data.add(new Summary(7, 10, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 40, 500, 5, 100));
		data.add(new Summary(10, 70, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(12, 80, 500, 5, 100));
		data.add(new Summary(13, 90, 500, 5, 100));
		data.add(new Summary(14, 90, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 5, 100));
		data.add(new Summary(7, 60, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 10, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		data.add(new Summary(7, 0, 500, 5, 100));
		summaries.put(cloudUsers[0], data);//30 extra cpu-hours at first month, 10 cpu-hours at second
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(5, 40, 500, 5, 100));
		data2.add(new Summary(10, 40, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(12, 80, 500, 5, 100));
		data2.add(new Summary(13, 50, 500, 5, 100));
		data2.add(new Summary(14, 50, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(7, 60, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 50, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(5, 40, 500, 5, 100));
		data2.add(new Summary(10, 40, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(12, 80, 500, 5, 100));
		data2.add(new Summary(13, 50, 500, 5, 100));
		data2.add(new Summary(14, 50, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(7, 60, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 5, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 3, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		summaries.put(cloudUsers[1], data2);//50 extra cpu-hrs at first month, 8 cpu-hours at second 
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + 2 * price + 2 * price2 + 40 * 0.1 + 58 * 0.2;
		assertEquals(receipt, function.calcReceipt(59), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testAggregateThinkTimeInCorrectOrder(){
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		
		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(1, 10, 500, 10, 100));
		data.add(new Summary(1, 10, 500, 5, 100));
		data.add(new Summary(2, 20, 500, 10, 100));
		data.add(new Summary(2, 20, 500, 5, 100));
		data.add(new Summary(5, 40, 500, 10, 100));
		data.add(new Summary(10, 70, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 10, 100));
		data.add(new Summary(12, 80, 500, 5, 100));
		data.add(new Summary(13, 90, 500, 10, 100));
		data.add(new Summary(14, 90, 500, 5, 100));
		data.add(new Summary(11, 75, 500, 10, 100));
		data.add(new Summary(7, 60, 500, 5, 100));
		summaries.put(cloudUsers[0], data);//640 cpu-hrs
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(1, 10, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(2, 20, 500, 5, 100));
		data2.add(new Summary(5, 40, 500, 5, 100));
		data2.add(new Summary(10, 40, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(12, 80, 500, 5, 100));
		data2.add(new Summary(13, 50, 500, 5, 100));
		data2.add(new Summary(14, 50, 500, 5, 100));
		data2.add(new Summary(11, 75, 500, 5, 100));
		data2.add(new Summary(7, 60, 500, 20, 100));
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(7.5, function.aggregateThinkTime(0), 0.0001);
		assertEquals(7.5, function.aggregateThinkTime(10), 0.0001);
		assertEquals(12.5, function.aggregateThinkTime(11), 0.0001);
	}
	
	@Test
	public void testAggregateThinkTimeInAnyOrder(){
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		
		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(1, 10, 500, 3, 100));
		data.add(new Summary(1, 10, 500, 15, 100));
		data.add(new Summary(2, 20, 500, 7, 100));
		data.add(new Summary(2, 20, 500, 7, 100));
		data.add(new Summary(5, 40, 500, 15, 100));
		data.add(new Summary(10, 70, 500, 3, 100));
		data.add(new Summary(11, 75, 500, 3, 100));
		data.add(new Summary(12, 80, 500, 15, 100));
		data.add(new Summary(13, 90, 500, 7, 100));
		data.add(new Summary(14, 90, 500, 15, 100));
		data.add(new Summary(11, 75, 500, 7, 100));
		data.add(new Summary(7, 60, 500, 3, 100));
		summaries.put(cloudUsers[0], data);//640 cpu-hrs
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 15, 100));
		data2.add(new Summary(1, 10, 500, 7, 100));
		data2.add(new Summary(2, 20, 500, 3, 100));
		data2.add(new Summary(2, 20, 500, 3, 100));
		data2.add(new Summary(5, 40, 500, 7, 100));
		data2.add(new Summary(10, 40, 500, 15, 100));
		data2.add(new Summary(11, 75, 500, 7, 100));
		data2.add(new Summary(12, 80, 500, 3, 100));
		data2.add(new Summary(13, 50, 500, 15, 100));
		data2.add(new Summary(14, 50, 500, 3, 100));
		data2.add(new Summary(11, 75, 500, 7, 100));
		data2.add(new Summary(7, 60, 500, 15, 100));
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(9, function.aggregateThinkTime(11), 0.0001);
		assertEquals(11, function.aggregateThinkTime(1), 0.0001);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAggregateThinkTimeWithInvalidIndex(){
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		
		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(1, 10, 500, 3, 100));
		data.add(new Summary(1, 10, 500, 15, 100));
		data.add(new Summary(2, 20, 500, 7, 100));
		data.add(new Summary(2, 20, 500, 7, 100));
		data.add(new Summary(5, 40, 500, 15, 100));
		data.add(new Summary(10, 70, 500, 3, 100));
		data.add(new Summary(11, 75, 500, 3, 100));
		data.add(new Summary(12, 80, 500, 15, 100));
		data.add(new Summary(13, 90, 500, 7, 100));
		data.add(new Summary(14, 90, 500, 15, 100));
		data.add(new Summary(11, 75, 500, 7, 100));
		data.add(new Summary(7, 60, 500, 3, 100));
		summaries.put(cloudUsers[0], data);//640 cpu-hrs
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 15, 100));
		data2.add(new Summary(1, 10, 500, 7, 100));
		data2.add(new Summary(2, 20, 500, 3, 100));
		data2.add(new Summary(2, 20, 500, 3, 100));
		data2.add(new Summary(5, 40, 500, 7, 100));
		data2.add(new Summary(10, 40, 500, 15, 100));
		data2.add(new Summary(11, 75, 500, 7, 100));
		data2.add(new Summary(12, 80, 500, 3, 100));
		data2.add(new Summary(13, 50, 500, 15, 100));
		data2.add(new Summary(14, 50, 500, 3, 100));
		data2.add(new Summary(11, 75, 500, 7, 100));
		data2.add(new Summary(7, 60, 500, 15, 100));
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		function.aggregateThinkTime(12);
	}
	
//	@Test
//	public void testAggregateNumberOfUsers(){
//		double setupCost = 100d;
//		double price = 555d;
//		double price2 = 99.765d;
//		
//		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(1, 10, 500, 3, 100));
//		data.add(new Summary(1, 10, 500, 15, 100));
//		data.add(new Summary(2, 20, 500, 7, 100));
//		data.add(new Summary(2, 20, 500, 7, 100));
//		data.add(new Summary(5, 40, 500, 15, 100));
//		data.add(new Summary(10, 70, 500, 3, 100));
//		data.add(new Summary(11, 75, 500, 3, 100));
//		data.add(new Summary(12, 80, 500, 15, 100));
//		data.add(new Summary(13, 90, 500, 7, 100));
//		data.add(new Summary(14, 90, 500, 15, 100));
//		data.add(new Summary(11, 75, 500, 7, 100));
//		data.add(new Summary(7, 60, 500, 3, 100));
//		summaries.put(cloudUsers[0], data);//640 cpu-hrs
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(1, 10, 500, 15, 100));
//		data2.add(new Summary(1, 10, 500, 7, 100));
//		data2.add(new Summary(2, 20, 500, 3, 100));
//		data2.add(new Summary(2, 20, 500, 3, 100));
//		data2.add(new Summary(5, 40, 500, 7, 100));
//		data2.add(new Summary(10, 40, 500, 15, 100));
//		data2.add(new Summary(11, 75, 500, 7, 100));
//		data2.add(new Summary(12, 80, 500, 3, 100));
//		data2.add(new Summary(13, 50, 500, 15, 100));
//		data2.add(new Summary(14, 50, 500, 3, 100));
//		data2.add(new Summary(11, 75, 500, 7, 100));
//		data2.add(new Summary(7, 60, 500, 15, 100));
//		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
//		
//		assertEquals(200, function.aggregateNumberOfUsers(), 0.0001);
//	}
//	
//	@Test
//	public void testAggregateNumberOfUsers2(){
//		double setupCost = 100d;
//		double price = 555d;
//		double price2 = 99.765d;
//		
//		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(1, 10, 500, 3, 100));
//		data.add(new Summary(1, 10, 500, 15, 200));
//		data.add(new Summary(2, 20, 500, 7, 300));
//		data.add(new Summary(2, 20, 500, 7, 400));
//		data.add(new Summary(5, 40, 500, 15, 500));
//		data.add(new Summary(10, 70, 500, 3, 500));
//		data.add(new Summary(11, 75, 500, 3, 600));
//		data.add(new Summary(12, 80, 500, 15, 700));
//		data.add(new Summary(13, 90, 500, 7, 800));
//		data.add(new Summary(14, 90, 500, 15, 900));
//		data.add(new Summary(11, 75, 500, 7, 1000));
//		data.add(new Summary(7, 60, 500, 3, 1100));
//		summaries.put(cloudUsers[0], data);//7100 users
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(1, 10, 500, 15, 1));
//		data2.add(new Summary(1, 10, 500, 7, 5));
//		data2.add(new Summary(2, 20, 500, 3, 6));
//		data2.add(new Summary(2, 20, 500, 3, 9));
//		data2.add(new Summary(5, 40, 500, 7, 11));
//		data2.add(new Summary(10, 40, 500, 15, 23));
//		data2.add(new Summary(11, 75, 500, 7, 35));
//		data2.add(new Summary(12, 80, 500, 3, 99));
//		data2.add(new Summary(13, 50, 500, 15, 88));
//		data2.add(new Summary(14, 50, 500, 3, 77));
//		data2.add(new Summary(11, 75, 500, 7, 99));
//		data2.add(new Summary(7, 60, 500, 15, 1));
//		summaries.put(cloudUsers[1], data2);//454 users
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
//		
//		assertEquals(628, function.aggregateNumberOfUsers(), 0.0001);
//	}
//	
//	@Test
//	public void testAggregateNumberOfUsersWithoutUsers(){
//		double setupCost = 100d;
//		double price = 555d;
//		double price2 = 99.765d;
//		
//		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(1, 10, 500, 0, 0));
//		data.add(new Summary(1, 10, 500, 0, 0));
//		data.add(new Summary(2, 20, 500, 0, 0));
//		data.add(new Summary(2, 20, 500, 0, 0));
//		data.add(new Summary(5, 40, 500, 0, 0));
//		data.add(new Summary(10, 70, 500, 0, 0));
//		data.add(new Summary(11, 75, 500, 0, 0));
//		data.add(new Summary(12, 80, 500, 0, 0));
//		data.add(new Summary(13, 90, 500, 0, 0));
//		data.add(new Summary(14, 90, 500, 0, 0));
//		data.add(new Summary(11, 75, 500, 0, 0));
//		data.add(new Summary(7, 60, 500, 0, 0));
//		summaries.put(cloudUsers[0], data);
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(1, 10, 500, 0, 0));
//		data2.add(new Summary(1, 10, 500, 0, 0));
//		data2.add(new Summary(2, 20, 500, 0, 0));
//		data2.add(new Summary(2, 20, 500, 0, 0));
//		data2.add(new Summary(5, 40, 500, 0, 0));
//		data2.add(new Summary(10, 40, 500, 0, 0));
//		data2.add(new Summary(11, 75, 500, 0, 0));
//		data2.add(new Summary(12, 80, 500, 0, 0));
//		data2.add(new Summary(13, 50, 500, 0, 0));
//		data2.add(new Summary(14, 50, 500, 0, 0));
//		data2.add(new Summary(11, 75, 500, 0, 0));
//		data2.add(new Summary(7, 60, 500, 0, 0));
//		summaries.put(cloudUsers[1], data2);
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
//		
//		assertEquals(0, function.aggregateNumberOfUsers(), 0.0001);
//	}
//	
//	@Test
//	public void testAggregateServiceTimeForOneSaaSClient(){
//		double setupCost = 100d;
//		double price = 555d;
//		double price2 = 99.765d;
//		
//		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(1, 10, 500, 0, 100));
//		data.add(new Summary(1, 10, 600, 0, 200));
//		data.add(new Summary(2, 20, 400, 0, 300));
//		data.add(new Summary(2, 20, 700, 0, 400));
//		data.add(new Summary(5, 40, 300, 0, 500));
//		data.add(new Summary(10, 70, 800, 0, 500));
//		data.add(new Summary(11, 75, 200, 0, 600));
//		data.add(new Summary(12, 80, 500, 0, 700));
//		data.add(new Summary(13, 90, 500, 0, 800));
//		data.add(new Summary(14, 90, 900, 0, 900));
//		data.add(new Summary(11, 75, 100, 0, 1000));
//		data.add(new Summary(7, 60, 500, 0, 1100));
//		summaries.put(cloudUsers[0], data);
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
//		
//		assertEquals(500, function.aggregateServiceTime(), 0.0001);
//	}
//	
//	@Test
//	public void testAggregateServiceTimeForMultipleSaaSClients(){
//		double setupCost = 100d;
//		double price = 555d;
//		double price2 = 99.765d;
//		
//		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(1, 10, 1000, 3, 100));
//		data.add(new Summary(1, 10, 2000, 15, 200));
//		data.add(new Summary(2, 20, 500, 7, 300));
//		data.add(new Summary(2, 20, 2500, 7, 400));
//		data.add(new Summary(5, 40, 3000, 15, 500));
//		data.add(new Summary(10, 70, 1000, 3, 500));
//		data.add(new Summary(11, 75, 2000, 3, 600));
//		data.add(new Summary(12, 80, 2500, 15, 700));
//		data.add(new Summary(13, 90, 2500, 7, 800));
//		data.add(new Summary(14, 90, 3000, 15, 900));
//		data.add(new Summary(11, 75, 1000, 7, 1000));
//		data.add(new Summary(7, 60, 2000, 3, 1100));
//		summaries.put(cloudUsers[0], data);//Average service time: 1916.667
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(1, 10, 100, 15, 1));
//		data2.add(new Summary(1, 10, 200, 7, 5));
//		data2.add(new Summary(2, 20, 300, 3, 6));
//		data2.add(new Summary(2, 20, 400, 3, 9));
//		data2.add(new Summary(5, 40, 500, 7, 11));
//		data2.add(new Summary(10, 40, 600, 15, 23));
//		data2.add(new Summary(11, 75, 700, 7, 35));
//		data2.add(new Summary(12, 80, 800, 3, 99));
//		data2.add(new Summary(13, 50, 900, 15, 88));
//		data2.add(new Summary(14, 50, 1000, 3, 77));
//		data2.add(new Summary(11, 75, 1000, 7, 99));
//		data2.add(new Summary(7, 60, 1000, 15, 1));
//		summaries.put(cloudUsers[1], data2);//Average service time: 625
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
//		
//		assertEquals(1270.8333333, function.aggregateServiceTime(), 0.0001);
//	}
//	
//	@Test
//	public void testAggregateArrivalsForOneSaaSClient(){
//		double setupCost = 100d;
//		double price = 555d;
//		double price2 = 99.765d;
//		
//		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(1, 10, 500, 0, 100));
//		data.add(new Summary(1, 10, 600, 0, 200));
//		data.add(new Summary(2, 20, 400, 0, 300));
//		data.add(new Summary(2, 20, 700, 0, 400));
//		data.add(new Summary(5, 40, 300, 0, 500));
//		data.add(new Summary(10, 70, 800, 0, 500));
//		data.add(new Summary(11, 75, 200, 0, 600));
//		data.add(new Summary(12, 80, 500, 0, 700));
//		data.add(new Summary(13, 90, 500, 0, 800));
//		data.add(new Summary(14, 90, 900, 0, 900));
//		data.add(new Summary(11, 75, 100, 0, 1000));
//		data.add(new Summary(7, 60, 500, 0, 1100));
//		summaries.put(cloudUsers[0], data);
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
//		
//		assertEquals(7.416667, function.aggregateArrivals(), 0.0001);
//	}
//	
//	@Test
//	public void testAggregateArrivalsForMultipleSaaSClients(){
//		double setupCost = 100d;
//		double price = 555d;
//		double price2 = 99.765d;
//		
//		Contract contract = new Contract("p1", 1, setupCost, price, 640 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, 530 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(10, 10, 1000, 3, 100));
//		data.add(new Summary(20, 10, 2000, 15, 200));
//		data.add(new Summary(25, 20, 500, 7, 300));
//		data.add(new Summary(30, 20, 2500, 7, 400));
//		data.add(new Summary(40, 40, 3000, 15, 500));
//		data.add(new Summary(50, 70, 1000, 3, 500));
//		data.add(new Summary(55, 75, 2000, 3, 600));
//		data.add(new Summary(60, 80, 2500, 15, 700));
//		data.add(new Summary(40, 90, 2500, 7, 800));
//		data.add(new Summary(30, 90, 3000, 15, 900));
//		data.add(new Summary(25, 75, 1000, 7, 1000));
//		data.add(new Summary(5, 60, 2000, 3, 1100));
//		summaries.put(cloudUsers[0], data);//Average arrival: 32.5
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(100, 10, 100, 15, 1));
//		data2.add(new Summary(300, 10, 200, 7, 5));
//		data2.add(new Summary(500, 20, 300, 3, 6));
//		data2.add(new Summary(400, 20, 400, 3, 9));
//		data2.add(new Summary(900, 40, 500, 7, 11));
//		data2.add(new Summary(800, 40, 600, 15, 23));
//		data2.add(new Summary(700, 75, 700, 7, 35));
//		data2.add(new Summary(600, 80, 800, 3, 99));
//		data2.add(new Summary(500, 50, 900, 15, 88));
//		data2.add(new Summary(300, 50, 1000, 3, 77));
//		data2.add(new Summary(400, 75, 1000, 7, 99));
//		data2.add(new Summary(250, 60, 1000, 15, 1));
//		summaries.put(cloudUsers[1], data2);//Average service time: 479.16667
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
//		
//		assertEquals(511.66667, function.aggregateArrivals(), 0.0001);
//	}
//	
//	@Test
//	public void testEvaluateWithNegativeFitness(){
//		//SaaS clients contracts
//		Contract contract = new Contract("p1", 1, 100d, 555d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
//		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(4);
//		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(4);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
//		
//		//IaaS providers
//		List<TypeProvider> types = new ArrayList<TypeProvider>();
//		int largeReservationFee = 1000;
//		int mediumReservationFee = 990;
//		int smallReservationFee = 300;
//		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, largeReservationFee, 170, 5));
//		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, mediumReservationFee, 188, 5));
//		types.add(new TypeProvider(0, MachineType.SMALL, 0.01, 0.0005, smallReservationFee, 188, 5));
//		
//		Provider[] providers = new Provider[1];
//		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
//		
//		PowerMock.replayAll(config);
//		
//		//Summaries
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		data.add(new Summary(10, 20, 500, 5, 100));
//		summaries.put(cloudUsers[0], data);//10 req/s, 240 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		data2.add(new Summary(20, 30, 500, 5, 100));
//		summaries.put(cloudUsers[1], data2);//20 req/s, 360 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.LARGE, MachineType.MEDIUM));
//		
//		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
//		Gene[] genes = new Gene[2];
//		genes[0] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[0].getAllele()).andReturn(15);
//		genes[1] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[1].getAllele()).andReturn(5);
//		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
//		
//		EasyMock.replay(chromosome, genes[0], genes[1]);
//		
//		double receipt = 555 + 100 + 0 + 99.765 + 100 + 6;//for each contract: price + setup + extra cpu
//		double cost = largeReservationFee * 15 + 8640 * 0.01 + mediumReservationFee * 5 + 8640 * 0.25 + 864;//for each machine type: reservation fee + usage + on-demand cost
//		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
//		
//		assertEquals(1/Math.abs(receipt - cost - penalties) + 1, function.evaluate(chromosome), 0.0001);
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testEvaluateWithPositiveFitness(){
//		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
//		
//		//SaaS clients contracts
//		Contract contract = new Contract("p1", 1, 10000d, 555d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		Contract contract2 = new Contract("p1", 1, 10000d, 99.765d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
//		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(4);
//		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(4);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
//		
//		//IaaS providers
//		List<TypeProvider> types = new ArrayList<TypeProvider>();
//		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
//		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
//		types.add(new TypeProvider(0, MachineType.SMALL, 0.01, 0.0005, 99, 188, 5));
//		
//		Provider[] providers = new Provider[1];
//		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
//		
//		PowerMock.replayAll(config);
//		
//		//Summaries
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		summaries.put(cloudUsers[0], data);//10 req/s, 200 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		summaries.put(cloudUsers[1], data2);//20 req/s, 300 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.LARGE, MachineType.MEDIUM));
//		
//		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
//		Gene[] genes = new Gene[2];
//		genes[0] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[0].getAllele()).andReturn(15);
//		genes[1] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[1].getAllele()).andReturn(5);
//		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
//		
//		EasyMock.replay(chromosome, genes[0], genes[1]);
//		
//		double receipt = 12 * 555 + 10000 + 0 + 12 * 99.765 + 10000 + 0;//for each contract: price + setup + extra cpu
//		double cost = 100 * 15 + 8640 * 0.01 + 99 * 5 + 8640 * 0.25 + 864;//for each machine type: reservation fee + usage
//		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
//		
//		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.0001);
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testEvaluateWithoutReservingMachines(){
//		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
//		
//		//SaaS clients contracts
//		Contract contract = new Contract("p1", 1, 10000d, 555d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		Contract contract2 = new Contract("p1", 1, 10000d, 99.765d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
//		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(4);
//		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(4);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
//		
//		//IaaS providers
//		List<TypeProvider> types = new ArrayList<TypeProvider>();
//		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
//		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
//		types.add(new TypeProvider(0, MachineType.SMALL, 0.01, 0.0005, 99, 188, 5));
//		
//		Provider[] providers = new Provider[1];
//		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
//		
//		PowerMock.replayAll(config);
//		
//		//Summaries
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		summaries.put(cloudUsers[0], data);//10 req/s, 200 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		summaries.put(cloudUsers[1], data2);//20 req/s, 300 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.LARGE, MachineType.MEDIUM));
//		
//		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
//		Gene[] genes = new Gene[2];
//		genes[0] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[0].getAllele()).andReturn(0);
//		genes[1] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[1].getAllele()).andReturn(0);
//		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
//		
//		EasyMock.replay(chromosome, genes[0], genes[1]);
//		
//		double receipt = 12 * 555 + 10000 + 0 + 12 * 99.765 + 10000 + 0;//for each contract: price + setup + extra cpu
//		double cost = (129600.0) * 0.01;//for each machine type: reservation fee + usage
//		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
//		
//		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.0001);
//		
//		PowerMock.verifyAll();
//	}
//	
//	@Test
//	public void testEvaluateWithPositiveFitnessAndAmazonValues(){
//		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
//		double setupCost = 18 * 50d;
//		
//		//SaaS clients contracts
//		double price = 18 * 150d;
//		Contract contract = new Contract("p1", 1, setupCost, price, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		double price2 = 18 * 300d;
//		Contract contract2 = new Contract("p2", 1, setupCost, price2, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
//		
//		User[] cloudUsers = new User[2];
//		cloudUsers[0] = new User(0, contract, 100);
//		cloudUsers[1] = new User(1, contract2, 100);
//		
//		Configuration config = EasyMock.createMock(Configuration.class);
//		PowerMock.mockStatic(Configuration.class);
//		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
//		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(4);
//		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(4);
//		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
//		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
//		
//		//IaaS providers
//		List<TypeProvider> types = new ArrayList<TypeProvider>();
//		int largeReservation = 910;
//		types.add(new TypeProvider(0, MachineType.LARGE, 0.34, 0.12, largeReservation, 170, 5));
//		int mediumReservation = 455;
//		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.17, 0.06, mediumReservation, 188, 5));
//		double smallReservation = 227.5;
//		types.add(new TypeProvider(0, MachineType.SMALL, 0.085, 0.03, smallReservation, 188, 5));
//		
//		Provider[] providers = new Provider[1];
//		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
//		
//		PowerMock.replayAll(config);
//		
//		//Summaries
//		//Workload summaries
//		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
//		List<Summary> data = new ArrayList<Summary>();
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		data.add(new Summary(10, 200, 500, 5, 100));
//		summaries.put(cloudUsers[0], data);//10 req/s, 200 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		List<Summary> data2 = new ArrayList<Summary>();
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 299, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		data2.add(new Summary(20, 300, 500, 5, 100));
//		summaries.put(cloudUsers[1], data2);//20 req/s, 300 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
//		
//		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.LARGE, MachineType.MEDIUM));
//		
//		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
//		Gene[] genes = new Gene[2];
//		genes[0] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[0].getAllele()).andReturn(15);
//		genes[1] = EasyMock.createStrictMock(Gene.class);
//		EasyMock.expect(genes[1].getAllele()).andReturn(5);
//		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
//		
//		EasyMock.replay(chromosome, genes[0], genes[1]);
//		
//		double receipt = 12 * price + setupCost + 0 + 12 * price2 + setupCost;//for each contract: price + setup + extra cpu
//		double cost = largeReservation * 15 + 8640 * 0.12 + mediumReservation * 5 + 8640 * 0.06 + 86400 * 0.085;//for each machine type: reservation fee + usage
//		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
//		
//		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.0001);
//		
//		PowerMock.verifyAll();
//	}
}
