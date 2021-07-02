package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorRefTest extends RulpTestBase {

	@Test
	void test_ref_1() {

		_setup();

		_test("(defvar x 10) x", "&x 10");
		_test("x", "10");

		// reference exist variable
		_test("(setq (ref x) 1)", "&x");
		_test("x", "1");

		// reference not exist variable
		_test("(ref y)", "&y");
		_test("y", "nil");

		// reference exist variable
		_test("(setq (ref z) 2)", "&z");
		_test("z", "2");
	}
}
