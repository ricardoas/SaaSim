package commons.sim.jeevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class JETimeTest {
	
	/**
	 * This is quite realistic for our usage.
	 */
	private static final int UPPERBOUND = 3 * 365 * 24 * 60 * 60 * 1000;
	private Random random;
	private int firstTime;
	private int secondTime;

	@Before
	public void setUpBeforeClass() {
		this.random = new Random();
		firstTime = random.nextInt(UPPERBOUND);
		secondTime = random.nextInt(UPPERBOUND);
		if(secondTime < firstTime){
			int tmp = firstTime;
			firstTime = secondTime;
			secondTime = tmp;
		}
	}

	@Test
	public void testPlus() {
		assertEquals(new JETime(firstTime+secondTime), new JETime(firstTime).plus(new JETime(secondTime)));
	}
	
	@Test
	public void testPlusWithinfinity() {
		assertEquals(JETime.INFINITY, new JETime(firstTime).plus(JETime.INFINITY));
		assertEquals(JETime.INFINITY, JETime.INFINITY.plus(new JETime(firstTime)));
		assertEquals(JETime.INFINITY, JETime.INFINITY.plus(JETime.INFINITY));
	}
	
	
	
	@Test
	public void testCompareTo() {
		assertEquals(-1, new JETime(firstTime).compareTo(new JETime(secondTime)));
		assertEquals(1, new JETime(secondTime).compareTo(new JETime(firstTime)));
		assertEquals(0, new JETime(firstTime).compareTo(new JETime(firstTime)));
	}

	@Test
	public void testCompareToInfinity() {
		assertEquals(-1, new JETime(firstTime).compareTo(JETime.INFINITY));
		assertEquals(1, JETime.INFINITY.compareTo(new JETime(firstTime)));
		assertEquals(1, JETime.INFINITY.compareTo(JETime.INFINITY));
	}

	@Test
	public void testIsEarlierThan() {
		assertTrue(new JETime(firstTime).isEarlierThan(new JETime(secondTime)));
		assertFalse(new JETime(secondTime).isEarlierThan(new JETime(firstTime)));
		assertFalse(new JETime(firstTime).isEarlierThan(new JETime(firstTime)));
	}

	@Test
	public void testIsEarlierThanInfinity() {
		assertTrue(new JETime(firstTime).isEarlierThan(JETime.INFINITY));
		assertFalse(JETime.INFINITY.isEarlierThan(new JETime(firstTime)));
		assertFalse(JETime.INFINITY.isEarlierThan(JETime.INFINITY));
	}

	@Test
	public void testEquals() {
		assertFalse(new JETime(firstTime).equals(new JETime(secondTime)));
		assertFalse(new JETime(secondTime).equals(new JETime(firstTime)));
		assertTrue(new JETime(firstTime).equals(new JETime(firstTime)));
	}

	@Test
	public void testEqualsToInfinity() {
		assertFalse(new JETime(firstTime).equals(JETime.INFINITY));
		assertFalse(JETime.INFINITY.equals(new JETime(firstTime)));
		assertTrue(JETime.INFINITY.equals(JETime.INFINITY));
	}

	@Test
	public void testToString() {
		assertEquals(Integer.toString(firstTime), new JETime(firstTime).toString());
		assertEquals("INFINITY", JETime.INFINITY.toString());
	}

}
