package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorJoinTest extends RulpTestBase {

	@Test
	void test_atom() {
		_test("(join '(a b c) '(x y z))", "'()");
		_test("(join '(a b c) '(x y a))", "'(a)");
	}

	@Test
	void test_list() {
		_test("(join '('(a b c) '(x y z)) '('(x y z)))", "'()");
	}

}
