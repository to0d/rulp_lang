package beta.rulp.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeLCO extends RulpTestBase {

	@Test
	void test_lco_1_factor_1_by_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_1_factor_2_power_x_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_1_factor_3_power_x_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_1_factor_4_power_y_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_1_factor_5_and_false() {
		_setup();
		_run_script();
	}

	@Test
	void test_lco_1_factor_6_or_true() {
		_setup();
		_run_script();
	}

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
}
