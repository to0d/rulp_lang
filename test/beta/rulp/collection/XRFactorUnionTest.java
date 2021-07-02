package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class XRFactorUnionTest extends RulpTestBase {

	@Test
	void test_atom() {
		_test("(union '(a b c) '(x y z))", "'(a b c x y z)");
		_test("(union '('(a b c) '(b c)))", "'(a b c)");
	}

	@Test
	void test_list() {
		_test("(sort (union '('(a b c) '(x y z)) '('(x y z))))", "'('(a b c) '(x y z) '(x y z))");
	}
}
