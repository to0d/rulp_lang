package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorBoolTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(not true)", "false");
		_test("(not false)", "true");

		_test("(and)", "true");
		_test("(and true)", "true");
		_test("(and false)", "false");
		_test("(and true false)", "false");
		_test("(and true true)", "true");
		_test("(and false false)", "false");
		_test("(and true true true)", "true");

		_test("(or)", "false");
		_test("(or true)", "true");
		_test("(or false)", "false");
		_test("(or true false)", "true");
		_test("(or true true)", "true");
		_test("(or false false)", "false");
		_test("(or false false false)", "false");
	}

}
