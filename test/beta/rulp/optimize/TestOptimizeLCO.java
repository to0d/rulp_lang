package beta.rulp.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeLCO extends RulpTestBase {

	@Test
	void test_lco_1_by_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_2_power_x_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_2_power_x_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_2_power_y_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_3_and_false() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_4_or_true() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_5_simple_fun() {
		_setup();
		_run_script();
	}

	@Test
	void test_cco_6_work_with_tco() {
		_setup();
		_run_script();
	}
}
