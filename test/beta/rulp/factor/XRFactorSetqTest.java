package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorSetqTest extends RulpTestBase {

	@Test
	void test_1() {
		_setup();
		_test("(defvar x 10) x", "&x 10");
		_test("(setq x 9) x", "&x 9");
	}

	@Test
	void test_2() {
		_setup();
		_test_error("(setq x 9)", "var not found: x\nat main: (setq x 9)");
	}

	@Test
	void test_err_1() {

		_setup();
		_run_script();
	}
}
