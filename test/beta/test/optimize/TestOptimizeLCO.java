package beta.test.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeLCO extends RulpTestBase {

	@Test
	void test_lco_2_fun_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_2_fun_2_has_para_type() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_3_tco_by_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_4_recursion_1() {
		_setup();
		_run_script();
	}
	

	@Test
	void test_lco_4_recursion_2() {
		_setup();
		_run_script();
	}
}
