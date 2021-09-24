package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorNameOfTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(name-of nil)", "nil");
		_test("(name-of true)", "nil");
		_test("(name-of 1)", "nil");
		_test("(name-of 1.1)", "nil");
		_test("(name-of '(a b))", "nil");
		_test("(name-of print-list)", "\"print-list\"");
		_test("(name-of n1:'(a b))", "\"n1\"");
	}

}
