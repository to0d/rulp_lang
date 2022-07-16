
package beta.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.XDay;

public class XDayTest extends RulpTestBase {

	@Test
	void test_getDay() {

		_setup();

		try {

			assertEquals("2015-02-28", XDay.getDay(2015, 2, 28).toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_getDayOfWeek() {

		_setup();

		try {

			assertEquals(1, XDay.getDay("2015-05-11").getDayOfWeek());
			assertEquals(2, XDay.getDay("2015-05-12").getDayOfWeek());
			assertEquals(3, XDay.getDay("2015-05-13").getDayOfWeek());
			assertEquals(4, XDay.getDay("2015-05-14").getDayOfWeek());
			assertEquals(5, XDay.getDay("2015-05-15").getDayOfWeek());
			assertEquals(6, XDay.getDay("2015-05-16").getDayOfWeek());
			assertEquals(0, XDay.getDay("2015-05-17").getDayOfWeek());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_getDiff() {

		_setup();

		try {

			assertEquals(0, XDay.getDiff(XDay.getDay("2015-12-1"), XDay.getDay("2015-12-1")));
			assertEquals(-1, XDay.getDiff(XDay.getDay("2015-12-2"), XDay.getDay("2015-12-1")));
			assertEquals(10, XDay.getDiff(XDay.getDay("2015-12-2"), XDay.getDay("2015-12-12")));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_getMinDay() {

		_setup();

		try {
			assertEquals("2015-01-01", XDay.getMinDay(2015, 1).toString());
			assertEquals("1993-07-01", XDay.getMinDay(1993, 3).toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_getNumDay() {

		_setup();

		try {
			assertTrue(XDay.getDay("2012-12-31").isSmallThan(XDay.getDay("2013-01-01")));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_getQuarter() {

		_setup();

		try {

			assertEquals(1, XDay.getDay("2015-1-1").getQuarter());
			assertEquals(1, XDay.getDay("2015-2-1").getQuarter());
			assertEquals(1, XDay.getDay("2015-3-1").getQuarter());
			assertEquals(2, XDay.getDay("2015-4-1").getQuarter());
			assertEquals(2, XDay.getDay("2015-5-1").getQuarter());
			assertEquals(2, XDay.getDay("2015-6-1").getQuarter());
			assertEquals(3, XDay.getDay("2015-7-1").getQuarter());
			assertEquals(3, XDay.getDay("2015-8-1").getQuarter());
			assertEquals(3, XDay.getDay("2015-09-1").getQuarter());
			assertEquals(4, XDay.getDay("2015-10-1").getQuarter());
			assertEquals(4, XDay.getDay("2015-11-1").getQuarter());
			assertEquals(4, XDay.getDay("2015-12-1").getQuarter());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_isBefore() {

		_setup();

		try {

			assertFalse(XDay.getDay("1991-12-09").isSmallThan(XDay.getDay("1991-11-29")));

			assertFalse(XDay.getDay("2015-1-1").isSmallThan(XDay.getDay("2015-1-1")));
			assertTrue(XDay.getDay("2015-1-1").isSmallThan(XDay.getDay("2015-1-2")));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_listDaysInQuarter() {

		_setup();

		try {
			assertEquals(90, XDay.listDaysInQuarter(2014, 1).size());
			assertEquals(91, XDay.listDaysInQuarter(2014, 2).size());
			assertEquals(92, XDay.listDaysInQuarter(2014, 3).size());
			assertEquals(92, XDay.listDaysInQuarter(2014, 4).size());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void testLastNextDay() {

		_setup();

		try {

			assertEquals("2015-02-28", XDay.getDay("2015-3-1").lastDay().toString());
			assertEquals("2014-12-31", XDay.getDay("2015-1-1").lastDay().toString());
			assertEquals("2015-03-01", XDay.getDay("2015-2-28").nextDay().toString());
			assertEquals("2016-01-01", XDay.getDay("2015-12-31").nextDay().toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void testToday() {

		_setup();

		XDay.today();
	}

}
