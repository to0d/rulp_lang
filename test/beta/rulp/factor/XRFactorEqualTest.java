package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorEqualTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(equal 1 1)", "true");
		_test("(equal 1 0)", "false");
		
		_test("(equal 1.1 1.1)", "true");
		_test("(equal 1.1 0.1)", "false");
		
		_test("(equal a a)", "true");
		_test("(equal a b)", "false");
		
		_test("(equal \"a\" \"a\")", "true");
		_test("(equal \"a\" \"b\")", "false");
		
	}

}
