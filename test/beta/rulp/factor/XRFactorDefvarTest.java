package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDefvarTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defvar x 10) x", "&x 10");
		_test("(defvar y) y", "&y nil");
		_test("(ls)", "'(main::main main::root main::system main::x main::y)");

	}

	@Test
	void test2() {

		_setup();

		_test("(defvar x 10) x", "&x 10");

		// Can't redefine
		_test_error("(defvar x 9) x", "duplicate local variable: x\n" + "at main: (defvar x 9)");

		_test("(defun fun1 () (defvar x 11) (return x))");
		_test("(fun1)", "11");
	}
}
