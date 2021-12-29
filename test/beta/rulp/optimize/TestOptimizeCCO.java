package beta.rulp.optimize;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeCCO extends RulpTestBase {

	@Test
	public void test_cco_1_para_const() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cco_1_para_expr() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cco_1_para_var() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cco_2_duplicate_fun() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cco_3_recursion_fun() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cco_4_lt0062_default() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cco_4_lt0062_opt() {

		_setup();
		_run_script();

	}
	
	@Test
	public void test_cco_5_work_with_tco() {

		_setup();
		
		fail();
		
		_run_script();
	}
}
