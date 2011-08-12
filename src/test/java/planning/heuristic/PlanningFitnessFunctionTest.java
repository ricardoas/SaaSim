package planning.heuristic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.junit.Before;
import org.junit.Test;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.sim.util.SimulatorProperties;

import config.GEISTMonthlyWorkloadParser;


public class PlanningFitnessFunctionTest {
	
	private static final double SLA = 10d;
	private String SIMPLE_WORKLOAD = "src/test/resources/workload/w2.dat";
	private static final String CONFIG_FILE = "src/test/resources/config.properties";
	private static final int HOUR_IN_MILLIS = 1000 * 60 * 60;
	
	@Before
	public void setUp(){
//		JEEventScheduler.SCHEDULER.clear();
	}
	
	@Test
	public void emptyWorkload(){
		User user = new User("us1");
		double setupCost = 100;
		double price = 30;
		double cpuLimit = 1000 * 60 * 20;
		double extraCpuCost = 0.5;
		
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		Map<User, Contract> cloudUsers = new HashMap<User, Contract>();
		cloudUsers.put(user, contract);
		
		double cpuCost = 0.2;
		int onDemandLimit = 10;
		int reservationLimit = 10;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 50;
		double reservationThreeYearsFee = 75;
		double monitoringCost = 0.15;
		String transferInLimits = "";
		String transferInCosts = "";
		String transferOutLimits = "";
		String transferOutCosts = "";
		Provider prov = new Provider("prov1", cpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		Map<String, Provider> providers = new HashMap<String, Provider>();
		providers.put(prov.name, prov);
		
		try {
			Configuration.buildInstance(CONFIG_FILE);
			Configuration.getInstance().setProperty(SimulatorProperties.WORKLOAD_PATH, SIMPLE_WORKLOAD);
			
			GEISTMonthlyWorkloadParser parser = EasyMock.createStrictMock(GEISTMonthlyWorkloadParser.class);
			EasyMock.expect(parser.hasNext()).andReturn(true);
			EasyMock.expect(parser.next()).andReturn(new ArrayList<Request>());
			EasyMock.expect(parser.getWorkloadPerUser()).andReturn(new HashMap<User, List<Request>>());
			PlanningFitnessFunction fc = new PlanningFitnessFunction(parser, cloudUsers, providers);
			
			IChromosome chrom = EasyMock.createMock(IChromosome.class);
			Gene gene = EasyMock.createMock(Gene.class);
			EasyMock.expect(chrom.getGene(0)).andReturn(gene);
			EasyMock.expect(gene.getAllele()).andReturn(5);
			EasyMock.replay(gene, chrom, parser);
			
			double fitness = fc.evaluate(chrom);
			
			EasyMock.verify(gene, chrom, parser);
			
			assertEquals(1.0, fitness, 0.0);
		} catch (IOException e) {
			fail("Valid scenario!");
		} catch (ConfigurationException e) {
			fail("Valid scenario!");
		}
	}
	
	//TODO: Add more tests!
//	@Test
//	public void simpleWorkloadWithTwoUsersAndLowReceipts() throws IOException{
//		
//		GEISTMonthlyWorkloadParser parser = new GEISTMonthlyWorkloadParser(this.simpleWorkload);
//		
//		User user = new User("1");
//		User user2 = new User("2");
//		
//		double setupCost = 100;
//		double price = 30;
//		double cpuLimit = 20 * HOUR_IN_MILLIS;
//		double extraCpuCost = 0.5;
//		
//		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
//		Map<User, Contract> cloudUsers = new HashMap<User, Contract>();
//		cloudUsers.put(user, contract);
//		cloudUsers.put(user2, contract);
//		
//		double cpuCost = 0.2;
//		int onDemandLimit = 10;
//		int reservationLimit = 10;
//		double reservedCpuCost = 0.01;
//		double reservationOneYearFee = 50;
//		double reservationThreeYearsFee = 75;
//		double monitoringCost = 0.15;
//		String transferInLimits = "";
//		String transferInCosts = "";
//		String transferOutLimits = "";
//		String transferOutCosts = "";
//		Provider prov = new Provider("prov1", cpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
//		Map<String, Provider> providers = new HashMap<String, Provider>();
//		providers.put(prov.name, prov);
//		
//		PlanningFitnessFunction fc = new PlanningFitnessFunction(parser.next(), cloudUsers, SLA, providers);
//		
//		IChromosome chrom = EasyMock.createMock(IChromosome.class);
//		Gene gene = EasyMock.createMock(Gene.class);
//		EasyMock.expect(chrom.getGene(0)).andReturn(gene);
//		EasyMock.expect(gene.getAllele()).andReturn(10);
//		EasyMock.replay(gene);
//		EasyMock.replay(chrom);
//		
//		double fitness = fc.evaluate(chrom);
//		
//		EasyMock.verify(gene);
//		EasyMock.verify(chrom);
//		
//		double receipt = 131.5 + 131;//100+30+(3)*0.5; 100+30+(2)*0.5
//		double cost = 503.35;//10*50+10*(1*0.01)+15*(1*0.15)+5*(1*0.2)
//		
//		assertEquals(1, fitness, 0.0);
//	}
//	
//	/**
//	 * In this test requests arrives before the evaluation period (using utilization) that determines
//	 * that new machine should be purchased. So, all requests are added to a single resource and executed
//	 * on it.
//	 * @throws IOException
//	 */
//	@Test
//	public void simpleWorkloadWithTwoUsers() throws IOException{
//		
//		GEISTMonthlyWorkloadParser parser = new GEISTMonthlyWorkloadParser(this.simpleWorkload);
//		
//		User user = new User("1");
//		User user2 = new User("2");
//		
//		double setupCost = 1000;
//		double price = 30;
//		double cpuLimit = 20 * HOUR_IN_MILLIS;
//		double extraCpuCost = 0.5;
//		
//		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
//		Map<User, Contract> cloudUsers = new HashMap<User, Contract>();
//		cloudUsers.put(user, contract);
//		cloudUsers.put(user2, contract);
//		
//		double cpuCost = 0.2;
//		int onDemandLimit = 10;
//		int reservationLimit = 10;
//		double reservedCpuCost = 0.01;
//		double reservationOneYearFee = 50;
//		double reservationThreeYearsFee = 75;
//		double monitoringCost = 0.15;
//		String transferInLimits = "";
//		String transferInCosts = "";
//		String transferOutLimits = "";
//		String transferOutCosts = "";
//		Provider prov = new Provider("prov1", cpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
//		Map<String, Provider> providers = new HashMap<String, Provider>();
//		providers.put(prov.name, prov);
//		
//		PlanningFitnessFunction fc = new PlanningFitnessFunction(parser.next(), cloudUsers, SLA, providers);
//		
//		IChromosome chrom = EasyMock.createMock(IChromosome.class);
//		Gene gene = EasyMock.createMock(Gene.class);
//		EasyMock.expect(chrom.getGene(0)).andReturn(gene);
//		EasyMock.expect(gene.getAllele()).andReturn(10);
//		EasyMock.replay(gene);
//		EasyMock.replay(chrom);
//		
//		double fitness = fc.evaluate(chrom);
//		
//		EasyMock.verify(gene);
//		EasyMock.verify(chrom);
//		//FIXME
//		double receipt = 1030 + 1030;//1000+30; 1000+30
//		double cost = 500.64;//10*50+(4*0.01)+(4*0.15)
//		
//		assertEquals(receipt-cost, fitness, 0.01);
//	}
//	
//	@Test
//	public void simpleWorkloadBeingEvaluatedRepeatdly() throws IOException{
//		
//		GEISTMonthlyWorkloadParser parser = new GEISTMonthlyWorkloadParser(this.simpleWorkload);
//		
//		User user = new User("1");
//		User user2 = new User("2");
//		
//		double setupCost = 1000;
//		double price = 30;
//		double cpuLimit = 20 * ProviderTest.HOUR_IN_MILLIS;
//		double extraCpuCost = 0.5;
//		
//		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
//		Map<User, Contract> cloudUsers = new HashMap<User, Contract>();
//		cloudUsers.put(user, contract);
//		cloudUsers.put(user2, contract);
//		
//		double cpuCost = 0.2;
//		int onDemandLimit = 10;
//		int reservationLimit = 10;
//		double reservedCpuCost = 0.01;
//		double reservationOneYearFee = 50;
//		double reservationThreeYearsFee = 75;
//		double monitoringCost = 0.15;
//		String transferInLimits = "";
//		String transferInCosts = "";
//		String transferOutLimits = "";
//		String transferOutCosts = "";
//		Provider prov = new Provider("prov1", cpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
//		Map<String, Provider> providers = new HashMap<String, Provider>();
//		providers.put(prov.name, prov);
//		
//		Map<User, List<Request>> requests = parser.next();
//		PlanningFitnessFunction fc = new PlanningFitnessFunction(requests, cloudUsers, SLA, providers);
//		
//		IChromosome chrom = EasyMock.createMock(IChromosome.class);
//		Gene gene = EasyMock.createMock(Gene.class);
//		EasyMock.expect(chrom.getGene(0)).andReturn(gene).anyTimes();
//		EasyMock.expect(gene.getAllele()).andReturn(10).anyTimes();
//		EasyMock.replay(gene);
//		EasyMock.replay(chrom);
//		
//		double fitness = fc.evaluate(chrom);
//		fitness = fc.evaluate(chrom);
//		
//		EasyMock.verify(gene);
//		EasyMock.verify(chrom);
//		
//		//Evaluating that accountability is correct
//		List<Request> userRequests = requests.get(user);
//		for(Request request : userRequests){
//			assertEquals(request.totalProcessed, request.getDemand());
//		}
//		
//		userRequests = requests.get(user2);
//		for(Request request : userRequests){
//			assertEquals(request.totalProcessed, request.getDemand());
//		}
//	}
	
}
