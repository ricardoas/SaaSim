package commons.sim;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Test;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.ProcessorSharedMachine;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.Triple;


public class AccountingSystemTest {
	
	private long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60; 
	
	@Test
	public void testCreateAccountingWithInvalidReservationData(){
		try{
			new AccountingSystem(-1, 2);
			fail("Invalid accounting");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void testCreateAccountingWithInvalidOnDemandData(){
		try{
			new AccountingSystem(1, -2);
			fail("Invalid accounting");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void testCreateReservedMachine(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		long machineID = 1l;
		boolean isReserved = true;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		
		Class cls;
		try {
			//Checking reserved machines created
			cls = Class.forName("commons.sim.AccountingSystem");
			Field fld = cls.getDeclaredField("reservedMachinesIDs");
			List<Long> machineIDs = (List<Long>) fld.get(acc);
			assertNotNull(machineIDs);
			assertEquals(1, machineIDs.size());
			assertTrue(machineIDs.contains(machineID));
			
			//Checking on-demand machines created
			fld = cls.getDeclaredField("onDemandMachinesIDs");
			machineIDs = (List<Long>) fld.get(acc);
			assertNotNull(machineIDs);
			assertEquals(0, machineIDs.size());
			
			//Checking that machine start times where registered
			fld = cls.getDeclaredField("machineUtilization");
			Map<Long, Triple<Long, Long, Double>> machineUtilization = (Map<Long, Triple<Long, Long, Double>>) fld.get(acc);
			assertNotNull(machineUtilization);
			assertEquals(1, machineUtilization.size());
			assertEquals(0, (long)machineUtilization.get(machineID).firstValue);
			assertNull(machineUtilization.get(machineID).secondValue);
			assertNull(machineUtilization.get(machineID).thirdValue);
			
		} catch (ClassNotFoundException e) {
			fail("Valid scenario!");
		} catch (SecurityException e) {
			fail("Valid scenario!");
		} catch (NoSuchFieldException e) {
			fail("Valid scenario!");
		} catch (IllegalArgumentException e) {
			fail("Valid scenario!");
		} catch (IllegalAccessException e) {
			fail("Valid scenario!");
		}  
	}
	
	@Test
	public void testCreateOnDemandMachine(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		long machineID = 1l;
		boolean isReserved = false;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		
		Class cls;
		try {
			//Checking reserved machines created
			cls = Class.forName("commons.sim.AccountingSystem");
			Field fld = cls.getDeclaredField("reservedMachinesIDs");
			List<Long> machineIDs = (List<Long>) fld.get(acc);
			assertNotNull(machineIDs);
			assertEquals(0, machineIDs.size());
			
			//Checking on-demand machines created
			fld = cls.getDeclaredField("onDemandMachinesIDs");
			machineIDs = (List<Long>) fld.get(acc);
			assertNotNull(machineIDs);
			assertEquals(1, machineIDs.size());
			assertTrue(machineIDs.contains(machineID));
			
			//Checking that machine start times where registered
			fld = cls.getDeclaredField("machineUtilization");
			Map<Long, Triple<Long, Long, Double>> machineUtilization = (Map<Long, Triple<Long, Long, Double>>) fld.get(acc);
			assertNotNull(machineUtilization);
			assertEquals(1, machineUtilization.size());
			assertEquals(0, (long)machineUtilization.get(machineID).firstValue);
			assertNull(machineUtilization.get(machineID).secondValue);
			assertNull(machineUtilization.get(machineID).thirdValue);
			
			//Creating a second machine
			acc.createMachine(new MachineDescriptor(machineID+1, false, 10000));
			
			//Checking reserved machines created
			fld = cls.getDeclaredField("reservedMachinesIDs");
			machineIDs = (List<Long>) fld.get(acc);
			assertNotNull(machineIDs);
			assertEquals(0, machineIDs.size());
			
			//Checking on-demand machines created
			fld = cls.getDeclaredField("onDemandMachinesIDs");
			machineIDs = (List<Long>) fld.get(acc);
			assertNotNull(machineIDs);
			assertEquals(2, machineIDs.size());
			assertTrue(machineIDs.contains(machineID+1));
			assertTrue(machineIDs.contains(machineID));
			
			//Checking that machine start times where registered
			fld = cls.getDeclaredField("machineUtilization");
			machineUtilization = (Map<Long, Triple<Long, Long, Double>>) fld.get(acc);
			assertNotNull(machineUtilization);
			assertEquals(2, machineUtilization.size());
			assertEquals(0, (long)machineUtilization.get(machineID).firstValue);
			assertNull(machineUtilization.get(machineID).secondValue);
			assertNull(machineUtilization.get(machineID).thirdValue);
			assertEquals(10000, (long)machineUtilization.get(machineID+1).firstValue);
			assertNull(machineUtilization.get(machineID+1).secondValue);
			assertNull(machineUtilization.get(machineID+1).thirdValue);
			
		} catch (ClassNotFoundException e) {
			fail("Valid scenario!");
		} catch (SecurityException e) {
			fail("Valid scenario!");
		} catch (NoSuchFieldException e) {
			fail("Valid scenario!");
		} catch (IllegalArgumentException e) {
			fail("Valid scenario!");
		} catch (IllegalAccessException e) {
			fail("Valid scenario!");
		}  
	}
	
	@Test
	public void testReportMachineFinished(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		long machineID = 1l;
		boolean isReserved = false;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		acc.createMachine(new MachineDescriptor(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2));
//		acc.createMachine(machineID, isReserved, 0);
//		acc.createMachine(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2);
		
		//Finishing machine
		acc.reportMachineFinish(machineID, ONE_HOUR_IN_MILLIS);
		
		try {
			
			//Verifying machine status
			Class cls = Class.forName("commons.sim.AccountingSystem");
			Field fld = cls.getDeclaredField("machineUtilization");
			Map<Long, Triple<Long, Long, Double>> machineUtilization = (Map<Long, Triple<Long, Long, Double>>) fld.get(acc);
			assertEquals(0, (long)machineUtilization.get(machineID).firstValue);
			assertEquals(ONE_HOUR_IN_MILLIS, (long)machineUtilization.get(machineID).secondValue);
			
			assertEquals(ONE_HOUR_IN_MILLIS * 2, (long)machineUtilization.get(machineID+1).firstValue);
			assertNull(machineUtilization.get(machineID+1).secondValue);
			
			//Finishing second machine
			acc.reportMachineFinish(machineID+1, ONE_HOUR_IN_MILLIS * 100);
			machineUtilization = (Map<Long, Triple<Long, Long, Double>>) fld.get(acc);
			assertEquals(0, (long)machineUtilization.get(machineID).firstValue);
			assertEquals(ONE_HOUR_IN_MILLIS, (long)machineUtilization.get(machineID).secondValue);
			
			assertEquals(ONE_HOUR_IN_MILLIS * 2, (long)machineUtilization.get(machineID+1).firstValue);
			assertEquals(ONE_HOUR_IN_MILLIS * 100, (long)machineUtilization.get(machineID+1).secondValue);
			
		} catch (ClassNotFoundException e) {
			fail("Valid scenario!");
		} catch (SecurityException e) {
			fail("Valid scenario!");
		} catch (NoSuchFieldException e) {
			fail("Valid scenario!");
		} catch (IllegalArgumentException e) {
			fail("Valid scenario!");
		} catch (IllegalAccessException e) {
			fail("Valid scenario!");
		} 	
	}
	
	@Test
	public void testReportMachineFinishOfInexistentMachine(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		long machineID = 1l;
		boolean isReserved = false;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
//		acc.createMachine(machineID, isReserved, 0);
		
		//Finishing inexistent machine
		try{
			acc.reportMachineFinish(100, ONE_HOUR_IN_MILLIS);
			fail("Inexistent machine");
		}catch(RuntimeException e){
		}
	}
	
	/**
	 * This method verifies that after adding a number of on-demand or reserved machines that 
	 * reaches the limits defined, the accounting system informs that no more machines could
	 * be added.
	 */
	@Test
	public void testCanAddMachines(){
		int resourcesReservationLimit = 3;
		int onDemandLimit = 2;
		long machineID = 1l;
		boolean isReserved = true;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		assertTrue(acc.canAddAOnDemandMachine());
		assertTrue(acc.canAddAReservedMachine());
		
		//Adding a number of machines below limits defined: 2 reserved machines, 1 on-demand
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		acc.createMachine(new MachineDescriptor(machineID+1, isReserved, ONE_HOUR_IN_MILLIS));
		acc.createMachine(new MachineDescriptor(machineID+2, !isReserved, ONE_HOUR_IN_MILLIS * 2));
//		acc.createMachine(machineID, isReserved, 0);
//		acc.createMachine(machineID+1, isReserved, ONE_HOUR_IN_MILLIS);
//		acc.createMachine(machineID+2, !isReserved, ONE_HOUR_IN_MILLIS * 2);
		
		assertTrue(acc.canAddAOnDemandMachine());
		assertTrue(acc.canAddAReservedMachine());
		
		//Adding machines that reaches limits: 1 reserved, 1 on-demand
		acc.createMachine(new MachineDescriptor(machineID+3, isReserved, (long)(ONE_HOUR_IN_MILLIS * 1.5)));
		acc.createMachine(new MachineDescriptor(machineID, !isReserved, (long)(ONE_HOUR_IN_MILLIS * 2.5)));
//		acc.createMachine(machineID+3, isReserved, ONE_HOUR_IN_MILLIS * 1.5);
//		acc.createMachine(machineID+4, !isReserved, ONE_HOUR_IN_MILLIS * 2.5);
		
		assertFalse(acc.canAddAOnDemandMachine());
		assertFalse(acc.canAddAReservedMachine());
	}
	
	@Test
	public void testCanAddMachinesAfterMachinesFinishing(){
		int resourcesReservationLimit = 4;
		int onDemandLimit = 6;
		long machineID = 1l;
		boolean isReserved = true;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		//Adding a number of machines that reaches limits defined: 2 reserved machines, 1 on-demand
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		acc.createMachine(new MachineDescriptor(machineID+1, isReserved, (long)(ONE_HOUR_IN_MILLIS * 0.5)));
		acc.createMachine(new MachineDescriptor(machineID+2, isReserved, (long)(ONE_HOUR_IN_MILLIS)));
		acc.createMachine(new MachineDescriptor(machineID+3, isReserved, (long)(ONE_HOUR_IN_MILLIS * 1.5)));
		acc.createMachine(new MachineDescriptor(machineID+4, !isReserved, (long)(ONE_HOUR_IN_MILLIS * 2)));
		acc.createMachine(new MachineDescriptor(machineID+5, !isReserved, (long)(ONE_HOUR_IN_MILLIS * 2.5)));
		acc.createMachine(new MachineDescriptor(machineID+6, !isReserved, (long)(ONE_HOUR_IN_MILLIS * 3)));
		acc.createMachine(new MachineDescriptor(machineID+7, !isReserved, (long)(ONE_HOUR_IN_MILLIS * 3)));
		acc.createMachine(new MachineDescriptor(machineID+8, !isReserved, (long)(ONE_HOUR_IN_MILLIS * 3.5)));
		acc.createMachine(new MachineDescriptor(machineID+9, !isReserved, (long)(ONE_HOUR_IN_MILLIS * 7)));
//		acc.createMachine(machineID, isReserved, 0);
//		acc.createMachine(machineID+1, isReserved, ONE_HOUR_IN_MILLIS * 0.5);
//		acc.createMachine(machineID+2, isReserved, ONE_HOUR_IN_MILLIS);
//		acc.createMachine(machineID+3, isReserved, ONE_HOUR_IN_MILLIS * 1.5);
//		acc.createMachine(machineID+4, !isReserved, ONE_HOUR_IN_MILLIS * 2);
//		acc.createMachine(machineID+5, !isReserved, ONE_HOUR_IN_MILLIS * 2.5);
//		acc.createMachine(machineID+6, !isReserved, ONE_HOUR_IN_MILLIS * 3);
//		acc.createMachine(machineID+7, !isReserved, ONE_HOUR_IN_MILLIS * 3);
//		acc.createMachine(machineID+8, !isReserved, ONE_HOUR_IN_MILLIS * 3.5);
//		acc.createMachine(machineID+9, !isReserved, ONE_HOUR_IN_MILLIS * 7);
		
		//Verifying that no more machines can be added
		assertFalse(acc.canAddAOnDemandMachine());
		assertFalse(acc.canAddAReservedMachine());
		
		//Finishing some reserved machines
		acc.reportMachineFinish(machineID, ONE_HOUR_IN_MILLIS * 9);
		acc.reportMachineFinish(machineID+3, (long)(ONE_HOUR_IN_MILLIS * 9.5));
		
		assertFalse(acc.canAddAOnDemandMachine());
		assertTrue(acc.canAddAReservedMachine());
		
		//Finishing some on-demand machines
		acc.reportMachineFinish(machineID+6, ONE_HOUR_IN_MILLIS * 15);
		acc.reportMachineFinish(machineID+9, ONE_HOUR_IN_MILLIS * 22);
		
		assertTrue(acc.canAddAOnDemandMachine());
		assertTrue(acc.canAddAReservedMachine());
	}
	
	@Test
	public void testGetMachineUtilizationForMachinesNotFinished(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		long machineID = 1l;
		boolean isReserved = false;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		acc.createMachine(new MachineDescriptor(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2));
//		acc.createMachine(machineID, isReserved, 0);
//		acc.createMachine(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2);
		
		//Verifying utilization for machines not finished
		try{
			acc.getMachineUtilization(machineID);
			fail("Machine not yet finished!");
		}catch(NullPointerException e){
		}
		
		try{
			acc.getMachineUtilization(machineID+1);
			fail("Machine not yet finished!");
		}catch(NullPointerException e){
		}
	}
	
	@Test
	public void testGetMachineUtilizationForMachinesAlreadyFinished(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		long machineID = 1l;
		boolean isReserved = false;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		acc.createMachine(new MachineDescriptor(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2));
//		acc.createMachine(machineID, isReserved, 0);
//		acc.createMachine(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2);
		
		acc.reportMachineFinish(machineID, ONE_HOUR_IN_MILLIS * 10);
		acc.reportMachineFinish(machineID+1, ONE_HOUR_IN_MILLIS * 7);
		
		//Verifying utilization for machines finished
		assertEquals(ONE_HOUR_IN_MILLIS * 10, acc.getMachineUtilization(machineID), 0.0);
		assertEquals(ONE_HOUR_IN_MILLIS * 5, acc.getMachineUtilization(machineID+1), 0.0);
	}
	
	@Test
	public void testGetMachineUtilizationForInexistentMachines(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		long machineID = 1l;
		boolean isReserved = false;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		acc.createMachine(new MachineDescriptor(machineID, isReserved, 0));
		acc.createMachine(new MachineDescriptor(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2));
//		acc.createMachine(machineID, isReserved, 0);
//		acc.createMachine(machineID+1, !isReserved, ONE_HOUR_IN_MILLIS * 2);
		
		//Verifying utilization for inexistent machines
		assertEquals(0, acc.getMachineUtilization(100), 0.0);
		assertEquals(0, acc.getMachineUtilization(987666), 0.0);
	}
	
	@Test
	public void testReportRequestFinished(){
		int resourcesReservationLimit = 4;
		int onDemandLimit = 2;
		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		Class cls;
		try {
			cls = Class.forName("commons.sim.AccountingSystem");
			Field fld = cls.getDeclaredField("requestsFinishedPerUser");
			Map<String, List<String>> requestsFinishedPerUser = (Map<String, List<String>>) fld.get(acc);
			assertNotNull(requestsFinishedPerUser);
			assertEquals(0, requestsFinishedPerUser.size());
			
			//Finishing some requests
			String userID = "us1";
			String reqID = "1999876";
			Long reqSize = 10000l;
			Request request = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
			EasyMock.expect(request.getRequestID()).andReturn(reqID);
			EasyMock.expect(request.getSizeInBytes()).andReturn(reqSize);
			EasyMock.replay(request);
			acc.reportRequestFinished(request);
			
			String user2ID = "us2";
			String req2ID = "11111";
			Long req2Size = 999l;
			Request request2 = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request2.getUserID()).andReturn(user2ID).times(2);
			EasyMock.expect(request2.getRequestID()).andReturn(req2ID);
			EasyMock.expect(request2.getSizeInBytes()).andReturn(req2Size);
			EasyMock.replay(request2);
			acc.reportRequestFinished(request2);
			
			String req3ID = "999999";
			Long req3Size = 55l;
			Request request3 = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request3.getUserID()).andReturn(userID);
			EasyMock.expect(request3.getRequestID()).andReturn(req3ID);
			EasyMock.expect(request3.getSizeInBytes()).andReturn(req3Size);
			EasyMock.replay(request3);
			acc.reportRequestFinished(request3);
			
			EasyMock.verify(request, request2, request3);
			
			//Verifying that requests for correctly computed
			requestsFinishedPerUser = (Map<String, List<String>>) fld.get(acc);
			assertNotNull(requestsFinishedPerUser);
			assertEquals(2, requestsFinishedPerUser.size());
			
			List<String> userRequests = requestsFinishedPerUser.get(userID);
			assertEquals(2, userRequests.size());
			assertTrue(userRequests.contains(reqID));
			assertTrue(userRequests.contains(req3ID));
			
			userRequests = requestsFinishedPerUser.get(user2ID);
			assertEquals(1, userRequests.size());
			assertTrue(userRequests.contains(req2ID));
			
			//Verifying total transferred
			fld = cls.getDeclaredField("totalTransferred");
			double totalTransferred = (Double) fld.get(acc);
			assertEquals(reqSize+req2Size+req3Size, totalTransferred, 0.0);
			
		} catch (ClassNotFoundException e) {
			fail("Valid scenario!");
		} catch (SecurityException e) {
			fail("Valid scenario!");
		} catch (NoSuchFieldException e) {
			fail("Valid scenario!");
		} catch (IllegalArgumentException e) {
			fail("Valid scenario!");
		} catch (IllegalAccessException e) {
			fail("Valid scenario!");
		}
	}
	
	@Test
	public void testGetRequestsFinishedForInexistentUsers(){
		int resourcesReservationLimit = 4;
		int onDemandLimit = 2;

		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		assertEquals(0, acc.getRequestsFinished("us1"));
		assertEquals(0, acc.getRequestsFinished("us2"));
		assertEquals(0, acc.getRequestsFinished(""));
	}
	
	@Test
	public void testGetRquestsFinished(){
		int resourcesReservationLimit = 4;
		int onDemandLimit = 2;
		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		//Finishing some requests
		String userID = "us1";
		String reqID = "1";
		Long reqSize = 876554l;
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.expect(request.getRequestID()).andReturn(reqID);
		EasyMock.expect(request.getSizeInBytes()).andReturn(reqSize);
		EasyMock.replay(request);
		acc.reportRequestFinished(request);
		
		String user2ID = "us2";
		String req2ID = "2";
		Long req2Size = 777l;
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getUserID()).andReturn(user2ID).times(2);
		EasyMock.expect(request2.getRequestID()).andReturn(req2ID);
		EasyMock.expect(request2.getSizeInBytes()).andReturn(req2Size);
		EasyMock.replay(request2);
		acc.reportRequestFinished(request2);
		
		String req3ID = "3";
		Long req3Size = 123l;
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getUserID()).andReturn(userID);
		EasyMock.expect(request3.getRequestID()).andReturn(req3ID);
		EasyMock.expect(request3.getSizeInBytes()).andReturn(req3Size);
		EasyMock.replay(request3);
		acc.reportRequestFinished(request3);
		
		String user3ID = "us3";
		String req4ID = "3";
		Long req4Size = 1000000l;
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getUserID()).andReturn(user3ID).times(2);
		EasyMock.expect(request4.getRequestID()).andReturn(req4ID);
		EasyMock.expect(request4.getSizeInBytes()).andReturn(req4Size);
		EasyMock.replay(request4);
		acc.reportRequestFinished(request4);
		
		EasyMock.verify(request, request2, request3, request4);
		
		//Verifying requests per user
		assertEquals(2, acc.getRequestsFinished(userID));
		assertEquals(1, acc.getRequestsFinished(user2ID));
		assertEquals(1, acc.getRequestsFinished(user3ID));
	}
	
	@Test
	public void testExtraReceiptCalcWithExtraCpuResourcesUsed(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 11.5d * ONE_HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 12d * ONE_HOUR_IN_MILLIS;//Partial hour is billed as a full hour
		
		int resourcesReservationLimit = 4;
		int onDemandLimit = 2;
		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		assertEquals(0.9, acc.calcExtraReceipt(contract, user), 0.0);
	}
	
	@Test
	public void testCalculateCostForOnDemandAndReservedResources(){
		User user = new User("us1");
		JEEventScheduler scheduler = new JEEventScheduler();
		Provider provider = this.buildProvider();
		
		int resourcesReservationLimit = 4;
		int onDemandLimit = 2;
		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		//Adding reserved resources
		Long machine1ID = 1l;
		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
		triple.firstValue = 0l;
		triple.secondValue = 10 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(machine1ID, triple);
		
		Long machine2ID = 2l;
		Triple<Long, Long, Double> triple2 = new Triple<Long, Long, Double>();
		triple2.firstValue = 10 * ONE_HOUR_IN_MILLIS;
		triple2.secondValue = 30 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(machine2ID, triple2);
		
		Long machine3ID = 3l;
		Triple<Long, Long, Double> triple3 = new Triple<Long, Long, Double>();
		triple3.firstValue = 1 * ONE_HOUR_IN_MILLIS;
		triple3.secondValue = 16 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(machine3ID, triple3);
		
		Long machine4ID = 4l;
		Triple<Long, Long, Double> triple4 = new Triple<Long, Long, Double>();
		triple4.firstValue = 50 * ONE_HOUR_IN_MILLIS;
		triple4.secondValue = 65 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(machine4ID, triple4);
		
		Long machine5ID = 5l;
		Triple<Long, Long, Double> triple5 = new Triple<Long, Long, Double>();
		triple5.firstValue = 0l;
		triple5.secondValue = 15 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(machine5ID, triple5);
		
		Long machine6ID = 6l;
		Triple<Long, Long, Double> triple6 = new Triple<Long, Long, Double>();
		triple6.firstValue = (long)(11.5 * ONE_HOUR_IN_MILLIS);
		triple6.secondValue = 24 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(machine6ID, triple6);
		
		Long machine7ID = 7l;
		Triple<Long, Long, Double> triple7 = new Triple<Long, Long, Double>();
		triple7.firstValue = 0l;
		triple7.secondValue = (long)(15.23 * ONE_HOUR_IN_MILLIS);
		provider.reservedResources.put(machine7ID, triple7);
		
		Long machine8ID = 8l;
		Triple<Long, Long, Double> triple8 = new Triple<Long, Long, Double>();
		triple8.firstValue = 18 * ONE_HOUR_IN_MILLIS;
		triple8.secondValue = 36 * ONE_HOUR_IN_MILLIS;
		provider.onDemandResources.put(machine8ID, triple8);
		
		Long machine9ID = 9l;
		Triple<Long, Long, Double> triple9 = new Triple<Long, Long, Double>();
		triple9.firstValue = 0l;
		triple9.secondValue = (long)(78.5 * ONE_HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine9ID, triple9);
		
		Long machine10ID = 10l;
		Triple<Long, Long, Double> triple10 = new Triple<Long, Long, Double>();
		triple10.firstValue = (long)(2.4 * ONE_HOUR_IN_MILLIS);
		triple10.secondValue = (long)(3.6d * ONE_HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine10ID, triple10);
		
		Long machine11ID = 11l;
		Triple<Long, Long, Double> triple11 = new Triple<Long, Long, Double>();
		triple11.firstValue = 0l;
		triple11.secondValue = (long)(5.14d * ONE_HOUR_IN_MILLIS);
		provider.onDemandResources.put(machine11ID, triple11);
		
		//Verifying total cost
		Class cls;
		try {
			cls = Class.forName("commons.sim.AccountingSystem");
			Field fld = cls.getDeclaredField("totalTransferred");
			fld.setDouble(acc, 0d);
			
			assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost +
					105 * provider.onDemandCpuCost + 105 * provider.monitoringCost, acc.calculateCost(provider), 0.0);
		} catch (ClassNotFoundException e) {
			fail("Valid scenario!");
		} catch (SecurityException e) {
			fail("Valid scenario!");
		} catch (NoSuchFieldException e) {
			fail("Valid scenario!");
		} catch (IllegalArgumentException e) {
			fail("Valid scenario!");
		} catch (IllegalAccessException e) {
			fail("Valid scenario!");
		}
	}
	
	@Test
	public void testCalculateTotalReceipt(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 1d * ONE_HOUR_IN_MILLIS;
		Double extraCpuCost = 0.085d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 199d * ONE_HOUR_IN_MILLIS;
		
		int resourcesReservationLimit = 4;
		int onDemandLimit = 2;
		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		assertEquals(16.83 + price + setupCost, acc.calculateTotalReceipt(contract, user), 0.0);
	}
	
	private Provider buildProvider(){
		double onDemandCpuCost = 0.12;
		int onDemandLimit = 30;
		int reservationLimit = 20;
		double reservedCpuCost = 0.085;
		double reservationOneYearFee = 227.50;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		String transferInLimits = "";
		String transferInCosts = "";
		String transferOutLimits = "";
		String transferOutCosts = "";
		
		return new Provider("prov", onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
	}
	
	@Test
	public void testCalculateUtility(){
		Double setupCost = 100d;
		Double price = 200d;
		Double cpuLimit = 1d * ONE_HOUR_IN_MILLIS;
		Double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
		
		User user = new User("us1");
		user.consumedCpu = 212d * ONE_HOUR_IN_MILLIS;
		
		Provider provider = this.buildProvider();
		JEEventScheduler scheduler = new JEEventScheduler();
		
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		
		//Adding reserved resources data
		Long mach1ID = 1l;
		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
		triple.firstValue = 0l;
		triple.secondValue = 10 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(mach1ID, triple);
		
		Long mach2ID = 2l;
		Triple<Long, Long, Double> triple2 = new Triple<Long, Long, Double>();
		triple2.firstValue = 0l;
		triple2.secondValue = 20 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(mach2ID, triple2);
		
		Long mach3ID = 3l;
		Triple<Long, Long, Double> triple3 = new Triple<Long, Long, Double>();
		triple3.firstValue = 0l;
		triple3.secondValue = 15 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(mach3ID, triple3);
		
		Long mach4ID = 4l;
		Triple<Long, Long, Double> triple4 = new Triple<Long, Long, Double>();
		triple4.firstValue = 0l;
		triple4.secondValue = 15 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(mach4ID, triple4);
		
		Long mach5ID = 5l;
		Triple<Long, Long, Double> triple5 = new Triple<Long, Long, Double>();
		triple5.firstValue = 0l;
		triple5.secondValue = 15 * ONE_HOUR_IN_MILLIS;
		provider.reservedResources.put(mach5ID, triple5);
		
		Long mach6ID = 6l;
		Triple<Long, Long, Double> triple6 = new Triple<Long, Long, Double>();
		triple6.firstValue = 0l;
		triple6.secondValue = (long)(15.5d * ONE_HOUR_IN_MILLIS);
		provider.reservedResources.put(mach6ID, triple6);
		
		Long mach7ID = 7l;
		Triple<Long, Long, Double> triple7 = new Triple<Long, Long, Double>();
		triple7.firstValue = 0l;
		triple7.secondValue = (long)(15.23d * ONE_HOUR_IN_MILLIS);
		provider.reservedResources.put(mach7ID, triple7);
		
		//On-demand resources data
		Long mach8ID = 8l;
		Triple<Long, Long, Double> triple8 = new Triple<Long, Long, Double>();
		triple8.firstValue = 0l;
		triple8.secondValue = 18 * ONE_HOUR_IN_MILLIS;
		provider.onDemandResources.put(mach8ID, triple8);
		
		Long mach9ID = 9l;
		Triple<Long, Long, Double> triple9 = new Triple<Long, Long, Double>();
		triple9.firstValue = 0l;
		triple9.secondValue = (long)(78.5d * ONE_HOUR_IN_MILLIS);
		provider.onDemandResources.put(mach9ID, triple9);
		
		Long mach10ID = 10l;
		Triple<Long, Long, Double> triple10 = new Triple<Long, Long, Double>();
		triple10.firstValue = 0l;
		triple10.secondValue = (long)(1.2d * ONE_HOUR_IN_MILLIS);
		provider.onDemandResources.put(mach10ID, triple10);
		
		Long mach11ID = 11l;
		Triple<Long, Long, Double> triple11 = new Triple<Long, Long, Double>();
		triple11.firstValue = 0l;
		triple11.secondValue = (long)(5.14d * ONE_HOUR_IN_MILLIS);
		provider.onDemandResources.put(mach11ID, triple11);
		
		double cost = 7 * provider.reservationOneYearFee + 107 * provider.reservedCpuCost + 107 * provider.monitoringCost 
						+ 105 * provider.onDemandCpuCost + 105 * provider.monitoringCost;
		double receipt = Math.ceil((user.consumedCpu - cpuLimit)/ONE_HOUR_IN_MILLIS) * extraCpuCost + setupCost + price; 
		
		assertEquals(receipt-cost, acc.calculateUtility(contract, user, provider), 0.0);
	}
}
