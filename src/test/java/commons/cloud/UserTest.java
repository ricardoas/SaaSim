/**
 * 
 */
package commons.cloud;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@link User}
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UserTest {
	
	private static final int STORAGE_IN_BYTES = 1024000;
	private static final double PRICE = 100.0;
	private static final double PRICE_WITH_USAGE = 150.0;
	private static final double SETUP = 1000.0;

	/**
	 * Test method for {@link commons.cloud.User#calculatePartialReceipt()}.
	 */
	@Test
	public void testCalculateReceiptWithNothingUsed() {
		Contract contract = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(contract.calculateReceipt(0, 0, 0, STORAGE_IN_BYTES)).andReturn(PRICE);
		EasyMock.replay(contract);
		
		User user = new User(contract, STORAGE_IN_BYTES);
		assertEquals(PRICE, user.calculatePartialReceipt(), 0.0);
	
		EasyMock.verify(contract);
	}

	/**
	 * Test method for {@link commons.cloud.User#calculatePartialReceipt()}.
	 */
	@Test
	public void testCalculateReceiptWithUsage() {
		Contract contract = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(contract.calculateReceipt(0, 0, 0, STORAGE_IN_BYTES)).andReturn(PRICE);
		EasyMock.expect(contract.calculateReceipt(100000, 100000, 100000, STORAGE_IN_BYTES)).andReturn(PRICE_WITH_USAGE);
		EasyMock.expect(contract.calculateReceipt(200000, 200000, 200000, STORAGE_IN_BYTES)).andReturn(PRICE_WITH_USAGE + PRICE_WITH_USAGE);
		EasyMock.replay(contract);
		
		User user = new User(contract, STORAGE_IN_BYTES);
		assertEquals(PRICE, user.calculatePartialReceipt(), 0.0);
		user.update(100000, 100000, 100000);
		assertEquals(PRICE_WITH_USAGE, user.calculatePartialReceipt(), 0.0);
		user.update(100000, 100000, 100000);
		user.update(100000, 100000, 100000);
		assertEquals(PRICE_WITH_USAGE + PRICE_WITH_USAGE, user.calculatePartialReceipt(), 0.0);
	
		EasyMock.verify(contract);
	}

	/**
	 * Test method for {@link commons.cloud.User#calculateOneTimeFees()}.
	 */
	@Test
	public void testCalculateOneTimeFees() {
		Contract contract = EasyMock.createStrictMock(Contract.class);
		EasyMock.expect(contract.calculateOneTimeFees()).andReturn(SETUP);
		EasyMock.replay(contract);
		
		User user = new User(contract, STORAGE_IN_BYTES);
		assertEquals(SETUP, user.calculateOneTimeFees(), 0.0);
	
		EasyMock.verify(contract);
	}

	/**
	 * Test method for {@link commons.cloud.User#compareTo(User)}.
	 */
	@Test
	public void testCompareTo() {
		Contract gold = EasyMock.createStrictMock(Contract.class);
		Contract silver = EasyMock.createStrictMock(Contract.class);
		
		EasyMock.expect(gold.compareTo(gold)).andReturn(0).times(2);
		EasyMock.expect(gold.compareTo(silver)).andReturn(-1);
		EasyMock.expect(silver.compareTo(gold)).andReturn(1);
		
		EasyMock.replay(gold, silver);
		
		
		User goldUser1 = new User(gold, STORAGE_IN_BYTES);
		User goldUser2 = new User(gold, STORAGE_IN_BYTES);
		User silverUser = new User(silver, STORAGE_IN_BYTES);
		
		assertEquals(0, goldUser1.compareTo(goldUser2));
		assertEquals(0, goldUser2.compareTo(goldUser1));
		
		assertEquals(-1, goldUser1.compareTo(silverUser));
		assertEquals(1, silverUser.compareTo(goldUser1));
		
		EasyMock.verify(gold, silver);
	}

}
