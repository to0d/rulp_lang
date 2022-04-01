package beta.test.control;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorTryTest extends RulpTestBase {

	@Test
	void test_try_1_default() {

		_setup();
		_run_script();
	}

	@Test
	void test_try_2_user_define() {
		_setup();
		_run_script();
	}

	@Test
	void test_try_3_unhandle() {
		_setup();
		_run_script();
	}
}
