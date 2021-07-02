package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorAddTest extends RulpTestBase {

	@Test
	public void test1() {
		_setup();
		_test("(add '() a b)", "'(a b)");
		_test("(add '(a b) c)", "'(a b c)");
		_test("(add '(a b) c d)", "'(a b c d)");
		_test("(add '('(a b)) '(c d))", "'('(a b) '(c d))");
	}

}
