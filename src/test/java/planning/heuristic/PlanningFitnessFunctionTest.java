package planning.heuristic;

import static commons.sim.util.SimulatorProperties.*;
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
	
	private final long DEFAULT_SLA = 8000;
	
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTime = DEFAULT_SLA;
		double arrivalRate = 10;
		double totalThroughput = 10;
		
		assertEquals(0, function.calcPenalties(responseTime, arrivalRate, totalThroughput), 0.00001);
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTime = DEFAULT_SLA * 1.1;
		double arrivalRate = 10;
		double totalThroughput = 10;
		
		double expectedPenalty = contract.calculatePenalty(0.1) + contract2.calculatePenalty(0.1);
		assertEquals(expectedPenalty, function.calcPenalties(responseTime, arrivalRate, totalThroughput), 0.00001);
		
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTime = DEFAULT_SLA;
		double arrivalRate = 10;
		double totalThroughput = 5;
		
		double expectedPenalty = contract.calculatePenalty(0.5) + contract2.calculatePenalty(0.5);
		assertEquals(expectedPenalty, function.calcPenalties(responseTime, arrivalRate, totalThroughput), 0.00001);
		
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTime = DEFAULT_SLA * 1.2;
		double arrivalRate = 10;
		double totalThroughput = 6;
		
		double expectedPenalty = contract.calculatePenalty(0.6) + contract2.calculatePenalty(0.6);
		assertEquals(expectedPenalty, function.calcPenalties(responseTime, arrivalRate, totalThroughput), 0.00001);
		
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
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)).andReturn(DEFAULT_SLA);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, cloudUsers, null, null);
		
		double responseTime = DEFAULT_SLA * 2;
		double arrivalRate = 10;
		double totalThroughput = 0;
		
		double expectedPenalty = contract.calculatePenalty(2) + contract2.calculatePenalty(2);
		assertEquals(expectedPenalty, function.calcPenalties(responseTime, arrivalRate, totalThroughput), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWith2MachineTypes(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 6d);
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 * 8 + 432 + 6480;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithOnlyOneThroughput(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
		PowerMock.replayAll(config);
		
		PlanningFitnessFunction function = new PlanningFitnessFunction(null, null, providers, null);
		
		Map<MachineType, Double> throughputPerMachineType = new HashMap<MachineType, Double>();
		throughputPerMachineType.put(MachineType.LARGE, 10d);
		throughputPerMachineType.put(MachineType.MEDIUM, 0d);//Machine reserved but not used
		double meanServiceTimeInMillis = 500;
		
		Map<MachineType, Integer> currentPowerPerMachineType = new HashMap<MachineType, Integer>();
		currentPowerPerMachineType.put(MachineType.LARGE, 30);//10 machines with 3 cores
		currentPowerPerMachineType.put(MachineType.MEDIUM, 16);//8 machines with 2 cores
		
		double expectedCost = 100 * 10 + 99 *8 + 432;//One year fee for large + one year fee for medium + large usage + medium usage
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		
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
		
		assertEquals(0, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType), 0.00001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testCalculateCostWithoutDemand2(){
		List<TypeProvider> types = new ArrayList<TypeProvider>();
		types.add(new TypeProvider(0, MachineType.LARGE, 0.1, 0.01, 100, 170, 5));
		types.add(new TypeProvider(0, MachineType.MEDIUM, 0.6, 0.25, 99, 188, 5));
		
		Provider[] providers = new Provider[1];
		providers[0] = new Provider(1, "p1", 10, 10, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, types);
		
		Configuration config = EasyMock.createMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(12l);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(3d);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(2d);
		
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
		assertEquals(expectedCost, function.calcCost(throughputPerMachineType, meanServiceTimeInMillis, currentPowerPerMachineType), 0.00001);
		
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
		
		assertEquals(24 * 100, function.aggregateNumberOfUsers(), 0.0001);
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
		
		assertEquals(7554, function.aggregateNumberOfUsers(), 0.0001);
	}

}
