package commons.sim.jeevent;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JETimeTest {
	
	/**
	 * This is quite realistic for our usage.
	 */
	private final int UPPERBOUND = 3 * 365 * 24 * 60 * 60 * 1000;
	private Random random;

	@BeforeClass
	public void setUpBeforeClass() throws Exception {
		this.random = new Random();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPlus() {
		int firstTime = random.nextInt(UPPERBOUND);
		int secondTime = random.nextInt(UPPERBOUND);
		int result = firstTime+secondTime;
		
		assertEquals(new JETime(result), new JETime(firstTime).plus(new JETime(secondTime)));
	}
	
	@Test
	public void testPlusWithinfinity() {
		int firstTime = random.nextInt(UPPERBOUND);
		
		assertEquals(JETime.INFINITY, new JETime(firstTime).plus(JETime.INFINITY));
		assertEquals(JETime.INFINITY, JETime.INFINITY.plus(new JETime(firstTime)));
		assertEquals(JETime.INFINITY, JETime.INFINITY.plus(JETime.INFINITY));
	}
	
	
	
	@Test
	public void testIsEarlierThan() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

}
