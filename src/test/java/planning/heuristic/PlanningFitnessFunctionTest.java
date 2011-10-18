package planning.heuristic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.jgap.Gene;
import org.jgap.IChromosome;
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
import commons.sim.util.SaaSAppProperties;
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
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.6, 0.25, 99, 188, 5));
		
		User[] cloudUsers = new User[0];
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> requestsFinishedPerMachineType = new HashMap<MachineType, Double>();
		requestsFinishedPerMachineType.put(MachineType.M1_LARGE, 10d * 31104000);
		requestsFinishedPerMachineType.put(MachineType.C1_MEDIUM, 6d * 31104000);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30 );//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16 );//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 432 + 6480;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(requestsFinishedPerMachineType, meanServiceTimeInMillis, 
				currentPowerPerMachineType, 0, 0), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWith2MachineTypesAndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.2, 0.1, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.M1_LARGE, 5d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.C1_MEDIUM, 3d * 31104000);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16);//8 machines with 2 cores
		
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
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.2, 0.1, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.M1_LARGE, 5d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.C1_MEDIUM, 3d * 31104000);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 216 + 3240 + 17280;//One year fee for large + one year fee for medium + large usage + medium usage + on-demand cost
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 622080900), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithOnlyOneThroughput(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.2, 0.1, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.M1_LARGE, 3.3333d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.C1_MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8 + 143.9986;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 0), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithOnlyOneThroughputAndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.M1_LARGE, 3.3333d * 31104000);
		finishedRequestsPerMachineType.put(MachineType.C1_MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8 + 143.9986 + 2160;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 5 * 31104000), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 50, 70, 5));
		
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
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16);//8 machines with 2 cores
		
		assertEquals(0, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 0), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand2(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.M1_LARGE, 10d);
		finishedRequestsPerMachineType.put(MachineType.C1_MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 0;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(finishedRequestsPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 
				0, 0), 0.001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand2AndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.1, 0.05, 50, 70, 5));
		
		User[] cloudUsers = new User[0];
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0d);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, providers, null);
		
		Map<MachineType, Double> finishedRequestsPerMachineType = new HashMap<MachineType, Double>();
		finishedRequestsPerMachineType.put(MachineType.M1_LARGE, 10d);
		finishedRequestsPerMachineType.put(MachineType.C1_MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 0;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.M1_LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.C1_MEDIUM, 16);//8 machines with 2 cores
		
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
		for(int i = 0; i < 31 * 24; i++){
			data.add(new Summary(1, 20, 500, 5, 100));
		}
		summaries.put(cloudUsers[0], data);//320 extra cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost + price + 14580 * 0.1;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
		
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
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
		
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
		data.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of the month have gone
		for(int i = 0; i < 732; i++){//missing hours of month
			data.add(new Summary(7, 0, 500, 5, 100));
		}
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
		data2.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of the month have gone
		for(int i = 0; i < 732; i++){//missing hours of month
			data2.add(new Summary(7, 0, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + price + price2;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
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
		data.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of first month
		for(int i = 0; i < 732; i++){//missing hours of first month
			data.add(new Summary(7, 0, 500, 5, 100));
		}
		
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
		data.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of second month
		for(int i = 0; i < 732; i++){//missing hours of second month
			data.add(new Summary(7, 0, 500, 5, 100));
		}
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
		data2.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of first month
		for(int i = 0; i < 732; i++){//missing hours of first month
			data2.add(new Summary(7, 0, 500, 5, 100));
		}
		
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
		data2.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of second month
		for(int i = 0; i < 732; i++){//missing hours of second month
			data2.add(new Summary(7, 0, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + 2 * price + 2 * price2;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
		
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
		data.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of first month
		for(int i = 0; i < 12; i++){//next 12 hours of first month
			data.add(new Summary(7, 0, 500, 5, 100));
		}
		data.add(new Summary(7, 10, 500, 5, 100));
		data.add(new Summary(7, 10, 500, 5, 100));
		data.add(new Summary(7, 10, 500, 5, 100));//Three more hours of first month
		for(int i = 0; i < 717; i++){//Missing hours of first month
			data.add(new Summary(7, 0, 500, 5, 100));
		}
		
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
		data.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of second month
		for(int i = 0; i < 84; i++){//End of first day and next three days of second month
			data.add(new Summary(7, 0, 500, 5, 100));
		}
		data.add(new Summary(7, 10, 500, 5, 100));
		for(int i = 0; i < 647; i++){//Missing hours of second month
			data.add(new Summary(7, 0, 500, 5, 100));
		}
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
		data2.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of first month
		for(int i = 0; i < 12; i++){//next 12 hours of first month
			data2.add(new Summary(7, 0, 500, 5, 100));
		}
		data2.add(new Summary(7, 50, 500, 5, 100));
		for(int i = 0; i < 719; i++){//Missing hours of first month
			data2.add(new Summary(7, 0, 500, 5, 100));
		}
		
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
		data2.add(new Summary(7, 60, 500, 5, 100));//First 12 hours of second month
		for(int i = 0; i < 12; i++){//next 12 hours of second month
			data2.add(new Summary(7, 0, 500, 5, 100));
		}

		data2.add(new Summary(7, 5, 500, 5, 100));
		data2.add(new Summary(7, 0, 500, 5, 100));
		data2.add(new Summary(7, 3, 500, 5, 100));
		for(int i = 0; i < 717; i++){//Missing hours of second month
			data2.add(new Summary(7, 0, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//50 extra cpu-hrs at first month, 8 cpu-hours at second 
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + 2 * price + 2 * price2 + 40 * 0.1 + 58 * 0.2;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testAggregateThinkTimeInTimeOrder(){
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
	
	@Test
	public void testAggregateNumberOfUsersInTimeOrder(){
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
		data.add(new Summary(10, 70, 500, 3, 300));
		data.add(new Summary(11, 75, 500, 3, 100));
		data.add(new Summary(12, 80, 500, 15, 100));
		data.add(new Summary(13, 90, 500, 7, 100));
		data.add(new Summary(14, 90, 500, 15, 100));
		data.add(new Summary(11, 75, 500, 7, 100));
		data.add(new Summary(7, 60, 500, 3, 181));
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
		data2.add(new Summary(7, 60, 500, 15, 99));
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(200, function.aggregateNumberOfUsers(0), 0.0001);
		assertEquals(400, function.aggregateNumberOfUsers(5), 0.0001);
		assertEquals(280, function.aggregateNumberOfUsers(11), 0.0001);
	}
	
	@Test
	public void testAggregateNumberOfUsersInAnyOrder(){
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
		data.add(new Summary(1, 10, 500, 15, 200));
		data.add(new Summary(2, 20, 500, 7, 300));
		data.add(new Summary(2, 20, 500, 7, 400));
		data.add(new Summary(5, 40, 500, 15, 500));
		data.add(new Summary(10, 70, 500, 3, 500));
		data.add(new Summary(11, 75, 500, 3, 600));
		data.add(new Summary(12, 80, 500, 15, 700));
		data.add(new Summary(13, 90, 500, 7, 800));
		data.add(new Summary(14, 90, 500, 15, 900));
		data.add(new Summary(11, 75, 500, 7, 1000));
		data.add(new Summary(7, 60, 500, 3, 1100));
		summaries.put(cloudUsers[0], data);//7100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 15, 1));
		data2.add(new Summary(1, 10, 500, 7, 5));
		data2.add(new Summary(2, 20, 500, 3, 6));
		data2.add(new Summary(2, 20, 500, 3, 9));
		data2.add(new Summary(5, 40, 500, 7, 11));
		data2.add(new Summary(10, 40, 500, 15, 23));
		data2.add(new Summary(11, 75, 500, 7, 35));
		data2.add(new Summary(12, 80, 500, 3, 99));
		data2.add(new Summary(13, 50, 500, 15, 88));
		data2.add(new Summary(14, 50, 500, 3, 77));
		data2.add(new Summary(11, 75, 500, 7, 99));
		data2.add(new Summary(7, 60, 500, 15, 1));
		summaries.put(cloudUsers[1], data2);//454 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(799, function.aggregateNumberOfUsers(7), 0.0001);
		assertEquals(306, function.aggregateNumberOfUsers(2), 0.0001);
		assertEquals(1099, function.aggregateNumberOfUsers(10), 0.0001);
		assertEquals(1101, function.aggregateNumberOfUsers(11), 0.0001);
		assertEquals(101, function.aggregateNumberOfUsers(0), 0.0001);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAggregateNumberOfUsersWithInvalidIndex(){
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
		data.add(new Summary(1, 10, 500, 15, 200));
		data.add(new Summary(2, 20, 500, 7, 300));
		data.add(new Summary(2, 20, 500, 7, 400));
		data.add(new Summary(5, 40, 500, 15, 500));
		data.add(new Summary(10, 70, 500, 3, 500));
		data.add(new Summary(11, 75, 500, 3, 600));
		data.add(new Summary(12, 80, 500, 15, 700));
		data.add(new Summary(13, 90, 500, 7, 800));
		data.add(new Summary(14, 90, 500, 15, 900));
		data.add(new Summary(11, 75, 500, 7, 1000));
		data.add(new Summary(7, 60, 500, 3, 1100));
		summaries.put(cloudUsers[0], data);//7100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 15, 1));
		data2.add(new Summary(1, 10, 500, 7, 5));
		data2.add(new Summary(2, 20, 500, 3, 6));
		data2.add(new Summary(2, 20, 500, 3, 9));
		data2.add(new Summary(5, 40, 500, 7, 11));
		data2.add(new Summary(10, 40, 500, 15, 23));
		data2.add(new Summary(11, 75, 500, 7, 35));
		data2.add(new Summary(12, 80, 500, 3, 99));
		data2.add(new Summary(13, 50, 500, 15, 88));
		data2.add(new Summary(14, 50, 500, 3, 77));
		data2.add(new Summary(11, 75, 500, 7, 99));
		data2.add(new Summary(7, 60, 500, 15, 1));
		summaries.put(cloudUsers[1], data2);//454 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		function.aggregateNumberOfUsers(101);
	}
	
	@Test
	public void testAggregateNumberOfUsersWithoutUsers(){
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
		data.add(new Summary(1, 10, 500, 0, 0));
		data.add(new Summary(1, 10, 500, 0, 0));
		data.add(new Summary(2, 20, 500, 0, 0));
		data.add(new Summary(2, 20, 500, 0, 0));
		data.add(new Summary(5, 40, 500, 0, 0));
		data.add(new Summary(10, 70, 500, 0, 0));
		data.add(new Summary(11, 75, 500, 0, 0));
		data.add(new Summary(12, 80, 500, 0, 0));
		data.add(new Summary(13, 90, 500, 0, 0));
		data.add(new Summary(14, 90, 500, 0, 0));
		data.add(new Summary(11, 75, 500, 0, 0));
		data.add(new Summary(7, 60, 500, 0, 0));
		summaries.put(cloudUsers[0], data);
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 500, 0, 0));
		data2.add(new Summary(1, 10, 500, 0, 0));
		data2.add(new Summary(2, 20, 500, 0, 0));
		data2.add(new Summary(2, 20, 500, 0, 0));
		data2.add(new Summary(5, 40, 500, 0, 0));
		data2.add(new Summary(10, 40, 500, 0, 0));
		data2.add(new Summary(11, 75, 500, 0, 0));
		data2.add(new Summary(12, 80, 500, 0, 0));
		data2.add(new Summary(13, 50, 500, 0, 0));
		data2.add(new Summary(14, 50, 500, 0, 0));
		data2.add(new Summary(11, 75, 500, 0, 0));
		data2.add(new Summary(7, 60, 500, 0, 0));
		summaries.put(cloudUsers[1], data2);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(0, function.aggregateNumberOfUsers(0), 0.0001);
		assertEquals(0, function.aggregateNumberOfUsers(3), 0.0001);
		assertEquals(0, function.aggregateNumberOfUsers(9), 0.0001);
		assertEquals(0, function.aggregateNumberOfUsers(10), 0.0001);
	}
	
	@Test
	public void testAggregateServiceDemandForOneSaaSClient(){
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
		data.add(new Summary(1, 10, 500, 0, 100));
		data.add(new Summary(1, 10, 600, 0, 200));
		data.add(new Summary(2, 20, 400, 0, 300));
		data.add(new Summary(2, 20, 700, 0, 400));
		data.add(new Summary(5, 40, 300, 0, 500));
		data.add(new Summary(10, 70, 800, 0, 500));
		data.add(new Summary(11, 75, 200, 0, 600));
		data.add(new Summary(12, 80, 500, 0, 700));
		data.add(new Summary(13, 90, 500, 0, 800));
		data.add(new Summary(14, 90, 900, 0, 900));
		data.add(new Summary(11, 75, 100, 0, 1000));
		data.add(new Summary(7, 60, 500, 0, 1100));
		summaries.put(cloudUsers[0], data);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(500, function.aggregateServiceDemand(0), 0.0001);
		assertEquals(300, function.aggregateServiceDemand(4), 0.0001);
		assertEquals(500, function.aggregateServiceDemand(8), 0.0001);
		assertEquals(900, function.aggregateServiceDemand(9), 0.0001);
		assertEquals(100, function.aggregateServiceDemand(10), 0.0001);
	}
	
	@Test
	public void testAggregateServiceTimeForMultipleSaaSClients(){
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
		data.add(new Summary(1, 10, 1000, 3, 100));
		data.add(new Summary(1, 10, 2000, 15, 200));
		data.add(new Summary(2, 20, 500, 7, 300));
		data.add(new Summary(2, 20, 2500, 7, 400));
		data.add(new Summary(5, 40, 3000, 15, 500));
		data.add(new Summary(10, 70, 1000, 3, 500));
		data.add(new Summary(11, 75, 2000, 3, 600));
		data.add(new Summary(12, 80, 2500, 15, 700));
		data.add(new Summary(13, 90, 2500, 7, 800));
		data.add(new Summary(14, 90, 3000, 15, 900));
		data.add(new Summary(11, 75, 1000, 7, 1000));
		data.add(new Summary(7, 60, 2000, 3, 1100));
		summaries.put(cloudUsers[0], data);//Average service time: 1916.667
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 100, 15, 1));
		data2.add(new Summary(1, 10, 200, 7, 5));
		data2.add(new Summary(2, 20, 300, 3, 6));
		data2.add(new Summary(2, 20, 400, 3, 9));
		data2.add(new Summary(5, 40, 500, 7, 11));
		data2.add(new Summary(10, 40, 600, 15, 23));
		data2.add(new Summary(11, 75, 700, 7, 35));
		data2.add(new Summary(12, 80, 800, 3, 99));
		data2.add(new Summary(13, 50, 900, 15, 88));
		data2.add(new Summary(14, 50, 1000, 3, 77));
		data2.add(new Summary(11, 75, 1000, 7, 99));
		data2.add(new Summary(7, 60, 1000, 15, 1));
		summaries.put(cloudUsers[1], data2);//Average service time: 625
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(550, function.aggregateServiceDemand(0), 0.0001);
		assertEquals(1750, function.aggregateServiceDemand(4), 0.0001);
		assertEquals(2000, function.aggregateServiceDemand(9), 0.0001);
		assertEquals(1500, function.aggregateServiceDemand(11), 0.0001);
	}
	
	@Test
	public void testAggregateServiceTimeForMultipleSaaSClientsInAnyOrder(){
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
		data.add(new Summary(1, 10, 1000, 3, 100));
		data.add(new Summary(1, 10, 2000, 15, 200));
		data.add(new Summary(2, 20, 500, 7, 300));
		data.add(new Summary(2, 20, 2500, 7, 400));
		data.add(new Summary(5, 40, 3000, 15, 500));
		data.add(new Summary(10, 70, 1000, 3, 500));
		data.add(new Summary(11, 75, 2000, 3, 600));
		data.add(new Summary(12, 80, 2500, 15, 700));
		data.add(new Summary(13, 90, 2500, 7, 800));
		data.add(new Summary(14, 90, 3000, 15, 900));
		data.add(new Summary(11, 75, 1000, 7, 1000));
		data.add(new Summary(7, 60, 2000, 3, 1100));
		summaries.put(cloudUsers[0], data);//Average service time: 1916.667
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 100, 15, 1));
		data2.add(new Summary(1, 10, 200, 7, 5));
		data2.add(new Summary(2, 20, 300, 3, 6));
		data2.add(new Summary(2, 20, 400, 3, 9));
		data2.add(new Summary(5, 40, 500, 7, 11));
		data2.add(new Summary(10, 40, 600, 15, 23));
		data2.add(new Summary(11, 75, 700, 7, 35));
		data2.add(new Summary(12, 80, 800, 3, 99));
		data2.add(new Summary(13, 50, 900, 15, 88));
		data2.add(new Summary(14, 50, 1000, 3, 77));
		data2.add(new Summary(11, 75, 1000, 7, 99));
		data2.add(new Summary(7, 60, 1000, 15, 1));
		summaries.put(cloudUsers[1], data2);//Average service time: 625
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(1000, function.aggregateServiceDemand(10), 0.0001);
		assertEquals(1100, function.aggregateServiceDemand(1), 0.0001);
		assertEquals(1450, function.aggregateServiceDemand(3), 0.0001);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAggregateServiceTimeForMultipleSaaSClientsAndInvalidIndex(){
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
		data.add(new Summary(1, 10, 1000, 3, 100));
		data.add(new Summary(1, 10, 2000, 15, 200));
		data.add(new Summary(2, 20, 500, 7, 300));
		data.add(new Summary(2, 20, 2500, 7, 400));
		data.add(new Summary(5, 40, 3000, 15, 500));
		data.add(new Summary(10, 70, 1000, 3, 500));
		data.add(new Summary(11, 75, 2000, 3, 600));
		data.add(new Summary(12, 80, 2500, 15, 700));
		data.add(new Summary(13, 90, 2500, 7, 800));
		data.add(new Summary(14, 90, 3000, 15, 900));
		data.add(new Summary(11, 75, 1000, 7, 1000));
		data.add(new Summary(7, 60, 2000, 3, 1100));
		summaries.put(cloudUsers[0], data);//Average service time: 1916.667
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(1, 10, 100, 15, 1));
		data2.add(new Summary(1, 10, 200, 7, 5));
		data2.add(new Summary(2, 20, 300, 3, 6));
		data2.add(new Summary(2, 20, 400, 3, 9));
		data2.add(new Summary(5, 40, 500, 7, 11));
		data2.add(new Summary(10, 40, 600, 15, 23));
		data2.add(new Summary(11, 75, 700, 7, 35));
		data2.add(new Summary(12, 80, 800, 3, 99));
		data2.add(new Summary(13, 50, 900, 15, 88));
		data2.add(new Summary(14, 50, 1000, 3, 77));
		data2.add(new Summary(11, 75, 1000, 7, 99));
		data2.add(new Summary(7, 60, 1000, 15, 1));
		summaries.put(cloudUsers[1], data2);//Average service time: 625
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		function.aggregateServiceDemand(99999999);
	}
	
	@Test
	public void testAggregateArrivalsForOneSaaSClient(){
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
		data.add(new Summary(1, 10, 500, 0, 100));
		data.add(new Summary(1, 10, 600, 0, 200));
		data.add(new Summary(2, 20, 400, 0, 300));
		data.add(new Summary(2, 20, 700, 0, 400));
		data.add(new Summary(5, 40, 300, 0, 500));
		data.add(new Summary(10, 70, 800, 0, 500));
		data.add(new Summary(11, 75, 200, 0, 600));
		data.add(new Summary(12, 80, 500, 0, 700));
		data.add(new Summary(13, 90, 500, 0, 800));
		data.add(new Summary(14, 90, 900, 0, 900));
		data.add(new Summary(11, 75, 100, 0, 1000));
		data.add(new Summary(7, 60, 500, 0, 1100));
		summaries.put(cloudUsers[0], data);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(1, function.aggregateArrivals(0), 0.0001);
		assertEquals(5, function.aggregateArrivals(4), 0.0001);
		assertEquals(12, function.aggregateArrivals(7), 0.0001);
		assertEquals(7, function.aggregateArrivals(11), 0.0001);
	}
	
	@Test
	public void testAggregateArrivalsForMultipleSaaSClients(){
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
		data.add(new Summary(10, 10, 1000, 3, 100));
		data.add(new Summary(20, 10, 2000, 15, 200));
		data.add(new Summary(25, 20, 500, 7, 300));
		data.add(new Summary(30, 20, 2500, 7, 400));
		data.add(new Summary(40, 40, 3000, 15, 500));
		data.add(new Summary(50, 70, 1000, 3, 500));
		data.add(new Summary(55, 75, 2000, 3, 600));
		data.add(new Summary(60, 80, 2500, 15, 700));
		data.add(new Summary(40, 90, 2500, 7, 800));
		data.add(new Summary(30, 90, 3000, 15, 900));
		data.add(new Summary(25, 75, 1000, 7, 1000));
		data.add(new Summary(5, 60, 2000, 3, 1100));
		summaries.put(cloudUsers[0], data);//Average arrival: 32.5
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(100, 10, 100, 15, 1));
		data2.add(new Summary(300, 10, 200, 7, 5));
		data2.add(new Summary(500, 20, 300, 3, 6));
		data2.add(new Summary(400, 20, 400, 3, 9));
		data2.add(new Summary(900, 40, 500, 7, 11));
		data2.add(new Summary(800, 40, 600, 15, 23));
		data2.add(new Summary(700, 75, 700, 7, 35));
		data2.add(new Summary(600, 80, 800, 3, 99));
		data2.add(new Summary(500, 50, 900, 15, 88));
		data2.add(new Summary(300, 50, 1000, 3, 77));
		data2.add(new Summary(400, 75, 1000, 7, 99));
		data2.add(new Summary(250, 60, 1000, 15, 1));
		summaries.put(cloudUsers[1], data2);//Average service time: 479.16667
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(320, function.aggregateArrivals(1), 0.0001);
		assertEquals(755, function.aggregateArrivals(6), 0.0001);
		assertEquals(425, function.aggregateArrivals(10), 0.0001);
	}
	
	@Test
	public void testAggregateArrivalsForMultipleSaaSClientsInAnyOrder(){
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
		data.add(new Summary(10, 10, 1000, 3, 100));
		data.add(new Summary(20, 10, 2000, 15, 200));
		data.add(new Summary(25, 20, 500, 7, 300));
		data.add(new Summary(30, 20, 2500, 7, 400));
		data.add(new Summary(40, 40, 3000, 15, 500));
		data.add(new Summary(50, 70, 1000, 3, 500));
		data.add(new Summary(55, 75, 2000, 3, 600));
		data.add(new Summary(60, 80, 2500, 15, 700));
		data.add(new Summary(40, 90, 2500, 7, 800));
		data.add(new Summary(30, 90, 3000, 15, 900));
		data.add(new Summary(25, 75, 1000, 7, 1000));
		data.add(new Summary(5, 60, 2000, 3, 1100));
		summaries.put(cloudUsers[0], data);//Average arrival: 32.5
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(100, 10, 100, 15, 1));
		data2.add(new Summary(300, 10, 200, 7, 5));
		data2.add(new Summary(500, 20, 300, 3, 6));
		data2.add(new Summary(400, 20, 400, 3, 9));
		data2.add(new Summary(900, 40, 500, 7, 11));
		data2.add(new Summary(800, 40, 600, 15, 23));
		data2.add(new Summary(700, 75, 700, 7, 35));
		data2.add(new Summary(600, 80, 800, 3, 99));
		data2.add(new Summary(500, 50, 900, 15, 88));
		data2.add(new Summary(300, 50, 1000, 3, 77));
		data2.add(new Summary(400, 75, 1000, 7, 99));
		data2.add(new Summary(250, 60, 1000, 15, 1));
		summaries.put(cloudUsers[1], data2);//Average service time: 479.16667
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(255, function.aggregateArrivals(11), 0.0001);
		assertEquals(430, function.aggregateArrivals(3), 0.0001);
		assertEquals(540, function.aggregateArrivals(8), 0.0001);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAggregateArrivalsForMultipleSaaSClientsAndInvalidIndex(){
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
		data.add(new Summary(10, 10, 1000, 3, 100));
		data.add(new Summary(20, 10, 2000, 15, 200));
		data.add(new Summary(25, 20, 500, 7, 300));
		data.add(new Summary(30, 20, 2500, 7, 400));
		data.add(new Summary(40, 40, 3000, 15, 500));
		data.add(new Summary(50, 70, 1000, 3, 500));
		data.add(new Summary(55, 75, 2000, 3, 600));
		data.add(new Summary(60, 80, 2500, 15, 700));
		data.add(new Summary(40, 90, 2500, 7, 800));
		data.add(new Summary(30, 90, 3000, 15, 900));
		data.add(new Summary(25, 75, 1000, 7, 1000));
		data.add(new Summary(5, 60, 2000, 3, 1100));
		summaries.put(cloudUsers[0], data);//Average arrival: 32.5
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(100, 10, 100, 15, 1));
		data2.add(new Summary(300, 10, 200, 7, 5));
		data2.add(new Summary(500, 20, 300, 3, 6));
		data2.add(new Summary(400, 20, 400, 3, 9));
		data2.add(new Summary(900, 40, 500, 7, 11));
		data2.add(new Summary(800, 40, 600, 15, 23));
		data2.add(new Summary(700, 75, 700, 7, 35));
		data2.add(new Summary(600, 80, 800, 3, 99));
		data2.add(new Summary(500, 50, 900, 15, 88));
		data2.add(new Summary(300, 50, 1000, 3, 77));
		data2.add(new Summary(400, 75, 1000, 7, 99));
		data2.add(new Summary(250, 60, 1000, 15, 1));
		summaries.put(cloudUsers[1], data2);//Average service time: 479.16667
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		function.aggregateArrivals(109);
	}
	
	@Test
	public void testEvaluateWithNegativeFitnessNoLossAndNoReceipt(){
		//SaaS clients contracts
		Contract contract = new Contract("p1", 1, 100d, 555d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(12);
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d).times(4);
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		int largeReservationFee = 1000;
		int mediumReservationFee = 990;
		int smallReservationFee = 300;
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, largeReservationFee, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, mediumReservationFee, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.01, 0.0005, smallReservationFee, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));//2 hours of first month
		summaries.put(cloudUsers[0], data);//10 req/s, 240 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));//2 hours of first month
		summaries.put(cloudUsers[1], data2);//20 req/s, 360 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.M1_LARGE, MachineType.C1_MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(15);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(5);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
		double receipt = 0 + 100 + 0 + 0 + 100 + 0;//for each contract: price + setup + extra cpu
		double cost = largeReservationFee * 15 + 6 * 0.01 + mediumReservationFee * 5 + 4 * 0.25 + 20 * 0.01;//for each machine type: reservation fee + usage + on-demand cost
		double penalties = 0 + 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(1/Math.abs(receipt - cost - penalties) + 1, function.evaluate(chromosome), 0.01);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testEvaluateWithNegativeFitnessNoLossAndReceipt(){
		//SaaS clients contracts
		Contract contract = new Contract("p1", 1, 100d, 555d, 700 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 700 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d).anyTimes();
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		int largeReservationFee = 1000;
		int mediumReservationFee = 990;
		int smallReservationFee = 300;
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, largeReservationFee, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, mediumReservationFee, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.01, 0.0005, smallReservationFee, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){//One month hours!
			data.add(new Summary(10, 0.8333333, 500, 5, 100));
		}
		summaries.put(cloudUsers[0], data);//10 req/s, 240 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){//One month hours!
			data2.add(new Summary(20, 1.25, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//20 req/s, 360 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.M1_LARGE, MachineType.C1_MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(15);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(5);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
		double receipt = 555 + 100 + 0 + 99.765 + 100 + 0;//for each contract: price + setup + extra cpu
		double cost = largeReservationFee * 15 + 2232 * 0.01 + mediumReservationFee * 5 + 1488 * 0.25 + 7440 * 0.01;//for each machine type: reservation fee + usage + on-demand cost
		double penalties = 0 + 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(1/Math.abs(receipt - cost - penalties) + 1, function.evaluate(chromosome), 0.000001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testEvaluateWithPositiveFitness(){
		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
		
		//SaaS clients contracts
		Contract contract = new Contract("p1", 1, 10000d, 555d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 10000d, 99.765d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d).anyTimes();
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.01, 0.0005, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){
			data.add(new Summary(10, 0.8333333, 500, 5, 100));
		}
		summaries.put(cloudUsers[0], data);//10 req/s, 200 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){
			data2.add(new Summary(20, 1.25, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//20 req/s, 300 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.M1_LARGE, MachineType.C1_MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(15);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(5);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
//		double receipt = 12 * 555 + 10000 + 0 + 12 * 99.765 + 10000 + 0;//for each contract: price + setup + extra cpu
//		double cost = 100 * 15 + 8640 * 0.01 + 99 * 5 + 8640 * 0.25 + 864;//for each machine type: reservation fee + usage
//		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
		double receipt = 555 + 10000 + 630 * 0.1 + 99.765 + 10000 + 320 * 0.1;//for each contract: price + setup + extra cpu
		double cost = 100 * 15 + 2232 * 0.01 + 99 * 5 + 1488 * 0.25 + 7440 * 0.01;//for each machine type: reservation fee + usage
		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testEvaluateWithPositiveFitnessAndThreeMonths(){
		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
		
		//SaaS clients contracts
		Contract contract = new Contract("p1", 1, 10000d, 555d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 10000d, 99.765d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d).anyTimes();
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.01, 0.0005, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		for(int i = 0; i < 2160; i++){
			data.add(new Summary(10, 0.8333333, 500, 5, 100));
		}
		summaries.put(cloudUsers[0], data);//10 req/s, 200 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		for(int i = 0; i < 2160; i++){
			data2.add(new Summary(20, 1.25, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//20 req/s, 300 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.M1_LARGE, MachineType.C1_MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(15);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(5);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
		double receipt = 3 * 555 + 10000 + (630 + 540 + 630) * 0.1 + 3 * 99.765 + 10000 + (320 + 260 + 320) * 0.1;//for each contract: price + setup + extra cpu
		double cost = 100 * 15 + (2232 + 2016 + 2232) * 0.01 + 99 * 5 + (1488 + 1344 + 1488) * 0.25 + (7440 + 6720 + 7440) * 0.01;//for each machine type: reservation fee + usage
		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testEvaluateWithoutReservingMachines(){
		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
		
		//SaaS clients contracts
		Contract contract = new Contract("p1", 1, 10000d, 555d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 10000d, 99.765d, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d).anyTimes();
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.01, 0.0005, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Summaries
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){
			data.add(new Summary(10, 0.8333333, 500, 5, 100));
		}
		summaries.put(cloudUsers[0], data);//10 req/s, 200 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){
			data2.add(new Summary(20, 1.25, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//20 req/s, 300 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.M1_LARGE, MachineType.C1_MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(0);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(0);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
		double receipt = 555 + 10000 + 630 * 0.1 + 99.765 + 10000 + 320 * 0.1;//for each contract: price + setup + extra cpu
		double cost = (2232 + 1488 + 7440) * 0.01;//for each machine type: reservation fee + usage
		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testEvaluateWithPositiveFitnessAndAmazonValues(){
		long cpuLimitInMillis = 300 * 60 * 60 * 1000l;
		double setupCost = 18 * 500d;
		
		//SaaS clients contracts
		double price = 18 * 150d;
		Contract contract = new Contract("p1", 1, setupCost, price, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		double price2 = 18 * 300d;
		Contract contract2 = new Contract("p2", 1, setupCost, price2, cpuLimitInMillis, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.M1_LARGE)).andReturn(3d).anyTimes();
		EasyMock.expect(config.getRelativePower(MachineType.C1_MEDIUM)).andReturn(2d).anyTimes();
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_INTERVAL_SIZE)).andReturn(60l * 60l);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(500l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getDouble(SimulatorProperties.PLANNING_RISK)).andReturn(0.0);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		int largeReservation = 910;
		types.add(new TypeProvider(0, MachineType.M1_LARGE, 0.34, 0.12, largeReservation, 170, 5));
		int mediumReservation = 455;
		types.add(new TypeProvider(0, MachineType.C1_MEDIUM, 0.17, 0.06, mediumReservation, 188, 5));
		double smallReservation = 227.5;
		types.add(new TypeProvider(0, MachineType.M1_SMALL, 0.085, 0.03, smallReservation, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){
			data.add(new Summary(10, 0.8333333, 500, 5, 100));
		}
		summaries.put(cloudUsers[0], data);//10 req/s, 200 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		for(int i = 0; i < 31 * 24; i++){
			data2.add(new Summary(20, 1.25, 500, 5, 100));
		}
		summaries.put(cloudUsers[1], data2);//20 req/s, 300 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.M1_LARGE, MachineType.C1_MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(15);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(5);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
		double receipt = price + setupCost + 630 * 0.1 + price2 + setupCost + 320 * 0.1;//for each contract: price + setup + extra cpu
		double cost = largeReservation * 15 + 2232 * 0.12 + mediumReservation * 5 + 1488 * 0.06 + 7440 * 0.085;//for each machine type: reservation fee + usage
		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.01);
		
		PowerMock.verifyAll();
	}
}
