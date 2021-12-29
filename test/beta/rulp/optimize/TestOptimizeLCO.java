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

}
