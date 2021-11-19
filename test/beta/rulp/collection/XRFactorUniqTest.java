package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorUniqTest extends RulpTestBase {

	@Test
	void test_atom() {
		_test("(uniq '(a b c))", "'(a b c)");
		_test("(uniq '(a b c a))", "'(a b c)");

		_test("(uniq '(5 4 3 2 1))", "'(5 4 3 2 1)");
		_test("(uniq '(5 4 3 2 1) (lambda (?v) (return (% ?v 3))))", "'(5 4 3)");

		_test("(uniq '('(a b c) '(x y z)))", "'('(a b c) '(x y z))");
	}

}
