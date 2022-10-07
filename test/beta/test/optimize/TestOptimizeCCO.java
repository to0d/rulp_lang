package beta.test.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeCCO extends RulpTestBase {

	@Test
	void test_cco_1_para_const() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_1_para_expr() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_1_para_var() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_2_duplicate_fun() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_3_recursion_fun() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_4_lt0062_default() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_4_lt0062_opt() {

		_setup();
		_run_script();

	}

	@Test
	void test_cco_5_work_with_tco() {

		// should fail

		_setup();
		_run_script();
	}

	@Test
	void test_cco_6_infinite_loop_detected_recursion() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_7_lt0202_try() {

		_setup();
		_run_script();
	}

	@Test
	void test_cco_8_simple_fun() {

		_setup();
		_run_script();
	}
}
