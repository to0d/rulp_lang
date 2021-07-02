package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorRemoveTest extends RulpTestBase {

	@Test
	void test_atom() {
		_test("(remove '(a b c) a)", "'(b c)");
		_test("(remove '(a a b c) a)", "'(b c)");
		_test("(remove '(a b b c) a)", "'(b b c)");
	}

}
