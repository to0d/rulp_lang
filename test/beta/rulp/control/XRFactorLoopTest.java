package beta.rulp.control;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorLoopTest extends RulpTestBase {

	@Test
	void test_loop_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_loop_2_continue() {
		_setup();
		_run_script();
	}

	@Test
	void test_loop_3_break() {
		_setup();
		_run_script();
	}

	@Test
	void test_loop_4_err_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_loop_5_return() {
		_setup();
		_run_script();
	}
	
	@Test
	void test_loop_6_by() {

		_setup();
		_run_script();
	}
}
