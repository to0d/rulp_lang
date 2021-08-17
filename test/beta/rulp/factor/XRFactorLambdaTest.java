package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorLambdaTest extends RulpTestBase {

	@Test
	void test_1() {
		_setup();
		_test("((lambda (?v1 ?v2) (return (?v1 ?v2 3))) + 1)", "4");
	}

	@Test
	void test_2() {
		_setup();
		_test("(defun fun1 (?v) (return (+ ?v 1)))", "fun1");
		_test("(name-of fun1)", "\"(fun1 nil)\"");
		_test("(defun fun2 (?f) (return (?f)))", "fun2");
		_test("(name-of fun1)", "\"(fun1 nil)\"");
		_test("(fun2 (lambda () (return (fun1 1))))", "2");
		_gInfo("result/factor/XRFactorLambdaTest/test_2_ginfo.txt");
	}

	@Test
	void test_3() {

		_setup();
		_test("(defun fun1 (?v) (return (lambda () (return (* ?v ?v)))))", "fun1");
		_test("(name-of fun1)", "\"(fun1 nil)\"");
		_test("((fun1 2))", "4");
		_gInfo("result/factor/XRFactorLambdaTest/test_3_ginfo.txt");
	}

	@Test
	void test_4() {

		_setup();
		_test("(defun fun1 (?v) (return (lambda () (return (* ?v ?v)))))", "fun1");
		_test("(defvar ?f (fun1 2))", "&?f");
		_test("(?f)", "4");
		_test("(?f)", "4");
		_gInfo("result/factor/XRFactorLambdaTest/test_4_ginfo.txt");
	}

	@Test
	void test_5() {

		_setup();
		_test_script("result/factor/XRFactorLambdaTest/test_5.rulp");
		_gInfo("result/factor/XRFactorLambdaTest/test_5_ginfo.txt");
		_test("(setq ?f nil)", "&?f");
		_gInfo("result/factor/XRFactorLambdaTest/test_5_ginfo_b.txt");
	}
	

	@Test
	void test_6_extend_body() {
		_setup();
		_test("((lambda (?v) (if (> ?v 10) (return 2)) (if (> ?v 0) (return 1)) (return 0)) 11)", "2");
		_test("((lambda (?v) (if (> ?v 10) (return 2)) (if (> ?v 0) (return 1)) (return 0)) 5)", "1");
		_test("((lambda (?v) (if (> ?v 10) (return 2)) (if (> ?v 0) (return 1)) (return 0)) 0)", "0");
	}

}
