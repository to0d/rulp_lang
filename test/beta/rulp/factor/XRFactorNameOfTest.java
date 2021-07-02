package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorNameOfTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(name-of nil)", "nil");
		_test("(name-of true)", "\"nan\"");
		_test("(name-of 1)", "\"nan\"");
		_test("(name-of 1.1)", "\"nan\"");
		_test("(name-of '(a b))", "\"nan\"");
		_test("(name-of print-list)", "\"print-list\"");
	}

}
