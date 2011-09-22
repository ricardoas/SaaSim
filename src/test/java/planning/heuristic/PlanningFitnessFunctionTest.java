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

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.TypeProvider;
import commons.cloud.User;
import commons.config.Configuration;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class PlanningFitnessFunctionTest {
	
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeInSeconds = DEFAULT_SLA;
		double arrivalRate = 10;
		double totalThroughput = 10;
		
		assertEquals(0, function.calcPenalties(responseTimeInSeconds, arrivalRate, totalThroughput), 0.00001);
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeInSeconds = DEFAULT_SLA * 1.1;
		double arrivalRate = 10;
		double totalThroughput = 10;
		
		double expectedPenalty = contract.calculatePenalty(0.1) + contract2.calculatePenalty(0.1);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeInSeconds, arrivalRate, totalThroughput), 0.00001);
		
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeInSeconds = DEFAULT_SLA;
		double arrivalRate = 10;
		double totalThroughput = 5;
		
		double expectedPenalty = contract.calculatePenalty(0.5) + contract2.calculatePenalty(0.5);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeInSeconds, arrivalRate, totalThroughput), 0.00001);
		
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeInSeconds = DEFAULT_SLA * 1.2;
		double arrivalRate = 10;
		double totalThroughput = 6;
		
		double expectedPenalty = contract.calculatePenalty(0.6) + contract2.calculatePenalty(0.6);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeInSeconds, arrivalRate, totalThroughput), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void calculatePenaltyWithTotalLoss2(){
		Contract contract = new Contract("p1", 1, 100d, 555d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 1000l, 0.1, new long[]{}, new double[]{}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(1, contract, 100);
		cloudUsers[1] = new User(2, contract2, 100);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTimeInSeconds = DEFAULT_SLA * 2;
		double arrivalRate = 10;
		double totalThroughput = 0;
		
		double expectedPenalty = contract.calculatePenalty(2) + contract2.calculatePenalty(2);
		assertEquals(expectedPenalty, function.calcPenalties(responseTimeInSeconds, arrivalRate, totalThroughput), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWith2MachineTypesAndNoOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.6, 0.25, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(2);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 6d);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 144 + 3240;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 0), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWith2MachineTypesAndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.2, 0.1, 50, 70, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(2);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 6d);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 144 + 3240 + 4320;//One year fee for large + one year fee for medium + large usage + medium usage + on-demand cost
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 5), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithOnlyOneThroughput(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.2, 0.1, 50, 70, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(2);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8 + 143.9986;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 0), 0.01);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithOnlyOneThroughputAndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(2);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8 + 143.9986 + 2160;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 5), 0.01);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(1);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		assertEquals(0, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 0d), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand2(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(2);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 0;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 0d), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand2AndOnDemandResources(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.1, 0.05, 50, 70, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(2);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 0;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType, 10d), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateReceiptWithExtraConsumedCpu(){
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		
		Contract contract = new Contract("p1", 1, setupCost, price, 300 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, 500 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
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
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + price + price2 + 340 * 0.1 + 30 * 0.2;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
	}
	
	@Test
	public void testCalculateReceiptWithoutDemand(){
		double setupCost = 100d;
		double price = 555d;
		double price2 = 99.765d;
		
		Contract contract = new Contract("p1", 1, setupCost, price, 300 * 60 * 60 * 1000l, 0.1, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		Contract contract2 = new Contract("p2", 1, setupCost, price2, 500 * 60 * 60 * 1000l, 0.2, new long[]{100000}, new double[]{0.1, 0.2}, 1000, 5.12);
		
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
		
		double receipt = setupCost * 2 + price + price2;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
	}
	
	@Test
	public void testCalculateReceiptWithoutExtraCpu(){
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
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, null);
		
		double receipt = setupCost * 2 + price + price2;
		assertEquals(receipt, function.calcReceipt(), 0.0001);
	}
	
	@Test
	public void testAggregateThinkTime(){
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
		data2.add(new Summary(7, 60, 500, 5, 100));
		summaries.put(cloudUsers[1], data2);//530 cpu-hrs
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, null, null);
		
		assertEquals(6.25, function.aggregateThinkTime(), 0.0001);
	}
	
	@Test
	public void testAggregateThinkTime2(){
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
		
		assertEquals(8.333333, function.aggregateThinkTime(), 0.0001);
	}
	
	@Test
	public void testAggregateNumberOfUsers(){
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
		
		assertEquals(200, function.aggregateNumberOfUsers(), 0.0001);
	}
	
	@Test
	public void testAggregateNumberOfUsers2(){
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
		
		assertEquals(628, function.aggregateNumberOfUsers(), 0.0001);
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
		
		assertEquals(0, function.aggregateNumberOfUsers(), 0.0001);
	}
	
	@Test
	public void testAggregateServiceTimeForOneSaaSClient(){
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
		
		assertEquals(500, function.aggregateServiceTime(), 0.0001);
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
		
		assertEquals(1270.8333333, function.aggregateServiceTime(), 0.0001);
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
		
		assertEquals(7.416667, function.aggregateArrivals(), 0.0001);
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
		
		assertEquals(511.66667, function.aggregateArrivals(), 0.0001);
	}
	
	@Test
	public void testEvaluateWithNegativeFitness(){
		//SaaS clients contracts
		Contract contract = new Contract("p1", 1, 100d, 555d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 100d, 99.765d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(4);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.01, 0.0005, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Summaries
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		summaries.put(cloudUsers[0], data);//10 req/s, 240 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		summaries.put(cloudUsers[1], data2);//20 req/s, 360 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.LARGE, MachineType.MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(15);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(5);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
		double receipt = 555 + 100 + 0 + 99.765 + 100 + 6;//for each contract: price + setup + extra cpu
		double cost = 100 * 15 + 8640 * 0.01 + 99 * 5 + 8640 * 0.25 + 864;//for each machine type: reservation fee + usage + on-demand cost
		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(1/Math.abs(receipt - cost - penalties) + 1, function.evaluate(chromosome), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testEvaluateWithPositiveFitness(){
		//SaaS clients contracts
		Contract contract = new Contract("p1", 1, 10000d, 555d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		Contract contract2 = new Contract("p1", 1, 10000d, 99.765d, 300 * 60 * 60 * 1000l, 0.1, new long[]{1000}, new double[]{0, 0}, 1000, 5.12);
		
		User[] cloudUsers = new User[2];
		cloudUsers[0] = new User(0, contract, 100);
		cloudUsers[1] = new User(1, contract2, 100);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d).times(4);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d).times(4);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(360l);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA * 1000);
		
		//IaaS providers
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		types.add(new TypeProvider(0, MachineType.SMALL, 0.01, 0.0005, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		PowerMock.replayAll(config);
		
		//Summaries
		//Workload summaries
		Map<User, List<Summary>> summaries = new HashMap<User, List<Summary>>();
		List<Summary> data = new ArrayList<Summary>();
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		data.add(new Summary(10, 20, 500, 5, 100));
		summaries.put(cloudUsers[0], data);//10 req/s, 240 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		List<Summary> data2 = new ArrayList<Summary>();
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		data2.add(new Summary(20, 30, 500, 5, 100));
		summaries.put(cloudUsers[1], data2);//20 req/s, 360 cpu-hrs, Si = 500 ms, Z = 5 s, M = 100 users
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(summaries, cloudUsers, providers, Arrays.asList(MachineType.LARGE, MachineType.MEDIUM));
		
		IChromosome chromosome = EasyMock.createStrictMock(IChromosome.class);
		Gene[] genes = new Gene[2];
		genes[0] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[0].getAllele()).andReturn(15);
		genes[1] = EasyMock.createStrictMock(Gene.class);
		EasyMock.expect(genes[1].getAllele()).andReturn(5);
		EasyMock.expect(chromosome.getGenes()).andReturn(genes);
		
		EasyMock.replay(chromosome, genes[0], genes[1]);
		
		double receipt = 555 + 10000 + 0 + 99.765 + 10000 + 6;//for each contract: price + setup + extra cpu
		double cost = 100 * 15 + 8640 * 0.01 + 99 * 5 + 8640 * 0.25 + 864;//for each machine type: reservation fee + usage
		double penalties = 0;//Since loss is more than 5%, SaaS client does not pay the provider
		
		assertEquals(receipt - cost - penalties, function.evaluate(chromosome), 0.0001);
		
		PowerMock.verifyAll();
	}
}
