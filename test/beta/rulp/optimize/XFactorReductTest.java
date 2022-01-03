package beta.rulp.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XFactorReductTest extends RulpTestBase {

	@Test
	void test_reduct_1_num_1_const() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_2_str_1_const() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_3_logical_1_const() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_3_logical_2_var() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_4_multi_1_unchanged_var() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_4_multi_2_div_var() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_5_expr_special_num() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_5_expr_var_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_5_expr_var_2() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_5_expr_var_3() {

		_setup();
		_run_script();
	}

	@Test
	void test_reduct_6_by_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_reduct_6_add_0() {
		_setup();
		_run_script();
	}
	
	@Test
	void test_reduct_6_sub_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_reduct_6_power_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_reduct_6_factor_5_and_false() {
		_setup();
		_run_script();
	}

	@Test
	void test_reduct_6_factor_6_or_true() {
		_setup();
		_run_script();
	}

}
