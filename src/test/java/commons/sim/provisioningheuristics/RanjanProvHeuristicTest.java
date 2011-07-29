package commons.sim.provisioningheuristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;


public class RanjanProvHeuristicTest {
	
	private RanjanProvHeuristic heuristic;

	@Before
	public void setUp(){
		this.heuristic = new RanjanProvHeuristic();
	}
	
	@Test
	public void testWithEmptyStatistics(){
		try{
			this.heuristic.evaluateNumberOfServersForNextInterval(null);
			fail("Null statistics!");
		}catch(NullPointerException e){
		}
	}
	
	/**
	 * This scenario verifies that an utilization that is not so much greater than the target
	 * utilization (0.66) indicates that a small number of servers should be added
	 */
	@Test
	public void testWithASingleServer(){
		double totalUtilization = 0.7;
		long totalRequestsArrivals = 10;
		long totalRequestsCompletions = 10;
		long totalNumberOfServers = 1;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(1, serversForNextInterval);
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added
	 */
	@Test
	public void testMultipleServers(){
		double totalUtilization = 3.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		long totalNumberOfServers = 3;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(14, serversForNextInterval);
	}
	
	/**
	 * This scenario verifies that an utilization that is lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * large number of servers should be removed
	 */
	@Test
	public void testMultipleServersWithLowUtilization(){
		double totalUtilization = 6;
		long totalRequestsArrivals = 333;
		long totalRequestsCompletions = 279;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(-9, serversForNextInterval);
	}
	
	/**
	 * This scenario verifies that an utilization that is so much lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * minimal amount of servers should remain in the infrastructure
	 */
	@Test
	public void testMultipleServersWithLowUtilization2(){
		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(-19, serversForNextInterval);
	}
	
	/**
	 * This scenario verifies a scenario where no demand has occurred
	 */
	@Test
	public void testMultipleServersWithLowUtilization3(){
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 0;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(-20, serversForNextInterval);
	}
	
	/**
	 * This scenario verifies a scenario where no machines were available and a high demand
	 * has arrived
	 */
	@Test
	public void testMultipleServersWithoutPreviousMachines(){
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 0;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(1, serversForNextInterval);
	}
}
