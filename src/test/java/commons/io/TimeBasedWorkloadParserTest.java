package commons.io;

import static org.junit.Assert.*;

import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.util.SaaSUsersProperties;
import commons.util.SimulationInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class})
public class TimeBasedWorkloadParserTest {
	
	@Test
	public void testConstructorWithoutPeakDays(){
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testConstructorWithPeakDaysAndSimulatedDayBefore(){
		SimulationInfo simulationInfo = new SimulationInfo(0, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testConstructorWithPeakDaysAndSimulatedDayEqualsToTransition(){
		SimulationInfo simulationInfo = new SimulationInfo(2, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		long scheduledTime = 0l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.capture(captured));
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		PowerMock.verifyAll();
		
		assertEquals(JEEventType.TRANSITION, captured.getValue().getType());
		assertEquals(scheduledTime, captured.getValue().getScheduledTime());
	}
	
	@Test
	public void testConstructorWithPeakDaysAndSimulatedDayEqualsToPeak(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.capture(captured));
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		PowerMock.verifyAll();
		
		assertEquals(JEEventType.PEAK, captured.getValue().getType());
		assertEquals(scheduledTime, captured.getValue().getScheduledTime());
	}
	
	@Test
	public void testConstructorWithPeakDaysAndSimulatedDayEqualsToSecondTransition(){
		SimulationInfo simulationInfo = new SimulationInfo(4, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.capture(captured));
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		PowerMock.verifyAll();
		
		assertEquals(JEEventType.PEAK_END, captured.getValue().getType());
		assertEquals(scheduledTime, captured.getValue().getScheduledTime());
	}
	
	@Test(expected=RuntimeException.class)
	public void testClear() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		parser.clear();
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHandleTransitionEvent(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.changeToTransition()).andReturn(0);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.changeToTransition()).andReturn(0);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		//Handling a transition event
		JEEvent event = new JEEvent(JEEventType.TRANSITION, parser, 0);
		parser.handleEvent(event);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHandlePeakEvent(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.changeToPeak()).andReturn(0);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.changeToPeak()).andReturn(0);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		//Handling a peak event
		JEEvent event = new JEEvent(JEEventType.PEAK, parser, 0);
		parser.handleEvent(event);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHandlePeakEndEvent(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.changeToNormal()).andReturn(0);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.changeToNormal()).andReturn(0);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		//Handling a peak end event
		JEEvent event = new JEEvent(JEEventType.PEAK_END, parser, 0);
		parser.handleEvent(event);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testChanges(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		//Requesting changes
		try{
			parser.changeToNormal();
			fail("Not yet implemented");
		}catch(RuntimeException e){
		}
		
		try{
			parser.changeToPeak();
			fail("Not yet implemented");
		}catch(RuntimeException e){
		}
		
		try{
			parser.changeToTransition();
			fail("Not yet implemented");
		}catch(RuntimeException e){
		}
		
		PowerMock.verifyAll();
	}
	
	@Test(expected=RuntimeException.class)
	public void testSetDaysAlreadyRead(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		PowerMock.replayAll(config, scheduler);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		//Requesting changes
		parser.setDaysAlreadyRead(120);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHasNextWithOneParserHavingEvents(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		assertEquals(true, parser.hasNext());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHasNextWithAnyParserHavingEvents(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		
		assertEquals(false, parser.hasNext());
		
		PowerMock.verifyAll();
	}
	
	@Test(expected=NullPointerException.class)
	public void testNextWithoutRequest(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(null);
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, 5000, parsers);
		parser.next();
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testNextWithRequestsAndLeftOverForNextTick(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		int tick = 5000;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		//Requests
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getArrivalTimeInMillis()).andReturn(tick - 10l);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getArrivalTimeInMillis()).andReturn(tick + 1l).times(2);
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(thirdRequest.getArrivalTimeInMillis()).andReturn(tick + 1l).times(2);
		
		//Parsers
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, tick, parsers);
		List<Request> requests = parser.next();
		assertEquals(1, requests.size());
		assertEquals(firstRequest, requests.get(0));
		
		//Retrieving requests from leftOver
		requests = parser.next();
		assertEquals(2, requests.size());
		assertEquals(secondRequest, requests.get(0));
		assertEquals(thirdRequest, requests.get(1));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testNextWithRequestsAndLeftOverForFutureTicks(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		int tick = 5000;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		//Requests
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getArrivalTimeInMillis()).andReturn(tick - 10l);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getArrivalTimeInMillis()).andReturn(2l * tick).times(3);
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(thirdRequest.getArrivalTimeInMillis()).andReturn(2l * tick).times(3);
		
		//Parsers
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, tick, parsers);
		List<Request> requests = parser.next();
		assertEquals(1, requests.size());
		assertEquals(firstRequest, requests.get(0));
		
		//Any requests in leftOver
		requests = parser.next();
		assertEquals(0, requests.size());
		
		//Retrieving requests from leftOver
		requests = parser.next();
		assertEquals(2, requests.size());
		assertEquals(secondRequest, requests.get(0));
		assertEquals(thirdRequest, requests.get(1));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testNextWithRequestsAndWithoutLeftOver(){
		SimulationInfo simulationInfo = new SimulationInfo(3, 0);
		int tick = 5000;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLongArray(SaaSUsersProperties.SAAS_PEAK_PERIOD)).andReturn(new long []{4});
		EasyMock.expect(config.getSimulationInfo()).andReturn(simulationInfo);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeBasedWorkloadParser.class))).andReturn(1);
		long scheduledTime = 1000l;
		EasyMock.expect(scheduler.now()).andReturn(scheduledTime);
		scheduler.queueEvent(EasyMock.anyObject(JEEvent.class));
		
		//Requests
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getArrivalTimeInMillis()).andReturn(tick - 10l);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getArrivalTimeInMillis()).andReturn(tick - 1l);
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(thirdRequest.getArrivalTimeInMillis()).andReturn(tick - 1l);
		Request fourthRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(fourthRequest.getArrivalTimeInMillis()).andReturn(tick - 1l);
		
		//Parsers
		GEISTMultiFileWorkloadParser parser1 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(firstRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(true);
		EasyMock.expect(parser1.next()).andReturn(secondRequest);
		EasyMock.expect(parser1.hasNext()).andReturn(false);
		
		GEISTMultiFileWorkloadParser parser2 = EasyMock.createStrictMock(GEISTMultiFileWorkloadParser.class);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(thirdRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(true);
		EasyMock.expect(parser2.next()).andReturn(fourthRequest);
		EasyMock.expect(parser2.hasNext()).andReturn(false);
		
		PowerMock.replayAll(config, scheduler, parser1, parser2, firstRequest, secondRequest, thirdRequest);
		
		GEISTMultiFileWorkloadParser[] parsers = new GEISTMultiFileWorkloadParser[]{parser1, parser2};
		
		TimeBasedWorkloadParser parser = new TimeBasedWorkloadParser(scheduler, tick, parsers);
		List<Request> requests = parser.next();
		assertEquals(4, requests.size());
		assertEquals(firstRequest, requests.get(0));
		assertEquals(secondRequest, requests.get(1));
		assertEquals(thirdRequest, requests.get(2));
		assertEquals(fourthRequest, requests.get(3));
		
		PowerMock.verifyAll();
	}
}
