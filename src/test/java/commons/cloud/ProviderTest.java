package commons.cloud;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.io.Checkpointer;
import commons.sim.components.MachineDescriptor;
import commons.util.DataUnit;

/**
 * test class for {@link Provider} 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class ProviderTest extends ValidConfigurationTest {
	
	private Provider amazon;
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
		Checkpointer.clear();
		
		buildFullConfiguration();
		amazon = Checkpointer.loadProviders()[1];
		assert amazon.getName().equals("amazon"): "Check providers order in iaas.providers file.";
	}

	@After
	public void tearDown(){
		Checkpointer.clear();
	}
	
	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyOnDemandMachineWithoutAType() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 3, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(false, MachineType.M1_SMALL));
		
		EasyMock.verify(typeProvider);
	}
	
	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyOnDemandMachineWithNullType() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 3, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(false, null));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyOnDemandMachineWithAvailableMachines() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 3, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertTrue(provider.canBuyMachine(false, MachineType.M1_LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyOnDemandMachineWithoutAvailableMachines() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 0, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(false, MachineType.M1_LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyReservedMachineNotProvided() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 0, 1, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(true, MachineType.M1_SMALL));
		
		EasyMock.verify(typeProvider);
	}
	
	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyReservedMachineWithNullType() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 0, 1, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(true, null));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyReservedMachineProvidedButUnavailable() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.expect(typeProvider.canBuy()).andReturn(false);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 0, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(true, MachineType.M1_LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#buyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test (expected = RuntimeException.class)
	public void testBuyMachineOfUnavailableType() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );	
		assertNull(provider.buyMachine(false, MachineType.M1_LARGE));
		EasyMock.verify(typeProvider);
	}
	
	/**
	 * Test method for {@link commons.cloud.Provider#buyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testBuyMachineOnDemandMachineUntilNoMachineIsAvailable() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.expect(typeProvider.buyMachine(false)).andReturn(new MachineDescriptor(1, false, MachineType.M1_SMALL, 0));
		EasyMock.expect(typeProvider.buyMachine(false)).andReturn(null);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertTrue(provider.canBuyMachine(false, MachineType.M1_SMALL));
		MachineDescriptor descriptor = provider.buyMachine(false, MachineType.M1_SMALL);
		assertNotNull(descriptor);
		assertFalse(descriptor.isReserved());
		assertEquals(MachineType.M1_SMALL, descriptor.getType());
		
		assertFalse(provider.canBuyMachine(false, MachineType.M1_SMALL));
		assertNull(provider.buyMachine(false, MachineType.M1_SMALL));

		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#buyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testBuyReservedMachineProvidedAndAvailableUntilIsOver() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.expect(typeProvider.canBuy()).andReturn(true);
		EasyMock.expect(typeProvider.buyMachine(true)).andReturn(new MachineDescriptor(1, true, MachineType.M1_LARGE, 0));
		EasyMock.expect(typeProvider.canBuy()).andReturn(false);
		EasyMock.expect(typeProvider.buyMachine(true)).andReturn(null);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 0, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertTrue(provider.canBuyMachine(true, MachineType.M1_LARGE));
		MachineDescriptor descriptor = provider.buyMachine(true, MachineType.M1_LARGE);
		assertNotNull(descriptor);
		assertTrue(descriptor.isReserved());
		assertEquals(MachineType.M1_LARGE, descriptor.getType());
		
		assertFalse(provider.canBuyMachine(true, MachineType.M1_LARGE));
		assertNull(provider.buyMachine(true, MachineType.M1_LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test 
	public void testShutdownOfInexistentType() {
		MachineDescriptor machineDescriptor = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.shutdownMachine(machineDescriptor));
		
		EasyMock.verify(typeProvider);
	}
	
	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentOnDemandMachine() {
		MachineDescriptor descriptor = new MachineDescriptor(111, false, MachineType.M1_LARGE, 0);
		MachineDescriptor existentDescriptor = new MachineDescriptor(1, false, MachineType.M1_LARGE, 0);

		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.expect(typeProvider.buyMachine(false)).andReturn(existentDescriptor);
		EasyMock.expect(typeProvider.shutdownMachine(descriptor)).andReturn(false);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		provider.buyMachine(false, MachineType.M1_LARGE);//Buying an on-demand machine
		
		assertFalse(provider.canBuyMachine(false, MachineType.M1_LARGE));
		assertFalse(provider.shutdownMachine(descriptor));
		assertFalse(provider.canBuyMachine(false, MachineType.M1_LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentReservedMachine() {
		MachineDescriptor descriptor = new MachineDescriptor(1, true, MachineType.M1_LARGE, 0);
		
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.expect(typeProvider.shutdownMachine(descriptor)).andReturn(false);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 3, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.shutdownMachine(descriptor));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownOnDemandMachine() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.expect(typeProvider.buyMachine(false)).andReturn(new MachineDescriptor(1, false, MachineType.M1_SMALL, 0));
		EasyMock.expect(typeProvider.shutdownMachine(EasyMock.isA(MachineDescriptor.class))).andReturn(true);
		
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		MachineDescriptor descriptor = provider.buyMachine(false, MachineType.M1_SMALL);
		assertNotNull(descriptor);
		assertFalse(provider.canBuyMachine(false, MachineType.M1_SMALL));
		assertTrue(provider.shutdownMachine(descriptor));
		assertTrue(provider.canBuyMachine(false, MachineType.M1_SMALL));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownReservedMachine() {
		MachineDescriptor descriptorToSell = new MachineDescriptor(1, true, MachineType.M1_LARGE, 0);
		
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_LARGE);
		EasyMock.expect(typeProvider.buyMachine(true)).andReturn(descriptorToSell);
		EasyMock.expect(typeProvider.canBuy()).andReturn(false);
		EasyMock.expect(typeProvider.shutdownMachine(descriptorToSell)).andReturn(true);
		EasyMock.expect(typeProvider.canBuy()).andReturn(true);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		MachineDescriptor descriptor = provider.buyMachine(true, MachineType.M1_LARGE);
		assertNotNull(descriptor);
		assertFalse(provider.canBuyMachine(true, MachineType.M1_LARGE));
		assertTrue(provider.shutdownMachine(descriptor));
		assertTrue(provider.canBuyMachine(true, MachineType.M1_LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#calculateCost(UtilityResultEntry, long)}.
	 */
	@Test
	public void testCalculateCostWithNoTransference() {
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addTransferenceToCost(0, 0, 0, 0, 0);

		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.expect(typeProvider.getTotalTransferences()).andReturn(new long[]{0, 0});
		typeProvider.calculateMachinesCost(entry, 0, 3.0);
		EasyMock.replay(entry, typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{0}, new double[]{0,0}, new long[]{1,10240,51200,153600}, new double[]{0,0.12,0.09,0.07,0.05}, Arrays.asList(typeProvider) );
		provider.calculateCost(entry , 0);
		
		EasyMock.verify(entry, typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#calculateCost(UtilityResultEntry, long)}.
	 */
	@Test
	public void testCalculateCostWithInTransferenceAndNoOutTransference() {
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addTransferenceToCost(0, 5 * DataUnit.GB.getBytes(), 0, 0, 0);

		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.expect(typeProvider.getTotalTransferences()).andReturn(new long[]{5 * DataUnit.GB.getBytes(), 0});
		typeProvider.calculateMachinesCost(entry, 0, 3.0);
		EasyMock.replay(entry, typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{0}, new double[]{0,0}, new long[]{1,10240,51200,153600}, new double[]{0,0.12,0.09,0.07,0.05}, Arrays.asList(typeProvider) );
		provider.calculateCost(entry , 0);
		
		EasyMock.verify(entry, typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#calculateCost(UtilityResultEntry, long)}.
	 */
	@Test
	public void testCalculateCostWithOutTransferenceBelowMinimum() {
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		entry.addTransferenceToCost(0, 0, 0, DataUnit.GB.getBytes()/2, 0);

		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.expect(typeProvider.getTotalTransferences()).andReturn(new long[]{0, DataUnit.GB.getBytes()/2});
		typeProvider.calculateMachinesCost(entry, 0, 3.0);
		EasyMock.replay(entry, typeProvider);
		
		long[] transferOutLimitsInBytes = DataUnit.convert(new long[]{1,10240,51200,153600}, DataUnit.GB, DataUnit.B);
		double[] transferOutCostsPerByte = DataUnit.convert(new double[]{0,0.12,0.09,0.07,0.05}, DataUnit.B, DataUnit.GB);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{0}, new double[]{0,0}, transferOutLimitsInBytes, transferOutCostsPerByte, Arrays.asList(typeProvider) );
		provider.calculateCost(entry , 0);
		
		EasyMock.verify(entry, typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#calculateCost(UtilityResultEntry, long)}.
	 */
	@Test
	public void testCalculateCostWithOutTransferenceAboveMaximum() {
		long[] transferOutLimits = DataUnit.convert(new long[]{1,10240,51200,153600}, DataUnit.GB, DataUnit.B);;
		double[] transferOutCosts = DataUnit.convert(new double[]{0,0.12,0.09,0.07,0.05}, DataUnit.B, DataUnit.GB);
		
		UtilityResultEntry entry = EasyMock.createStrictMock(UtilityResultEntry.class);
		long outTransference = 154000 * DataUnit.GB.getBytes();
		double expectedCost = transferOutLimits[0] * transferOutCosts[0] + 
								(transferOutLimits[1]-transferOutLimits[0]) * transferOutCosts[1] +
								(transferOutLimits[2]-transferOutLimits[1]) * transferOutCosts[2] +
								(transferOutLimits[3]-transferOutLimits[2]) * transferOutCosts[3] +
								(outTransference - transferOutLimits[3]) * transferOutCosts[4];
		
		entry.addTransferenceToCost(0, 0, 0, outTransference, expectedCost);

		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.expect(typeProvider.getTotalTransferences()).andReturn(new long[]{0, outTransference});
		typeProvider.calculateMachinesCost(entry, 0, 3.0);
		EasyMock.replay(entry, typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{0}, new double[]{0,0}, transferOutLimits, transferOutCosts, Arrays.asList(typeProvider) );
		provider.calculateCost(entry , 0);
		
		EasyMock.verify(entry, typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#calculateUniqueCost(UtilityResult)}.
	 */
	@Test
	public void testCalculateUniqueCostWithNoConsumption() {
		UtilityResult result = EasyMock.createStrictMock(UtilityResult.class);
		result.addProviderUniqueCost(0, MachineType.M1_SMALL, 0);
		
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.expect(typeProvider.calculateUniqueCost()).andReturn(0.0);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.M1_SMALL);
		EasyMock.replay(result, typeProvider);
		
		Provider provider = new Provider(0, "amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		provider.calculateUniqueCost(result);
		
		EasyMock.verify(result);
	}
	
	@Test
	public void testEqualsHashCodeConsistencyProviderWithSameID() {
		Provider provider1 = new Provider(1, "xpto", 0, 0, 0.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		Provider provider2 = new Provider(1, "xpto2", 0, 0, 0.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		
		assertTrue(provider1.equals(provider1));
		assertTrue(provider1.equals(provider2));
		assertEquals(provider1.hashCode(), provider2.hashCode());
	}
	
	@Test
	public void testEqualsHashCodeConsistencyWithDifferentID() {
		Provider provider1 = new Provider(1, "xpto", 0, 0, 0.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		Provider provider2 = new Provider(2, "xpto2", 0, 0, 0.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		
		assertTrue(provider1.equals(provider1));
		assertFalse(provider1.equals(provider2));
		assertNotSame(provider1.hashCode(), provider2.hashCode());
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithNullObject() {
		Provider provider1 = new Provider(1, "xpto", 0, 0, 0.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());

		assertFalse(provider1.equals(null));
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject() {
		Provider provider1 = new Provider(1, "xpto", 0, 0, 0.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		
		provider1.equals(new String(""));
	}
}
