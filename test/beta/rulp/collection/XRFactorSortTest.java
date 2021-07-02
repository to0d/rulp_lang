package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorSortTest extends RulpTestBase {

	@Test
	void test_atom() {
		_test("(sort '())", "'()");
		_test("(sort '(a))", "'(a)");
		_test("(sort '(a b c))", "'(a b c)");
		_test("(sort '(c b a))", "'(a b c)");
	}

}
