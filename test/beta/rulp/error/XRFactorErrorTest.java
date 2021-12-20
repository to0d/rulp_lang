package beta.rulp.error;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorErrorTest extends RulpTestBase {

	@Test
	void test_error_2_user_define_handle_case() {
		_setup();
		_run_script();
	}

	@Test
	void test_error_1_default_handle_case() {

		_setup();
		_run_script();
	}

	@Test
	void test_error_3_unhandle_case_1() {
		_setup();
		_run_script();
	}
}
