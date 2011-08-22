/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.sim.components.MachineDescriptor;

/**
 * test class for {@link Provider} 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class ProviderTest {
	
	private Provider amazon;
	
	@Before
	public void setUp() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
		amazon = Configuration.getInstance().getProviders().get(0);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyOnDemandMachineWithAvailableMachines() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 3, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertTrue(provider.canBuyMachine(false, null));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyOnDemandMachineWithoutAvailableMachines() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 0, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(false, MachineType.LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyReservedMachineNotProvided() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 0, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(true, MachineType.SMALL));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#canBuyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testCanBuyReservedMachineProvidedButUnavailable() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.expect(typeProvider.canBuy()).andReturn(false);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 0, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.canBuyMachine(true, MachineType.LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#buyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testBuyMachineOnDemandMachineUntilNoMachineIsAvailable() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.SMALL);
		EasyMock.expect(typeProvider.buyMachine(false)).andReturn(new MachineDescriptor(1, false, MachineType.SMALL));
		EasyMock.expect(typeProvider.buyMachine(false)).andReturn(null);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertTrue(provider.canBuyMachine(false, MachineType.SMALL));
		MachineDescriptor descriptor = provider.buyMachine(false, MachineType.SMALL);
		assertNotNull(descriptor);
		assertFalse(descriptor.isReserved());
		assertEquals(MachineType.SMALL, descriptor.getType());
		
		assertFalse(provider.canBuyMachine(false, MachineType.SMALL));
		assertNull(provider.buyMachine(false, MachineType.SMALL));

		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#buyMachine(boolean, commons.cloud.MachineType)}.
	 */
	@Test
	public void testBuyReservedMachineProvidedAndAvailableUntilIsOver() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.expect(typeProvider.canBuy()).andReturn(true);
		EasyMock.expect(typeProvider.buyMachine(true)).andReturn(new MachineDescriptor(1, true, MachineType.LARGE));
		EasyMock.expect(typeProvider.canBuy()).andReturn(false);
		EasyMock.expect(typeProvider.buyMachine(true)).andReturn(null);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 0, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertTrue(provider.canBuyMachine(true, MachineType.LARGE));
		MachineDescriptor descriptor = provider.buyMachine(true, MachineType.LARGE);
		assertNotNull(descriptor);
		assertTrue(descriptor.isReserved());
		assertEquals(MachineType.LARGE, descriptor.getType());
		
		assertFalse(provider.canBuyMachine(true, MachineType.LARGE));
		assertNull(provider.buyMachine(true, MachineType.LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentOnDemandMachine() {
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.LARGE);

		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.expect(typeProvider.shutdownMachine(descriptor)).andReturn(false);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 3, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.shutdownMachine(descriptor));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownInexistentReservedMachine() {
		MachineDescriptor descriptor = new MachineDescriptor(1, true, MachineType.LARGE);
		
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.expect(typeProvider.shutdownMachine(descriptor)).andReturn(false);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 3, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		assertFalse(provider.shutdownMachine(descriptor));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownOnDemandMachine() {
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.SMALL);
		EasyMock.expect(typeProvider.buyMachine(false)).andReturn(new MachineDescriptor(1, false, MachineType.SMALL));
		EasyMock.expect(typeProvider.shutdownMachine(EasyMock.isA(MachineDescriptor.class))).andReturn(true);
		
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		MachineDescriptor descriptor = provider.buyMachine(false, MachineType.SMALL);
		assertNotNull(descriptor);
		assertFalse(provider.canBuyMachine(false, MachineType.SMALL));
		assertTrue(provider.shutdownMachine(descriptor));
		assertTrue(provider.canBuyMachine(false, MachineType.SMALL));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#shutdownMachine(commons.sim.components.MachineDescriptor)}.
	 */
	@Test
	public void testShutdownReservedMachine() {
		MachineDescriptor descriptorToSell = new MachineDescriptor(1, true, MachineType.LARGE);
		
		TypeProvider typeProvider = EasyMock.createStrictMock(TypeProvider.class);
		EasyMock.expect(typeProvider.getType()).andReturn(MachineType.LARGE);
		EasyMock.expect(typeProvider.buyMachine(true)).andReturn(descriptorToSell);
		EasyMock.expect(typeProvider.canBuy()).andReturn(false);
		EasyMock.expect(typeProvider.shutdownMachine(descriptorToSell)).andReturn(true);
		EasyMock.expect(typeProvider.canBuy()).andReturn(true);
		EasyMock.replay(typeProvider);
		
		Provider provider = new Provider("amazon", 1, 0, 3.0, new long[]{}, new double[]{}, new long[]{}, new double[]{}, Arrays.asList(typeProvider) );
		MachineDescriptor descriptor = provider.buyMachine(true, MachineType.LARGE);
		assertNotNull(descriptor);
		assertFalse(provider.canBuyMachine(true, MachineType.LARGE));
		assertTrue(provider.shutdownMachine(descriptor));
		assertTrue(provider.canBuyMachine(true, MachineType.LARGE));
		
		EasyMock.verify(typeProvider);
	}

	/**
	 * Test method for {@link commons.cloud.Provider#calculateCost(UtilityResultEntry, long)}.
	 */
	@Test
	public void testCalculateCost() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.cloud.Provider#calculateUniqueCost(UtilityResult)}.
	 */
	@Test
	public void testCalculateUnicCost() {
		fail("Not yet implemented");
	}
}
