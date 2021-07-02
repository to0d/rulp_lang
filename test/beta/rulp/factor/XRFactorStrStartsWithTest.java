package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrStartsWithTest extends RulpTestBase {

	@Test
	void test() {
		_test("(str-start-with \"abc5\" \"abc\")", "true");
		_test("(str-start-with \"*TEST\" \"*\")", "true");
		_test("(str-start-with \"*TEST\" \"X\")", "false");
	}
}
