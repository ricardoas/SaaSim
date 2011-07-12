package planning.heuristic;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.junit.Before;
import org.junit.Test;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.sim.jeevent.JEEventScheduler;
import config.GEISTMonthlyWorkloadParser;


public class PlanningFitnessFunctionTest {
	
	private static final double SLA = 10d;
	private String simpleWorkload = "test_files/workload/w2.dat";
	
	@Before
	public void setUp(){
		JEEventScheduler.SCHEDULER.clear();
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
		
		Map<User, List<Request>> currentWorkload = new HashMap<User, List<Request>>();
		
		PlanningFitnessFunction fc = new PlanningFitnessFunction(currentWorkload, cloudUsers, 10d, providers);
		
		IChromosome chrom = EasyMock.createMock(IChromosome.class);
		Gene gene = EasyMock.createMock(Gene.class);
		EasyMock.expect(chrom.getGene(0)).andReturn(gene);
		EasyMock.expect(gene.getAllele()).andReturn(5);
		EasyMock.replay(gene);
		EasyMock.replay(chrom);
		
		double fitness = fc.evaluate(chrom);
		
		EasyMock.verify(gene);
		EasyMock.verify(chrom);
		
		assertEquals(1.0, fitness, 0.0);
	}
	
	@Test
	public void simpleWorkloadWithTwoUsers() throws IOException{
		
		GEISTMonthlyWorkloadParser parser = new GEISTMonthlyWorkloadParser(this.simpleWorkload);
		
		User user = new User("1");
		User user2 = new User("2");
		
		double setupCost = 100;
		double price = 30;
		double cpuLimit = 1000 * 60 * 20;
		double extraCpuCost = 0.5;
		
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		Map<User, Contract> cloudUsers = new HashMap<User, Contract>();
		cloudUsers.put(user, contract);
		cloudUsers.put(user2, contract);
		
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
		
		PlanningFitnessFunction fc = new PlanningFitnessFunction(parser.next(), cloudUsers, SLA, providers);
		
		IChromosome chrom = EasyMock.createMock(IChromosome.class);
		Gene gene = EasyMock.createMock(Gene.class);
		EasyMock.expect(chrom.getGene(0)).andReturn(gene);
		EasyMock.expect(gene.getAllele()).andReturn(10);
		EasyMock.replay(gene);
		EasyMock.replay(chrom);
		
		double fitness = fc.evaluate(chrom);
		
		EasyMock.verify(gene);
		EasyMock.verify(chrom);
		
		double receipt = 3750130 + 2400130;//100+30+(7200000+300000)*0.5; 100+30+(4500000+300000)*0.5
		double cost = 3015500;//10*50+10*(900000*0.01)+15*(900000*0.15)+5*(900000*0.2)
		
		assertEquals(receipt-cost, fitness, 0.0);
	}
	
}
