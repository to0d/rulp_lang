package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorToStringTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(to-string 123)", "\"123\"");
		_test("(to-string \"123\")", "\"123\"");
	}
}
