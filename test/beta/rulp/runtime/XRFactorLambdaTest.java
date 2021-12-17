package beta.rulp.runtime;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorLambdaTest extends RulpTestBase {

	@Test
	void test_lambda_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_lambda_2() {
		_setup();
		_run_script();
	}

	@Test
	void test_3() {

		_setup();
		_test("(defun fun1 (?v) (return (lambda () (return (* ?v ?v)))))", "fun1");
		_test("(name-of fun1)", "\"(fun1 nil)\"");
		_test("((fun1 2))", "4");
		_gInfo("result/runtime/XRFactorLambdaTest/test_3_ginfo.txt");
	}

	@Test
	void test_4() {

		_setup();
		_test("(defun fun1 (?v) (return (lambda () (return (* ?v ?v)))))", "fun1");
		_test("(defvar ?f (fun1 2))", "&?f");
		_test("(?f)", "4");
		_test("(?f)", "4");
		_gInfo("result/runtime/XRFactorLambdaTest/test_4_ginfo.txt");
	}

	@Test
	void test_5() {

		_setup();
		_run_script();
		_gInfo("result/runtime/XRFactorLambdaTest/test_5_ginfo.txt");
		_test("(setq ?f nil)", "&?f");
		_gInfo("result/runtime/XRFactorLambdaTest/test_5_ginfo_b.txt");
	}

	@Test
	void test_6_extend_body() {
		_setup();
		_test("((lambda (?v) (if (> ?v 10) (return 2)) (if (> ?v 0) (return 1)) (return 0)) 11)", "2");
		_test("((lambda (?v) (if (> ?v 10) (return 2)) (if (> ?v 0) (return 1)) (return 0)) 5)", "1");
		_test("((lambda (?v) (if (> ?v 10) (return 2)) (if (> ?v 0) (return 1)) (return 0)) 0)", "0");
	}

}
