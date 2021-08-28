package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorLoopTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defvar x) (setq x 5)", "&x &x");
		_test("(loop (setq x (- x 1)) (print x \",\") (when (< x 2) (break)))", "nil", "4,3,2,1,");

		_test("(loop for x in '(1 2 3) do (print x \",\"))", "nil", "1,2,3,");
		_test("(loop for x from 1 to 3 do (print x \",\"))", "nil", "1,2,3,");
		_test("(loop for x from 3 to 1 do (print x \",\"))", "nil", "");
		_test("(loop for x from (+ 1 2) to (- 6 2) do (print x \",\"))", "nil", "3,4,");
		_test("(loop for x from 1 to 3 do (print x 1))", "nil", "112131");
	}

	@Test
	void test2() {
		_setup();
		_test("(loop for x in '(1 2 3 4) do (if (= x 3) (continue)) (print x \",\"))", "nil", "1,2,4,");
		_test("(loop for x in '(1 2 3 4) do (if (= x 3) (break)) (print x \",\"))", "nil", "1,2,");
	}

	@Test
	void test_loop_err_1() {

		_setup();
		_test_script("result/factor/XRFactorLoopTest/test_loop_err_1.rulp");
	}
}
