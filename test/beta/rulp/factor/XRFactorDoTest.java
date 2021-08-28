package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDoTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defvar x 10) (print x)", "&x nil", "10");

		_setup();
		_test("(do (defvar x 10) (print x))", "nil", "10");
		_test("(do (defvar x 11) (print x))", "nil", "11");

	}
}