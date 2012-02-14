package saasim.util;

import static org.junit.Assert.*;

import org.junit.Test;

import saasim.util.SimulationInfo;

public class SimulationInfoTest {

	@Test
	public void testSimulationInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddDay() {
		SimulationInfo info = new SimulationInfo(0, 0, 31);
		
		assertEquals(0, info.getCurrentDay());
		info.addDay();
		assertEquals(1, info.getCurrentDay());
	}

	@Test
	public void testIsNotFinishDay() {
		assertFalse(new SimulationInfo(0, 0, 5).isFinishDay());
	}

	@Test
	public void testIsFirstAndFinishDay() {
		assertTrue(new SimulationInfo(0, 0, 0).isFinishDay());
	}

	@Test
	public void testIsFinishDay() {
		SimulationInfo info = new SimulationInfo(0, 0, 1);
		assertFalse(info.isFinishDay());
		info.addDay();
		System.out.println(info.getCurrentDay());
		assertTrue(info.isFinishDay());
	}

	@Test
	public void testIsFirstDay() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsChargeDay() {
		fail("Not yet implemented");
	}

}
