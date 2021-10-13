package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestList extends RulpTestBase {

	@Test
	public void test_add_1() {
		_setup();
		_test("(add '() a b)", "'(a b)");
		_test("(add '(a b) c)", "'(a b c)");
		_test("(add '(a b) c d)", "'(a b c d)");
		_test("(add '('(a b)) '(c d))", "'('(a b) '(c d))");
	}

	@Test
	public void test_add_all_1() {
		_setup();
		_test("(add-all '(a b) '())", "'(a b)");
		_test("(add-all '() '(a b))", "'(a b)");
		_test("(add-all '(a b) '(c))", "'(a b c)");
	}

	@Test
	void test_list_1() {

		_setup();

		_test("(defvar l1 '(a b c))", "&l1");
		_test("(size-of l1)", "3");
		_test("(is-empty l1)", "false");
		_test("(to-list l1)", "'(a b c)");
	}

}
