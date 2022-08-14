package beta.test.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeTCO extends RulpTestBase {

	@Test
	void test_tco_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_2_overflow_a() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_2_overflow_b() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_3_return() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_4_if() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_5_multi() {

		_setup();
		_run_script();

//		fail("how to optimize");
//		_test("(fun2 1001)", "45866");
//		assertEquals(0, CPSUtils.getCPSCount());
	}

	@Test
	void test_tco_6_recursion_cycle() {
		_setup();
		_run_script();
	}

	@Test
	void test_tco_7_str() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_8_no_op() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_9_infinite_recursion_detected() {

		_setup();
		_run_script();
	}

	@Test
	void test_tco_a_reduct_by_0() {

		_setup();
		_run_script();
	}
}
