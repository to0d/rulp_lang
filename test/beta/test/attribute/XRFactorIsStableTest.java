package beta.test.attribute;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorIsStableTest extends RulpTestBase {

	@Test
	void test_is_stable_0() {
		_setup();
		_run_script();
	}

	@Test
	void test_is_stable_1_fun_simple() {
		_setup();
		_run_script();
	}
	
	@Test
	void test_is_stable_2_fun_recursion_cycle() {

		_setup();
		_run_script();
	}

	@Test
	void test_is_stable_2_fun_recursion_self() {

		_setup();
		_run_script();
	}

	@Test
	void test_is_stable_3_fun_list() {

		_setup();
		_run_script();
	}

	@Test
	void test_is_stable_4_max() {
		_setup();
		_run_script();
	}
}
