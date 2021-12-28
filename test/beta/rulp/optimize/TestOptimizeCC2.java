package beta.rulp.optimize;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeCC2 extends RulpTestBase {

	@Test
	public void test_cc2_1_para_var() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cc2_2_para_expr() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cc2_2_recursion_fun() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cc2_3_lt0062_a() {

		_setup();
		_run_script();
		// fail("why test_cc3_3_lt0062_a is bad than test_cc3_3_lt0062_b????");
	}

	@Test
	public void test_cc2_3_lt0062_b() {

		_setup();
		_run_script();

	}
}
