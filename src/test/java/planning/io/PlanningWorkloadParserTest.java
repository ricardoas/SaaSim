package planning.io;

import static commons.config.PropertiesTesting.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import planning.util.Summary;


public class PlanningWorkloadParserTest {
	
	@Test(expected=ConfigurationException.class)
	public void testConstructorWithInexistentFile() throws ConfigurationException{
		new PlanningWorkloadParser("aaa.dat");
	}
	
	@Test(expected=ConfigurationException.class)
	public void testConstructorWithConfigFileWithoutValue() throws ConfigurationException{
		new PlanningWorkloadParser(INVALID_PLANNING_PROPERTIES);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testConstructorWithConfigFileWithoutProperty() throws ConfigurationException{
		new PlanningWorkloadParser(INVALID_PLANNING_PROPERTIES_2);
	}
	
	@Test
	public void testConstructorWithConfigFileWithWrongType() throws ConfigurationException{
		new PlanningWorkloadParser(INVALID_PLANNING_PROPERTIES_3);
	}
	
	@Test
	public void testConstructorWithValidFile() throws ConfigurationException{
		PlanningWorkloadParser parser = new PlanningWorkloadParser(VALID_PLANNING_PROPERTIES);
		assertEquals(0, parser.getSummaries().size());
	}
	
	@Test
	public void testReadData() throws ConfigurationException{
		PlanningWorkloadParser parser = new PlanningWorkloadParser(VALID_PLANNING_PROPERTIES);
		parser.readData();
		
		List<Summary> summaries = parser.getSummaries();
		assertEquals(3, summaries.size());
		
		Summary firstSummary = summaries.get(0);
		assertEquals(10.0, firstSummary.getArrivalRate(), 0.00001);
		assertEquals(2000.0, firstSummary.getTotalCpuHrs(), 0.00001);
		assertEquals(500.0, firstSummary.getRequestServiceDemandInMillis(), 0.00001);
		assertEquals(5.0, firstSummary.getUserThinkTimeInSeconds(), 0.00001);
		assertEquals(10, firstSummary.getNumberOfUsers());
		
		Summary secondSummary = summaries.get(1);
		assertEquals(20.0, secondSummary.getArrivalRate(), 0.00001);
		assertEquals(1500.0, secondSummary.getTotalCpuHrs(), 0.00001);
		assertEquals(500.0, secondSummary.getRequestServiceDemandInMillis(), 0.00001);
		assertEquals(5.0, secondSummary.getUserThinkTimeInSeconds(), 0.00001);
		assertEquals(20, secondSummary.getNumberOfUsers());
		
		Summary thirdSummary = summaries.get(2);
		assertEquals(30.0, thirdSummary.getArrivalRate(), 0.00001);
		assertEquals(4000.0, thirdSummary.getTotalCpuHrs(), 0.00001);
		assertEquals(600.0, thirdSummary.getRequestServiceDemandInMillis(), 0.00001);
		assertEquals(5.0, thirdSummary.getUserThinkTimeInSeconds(), 0.00001);
		assertEquals(30, thirdSummary.getNumberOfUsers());
	}
	
	@Test(expected=NumberFormatException.class)
	public void testReadDataWithInvalidType() throws ConfigurationException{
		PlanningWorkloadParser parser = new PlanningWorkloadParser(INVALID_PLANNING_PROPERTIES_3);
		parser.readData();
	}
}
