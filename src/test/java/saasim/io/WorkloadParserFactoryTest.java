package saasim.io;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import saasim.cloud.Request;
import saasim.io.WorkloadParser;
import saasim.io.WorkloadParserFactory;
import saasim.util.ValidConfigurationTest;


public class WorkloadParserFactoryTest extends ValidConfigurationTest{
	
	private static final long PAGE_SIZE = 86400000;

	public void testGetWorkloadParserWithSingleGEISTFile() throws ConfigurationException{
		buildFullConfiguration();
		WorkloadParser<List<Request>> workloadParser = WorkloadParserFactory.getWorkloadParser(PAGE_SIZE);
		assertNotNull(workloadParser);
		assertEquals(1, workloadParser.size());
	}

	@Test
	public void testGetWorkloadParserWithMultipleGEISTFiles() throws ConfigurationException{
		buildMultiFileGEISTFullConfiguration();
		WorkloadParser<List<Request>> workloadParser = WorkloadParserFactory.getWorkloadParser(PAGE_SIZE);
		assertNotNull(workloadParser);
		assertEquals(2, workloadParser.size());
	}

	@Test(expected=ConfigurationException.class)
	public void testGetWorkloadParserWithUndefinedIdiomFiles() throws ConfigurationException{
		buildUndefinedWorkloadIdiomConfiguration();
		WorkloadParserFactory.getWorkloadParser(PAGE_SIZE);
	}

	@Test(expected=AssertionError.class)
	public void testGetWorkloadParserWithNegativePageSize(){
		WorkloadParserFactory.getWorkloadParser(-1);
	}

}
