package beta.rulp.factor.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrStartsWithTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-start-with \"abc5\" \"abc\")", "true");
		_test("(str-start-with \"*TEST\" \"*\")", "true");
		_test("(str-start-with \"*TEST\" \"X\")", "false");
	}
}
