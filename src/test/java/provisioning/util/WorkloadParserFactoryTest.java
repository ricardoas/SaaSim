package provisioning.util;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.io.GEISTMultiFileWorkloadParser;
import commons.io.GEISTSingleFileWorkloadParser;
import commons.io.ParserIdiom;
import commons.io.TickSize;
import commons.io.TimeBasedWorkloadParser;
import commons.io.WorkloadParser;
import commons.sim.util.SaaSUsersProperties;
import commons.util.SimulationInfo;

public class WorkloadParserFactoryTest extends ValidConfigurationTest{
	
	private static final long PAGE_SIZE = 86400000;

	@Test
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
