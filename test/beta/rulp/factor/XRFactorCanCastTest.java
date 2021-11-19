package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorCanCastTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(can-cast int true)", "false");
		_test("(can-cast int 123)", "true");
		_test("(can-cast int \"abc\")", "false");
		_test("(can-cast int \"123\")", "true");

		_test("(can-cast float \"123\")", "true");
		_test("(can-cast float \"12.1\")", "true");

		_test("(can-cast '(int float) \"12.1\")", "true");
		_test("(can-cast '(int float) \"123\")", "true");
	}

}
