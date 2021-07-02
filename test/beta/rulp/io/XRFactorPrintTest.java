package beta.rulp.io;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorPrintTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(print (+ 1 2))", "nil", "3");
		_test("(print \"xyz\")", "nil", "xyz");
		_test("(print \"a\\nb\")", "nil", "a\nb");
		_test("(print (+ 1 2) \"a\")", "nil", "3a");
		_test("(print \"a\\\\b\")", "nil", "a\\b");

		_setup();
		_test("(defvar x 1)", "&x");
		_test("(print x)", "nil", "1");
	}
}
