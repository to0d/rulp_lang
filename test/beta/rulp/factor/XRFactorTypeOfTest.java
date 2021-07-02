package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorTypeOfTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(type-of $(+ 1 2))", "int");
		_test("(type-of nil)", "null");
		_test("(type-of true)", "bool");
		_test("(type-of 1)", "int");
		_test("(type-of 1.1)", "float");
		_test("(type-of '(a b))", "list");
		_test("(type-of print-list)", "macro");
		_test("(type-of \"abc\")", "string");
		_test("(defvar v1 10)(type-of v1)", "&v1 var");
		_test("(type-of (+ 1 2))", "expr");
	}

}
