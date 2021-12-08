package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorIfTest extends RulpTestBase {

	@Test
	void test1() {
		_setup();
		_test("(if (> 2 1) 100 101)", "100");
		_test("(if (> 1 2) 100 101)", "101");
	}

	@Test
	void test2() {
		_setup();
		_test("(defvar v1 1) v1", "&v1 1");
		_test("(defvar v2 2) v2", "&v2 2");
		_test("(if (> 2 1) (do (setq v1 (+ v1 1)) (setq v2 (+ v2 2)))) v1 v2", "nil 2 4");
	}

	@Test
	void test3() {
		_setup();
		_test("(defvar ?v true)", "&?v");
		_test("(if ?v 1 2)", "1");
	}

	@Test
	void test4() {
		_setup();
		_test("(defclass c1 () (defvar ?var false) (defun fun1 () (if ?var (return 1) (return 2))))", "c1");
		_test("(new c1 o1)", "o1");
		_test("(o1::fun1)", "2");
	}
}
