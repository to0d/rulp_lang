package beta.rulp.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XFactorReductTest extends RulpTestBase {

	@Test
	public void test_reduct_1_num_1_const() {

		_setup();
		_run_script();
	}

	@Test
	public void test_reduct_2_str_1_const() {

		_setup();
		_run_script();
	}

	@Test
	public void test_reduct_3_logical_1_const() {

		_setup();
		_run_script();
	}

	@Test
	public void test_reduct_3_logical_2_var() {

		_setup();
		_run_script();
	}

	@Test
	public void test_reduct_4_multi_1_unchanged_var() {

		_setup();
		_run_script();
	}
	
	@Test
	public void test_reduct_4_multi_2_div_var() {

		_setup();
		_run_script();
	}
}
