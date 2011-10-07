package provisioning.util;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.GEISTMultiFileWorkloadParser;
import commons.io.ParserIdiom;
import commons.io.TickSize;
import commons.io.TimeBasedWorkloadParser;
import commons.io.WorkloadParser;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SaaSUsersProperties;
import commons.util.SimulationInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class})
public class WorkloadParserFactoryTest {

	@Test
	public void testGetWorkloadParserWithoutPeaks() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SimulationInfo simInfo = new SimulationInfo(2, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getWorkloads()).andReturn(new String []{"src/test/resources/workload/", "src/test/resources/workload/transition/",
				"src/test/resources/workload/peak/"});
		EasyMock.expect(config.getParserIdiom()).andReturn(ParserIdiom.GEIST);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		EasyMock.expect(config.getInt(SaaSUsersProperties.SAAS_NUMBER_OF_USERS)).andReturn(10);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		PowerMock.replayAll(config, scheduler);
		
		WorkloadParserFactory.setScheduler(scheduler);
		WorkloadParser<List<Request>> parser = WorkloadParserFactory.getWorkloadParser();
		
		assertNotNull(parser);
		assertTrue(parser.hasNext());
		
		Field field = TimeBasedWorkloadParser.class.getDeclaredField("parsers");
		field.setAccessible(true);
		WorkloadParser<Request>[] parsers = (WorkloadParser<Request>[])field.get(parser);
		assertEquals(10, parsers.length);
		assertEquals(GEISTMultiFileWorkloadParser.class, parsers[0].getClass());
		
		field = TimeBasedWorkloadParser.class.getDeclaredField("tick");
		field.setAccessible(true);
		long tick = (Long) field.get(parser); 
		assertEquals(TickSize.MINUTE.getTickInMillis(), tick);	
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testGetWorkloadParserWithPeak() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SimulationInfo simInfo = new SimulationInfo(2, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getWorkloads()).andReturn(new String []{"src/test/resources/workload/", "src/test/resources/workload/transition/",
				"src/test/resources/workload/peak/"});
		EasyMock.expect(config.getParserIdiom()).andReturn(ParserIdiom.GEIST);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		EasyMock.expect(config.getInt(SaaSUsersProperties.SAAS_NUMBER_OF_USERS)).andReturn(10);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{3});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		PowerMock.replayAll(config, scheduler);
		
		WorkloadParserFactory.setScheduler(scheduler);
		WorkloadParser<List<Request>> parser = WorkloadParserFactory.getWorkloadParser();
		
		assertNotNull(parser);
		assertTrue(parser.hasNext());
		
		Field field = TimeBasedWorkloadParser.class.getDeclaredField("parsers");
		field.setAccessible(true);
		WorkloadParser<Request>[] parsers = (WorkloadParser<Request>[])field.get(parser);
		assertEquals(10, parsers.length);
		assertEquals(GEISTMultiFileWorkloadParser.class, parsers[0].getClass());
		
		field = TimeBasedWorkloadParser.class.getDeclaredField("tick");
		field.setAccessible(true);
		long tick = (Long) field.get(parser); 
		assertEquals(TickSize.MINUTE.getTickInMillis(), tick);	
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testGetWorkloadParserWithPeakAndNormalSimulationDay() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SimulationInfo simInfo = new SimulationInfo(1, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getWorkloads()).andReturn(new String []{"src/test/resources/workload/", "src/test/resources/workload/transition/",
				"src/test/resources/workload/peak/"});
		EasyMock.expect(config.getParserIdiom()).andReturn(ParserIdiom.GEIST);
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		EasyMock.expect(config.getInt(SaaSUsersProperties.SAAS_NUMBER_OF_USERS)).andReturn(10);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{5});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		PowerMock.replayAll(config, scheduler);
		
		WorkloadParserFactory.setScheduler(scheduler);
		WorkloadParser<List<Request>> parser = WorkloadParserFactory.getWorkloadParser();
		
		assertNotNull(parser);
		assertTrue(parser.hasNext());
		
		Field field = TimeBasedWorkloadParser.class.getDeclaredField("parsers");
		field.setAccessible(true);
		WorkloadParser<Request>[] parsers = (WorkloadParser<Request>[])field.get(parser);
		assertEquals(10, parsers.length);
		assertEquals(GEISTMultiFileWorkloadParser.class, parsers[0].getClass());
		
		field = TimeBasedWorkloadParser.class.getDeclaredField("tick");
		field.setAccessible(true);
		long tick = (Long) field.get(parser); 
		assertEquals(TickSize.MINUTE.getTickInMillis(), tick);	
		
		PowerMock.verifyAll();
	}
	
}
