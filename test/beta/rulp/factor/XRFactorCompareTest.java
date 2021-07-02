package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorCompareTest extends RulpTestBase {

	@Test
	void test() {
	
		_test("(cmp a a)", "0");
		_test("(cmp a b)", "-1");
		_test("(cmp a nil)", "-13");
		_test("(cmp nil a)", "13");
		_test("(cmp nil nil)", "0");
		
		_test("(cmp 1 1)", "0");
		_test("(cmp 1 1.1)", "-2");
		_test("(cmp 1.1 1)", "2");

	}
}
