package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrEqualTest extends RulpTestBase {

	@Test
	void test() {
		_test("(str-equal \"\" \"\")", "true");
		_test("(str-equal \"\" nil)", "false");
		_test("(str-equal \"abc\" \"abc\")", "true");
		_test("(str-equal \"abc\" \"ab\")", "false");
		_test("(str-equal \"abc\" \"abd\")", "false");
		_test("(str-equal \"abc\" \"Abc\")", "false");
		_test("(str-equal-nocase \"abc\" \"Abc\")", "true");

	}
}
