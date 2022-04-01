package beta.test.thread;

import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDoParallelTest extends RulpTestBase {

	@Test
	void test_do_p_1() {

		_setup();
		_test("(defvar count)");
		_test("(defun fun1 ((?t1 int) ?v2) (do (sleep ?t1) (setq count (+ count 1)) (return ?v2)))", "fun1");
		_test("(name-of fun1)", "\"(fun1 int nil)\"");

		_test_time("(fun1 50 true)", "true", 1, 50);
		_test_time("(and (fun1 50 true) (fun1 100 true))", "true", 2, 150);
		_test_time("(or (fun1 50 true) (fun1 100 true))", "true", 1, 50);
		_test_time("(or (fun1 50 false) (fun1 100 true))", "true", 2, 150);

		_test_time("(do-p (fun1 50 false) (fun1 100 true))", "false", 1, 50);
		_test_time("(do-p (fun1 300 false) (fun1 600 true))", "false", 1, 300);
		_test_time("(do-p (do (fun1 600 false) (fun1 500 false)) (fun1 500 true))", "true", 1, 500);
	}

	void _test_time(String expr, String out, int count, int min_time) {

		_test("(setq count 0)");
		long t1 = System.currentTimeMillis();
		_test(expr, out);
		long t2 = System.currentTimeMillis() - t1;
		_test("count", "" + count);

		if (t2 < min_time || t2 > min_time + 100) {
			fail(String.format("time expect=%d, actual=%d", min_time, t2));
		}
	}

}
