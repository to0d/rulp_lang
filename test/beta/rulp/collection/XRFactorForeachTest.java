package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorForeachTest extends RulpTestBase {

	@Test
	void test_foreach_1() {

		_setup();
		_test("(foreach (?a '(1 2 3)) (+ ?a 1))", "'(2 3 4)");
		_test("(foreach (?a '(1 2 3)) (+ ?a 1) (+ ?a 2))", "'(3 4 5)");
		_test("(foreach (?a '(1 2 3 4 5)) (if (> ?a 3) (return 1)) (return ?a))", "'(1 2 3 1 1)");
		_test("(foreach (?a '(1 2 3 4 5)) (if (= ?a 3) (continue)) (return ?a))", "'(1 2 4 5)");
		_test("(foreach (?a '(1 2 3 4 5)) (if (= ?a 3) (break)) (return ?a))", "'(1 2)");
		_test("(foreach (?a '(1 2 3)) (return '(?a 1)))", "'('(1 1) '(2 1) '(3 1))");
	}

	@Test
	void test_foreach_2() {

		_setup();
		_test("(defun fun1 () (return '(1 2 3)))", "fun1");
		_test("(name-of fun1)", "\"(fun1)\"");
		_test("(foreach (?a (fun1)) (+ ?a 1))", "'(2 3 4)");
	}
}
