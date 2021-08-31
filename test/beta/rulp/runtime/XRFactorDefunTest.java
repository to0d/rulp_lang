package beta.rulp.runtime;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

class XRFactorDefunTest extends RulpTestBase {

	@Test
	void test_fun_0_invalid_type() {

		_setup();

		_test_error("(defun fun2 ((?x Expr)) (print ?x))",
				"Undefined para type: Expr\n" + "at main: (defun fun2 ((?x Expr)) (print ?x))");

	}

	@Test
	void test_fun_1() {
		_setup();
		_test("(defun fun1 (?v1 ?v2) (return (?v1 ?v2 3))) (fun1 + 1)", "fun1 4");
		_test("(name-of fun1)", "\"(fun1 nil nil)\"");
		_test("(defun fun2 (?v1 ?v2) (do (?v1 ?v2 3) (return (?v1 ?v2 4)))) (fun2 + 1)", "fun2 5");
		_test("(name-of fun2)", "\"(fun2 nil nil)\"");
	}

	@Test
	void test_fun_2_ref_1() {

		_setup();

		_test("(defvar x 10) x", "&x 10");
		_test("(defun fun1 (?p1) (setq ?p1 (+ ?p1 1)))", "fun1");
		_test("(name-of fun1)", "\"(fun1 nil)\"");

		// pass value
		_test("(fun1 x)");
		_test("x", "10");
	}

	@Test
	void test_fun_2_ref_2() {

		_setup();
		_test("(defvar x 10) x", "&x 10");
		_test("(defun fun1 (?p1) (setq ?p1 (+ ?p1 1)))", "fun1");
		_test("(name-of fun1)", "\"(fun1 nil)\"");

		// pass reference
		_test("(fun1 &x)");
		_test("x", "11");
	}

	@Test
	void test_fun_3_overload_arg_number() {

		_setup();

		_test("(defun fun (?v) (return (+ ?v 1)))", "fun");
		_test("(name-of fun)", "\"(fun nil)\"");
		_test("(fun 1)", "2");

		_test("(defun fun (?v1 ?v2) (return (+ ?v1 ?v2)))", "fun");
		_test("(name-of fun)", "\"'((fun nil nil) (fun nil))\"");
		_test("(fun 1 2)", "3");

		_test("(out-to-file \"result/runtime/XRFactorDefunTest/test_fun_3_overload_arg_number_frame_1.txt\" (print-subject))");
	}

	@Test
	void test_fun_3_overload_arg_type_1() {

		_setup();

		_test_script("result/runtime/XRFactorDefunTest/test_fun_overload_arg_type_1.rulp");
		_test("(out-to-file \"result/runtime/XRFactorDefunTest/test_fun_3_overload_arg_type_1_frame_1.txt\" (print-subject))");

	}

	@Test
	void test_fun_3_overload_arg_type_2() {

		_setup();

		_test("(defun fun1 ((?v1 int) ?v2) (return (+ ?v1 ?v2)))", "fun1");
		_test("(name-of fun1)", "\"(fun1 int nil)\"");
		_test("(defun fun1 (?v1 ?v2) (return (- ?v1 ?v2)))", "fun1");
		_test("(name-of fun1)", "\"'((fun1 int nil) (fun1 nil nil))\"");
		_test("(fun1 2 3)", "5");
		_test("(fun1 2 3.1)", "5.1");
		_test("(fun1 2.1 3.1)", "-1.0");
		_test("(out-to-file \"result/runtime/XRFactorDefunTest/test_fun_3_overload_arg_type_2_frame_1.txt\" (print-subject))");
	}

	@Test
	void test_fun_3_overload_cross_frame_1_override() {
		_setup();
		_test_script("result/runtime/XRFactorDefunTest/test_fun_3_overload_cross_frame_1_override.rulp");
	}

	@Test
	void test_fun_3_overload_cross_frame_1_override2() {
		_setup();
		_test_script("result/runtime/XRFactorDefunTest/test_fun_3_overload_cross_frame_1_override2.rulp");
	}

	@Test
	void test_fun_3_overload_cross_frame_2() {
		_setup();
		_test_script("result/runtime/XRFactorDefunTest/test_fun_3_overload_cross_frame_2.rulp");
	}

	@Test
	void test_fun_3_overload_null_arg() {

		_setup();

		try {
			RulpUtil.addFactor(_getInterpreter().getMainFrame(), "get-null", (_args, _interpreter, _frame) -> {
				return null;
			});
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		_test("(defun fun1 (?v1 ?v2)  (return ?v2))", "fun1");
		_test("(name-of fun1)", "\"(fun1 nil nil)\"");
		_test("(defun fun1 ((?v1 int) (?v2 int)) (return (+ 1 ?v2)))", "fun1");
		_test("(name-of fun1)", "\"'((fun1 int int) (fun1 nil nil))\"");
		_test("(defun fun1 ((?v1 int) ?v2) (return (- 1 ?v2)))", "fun1");
		_test("(name-of fun1)", "\"'((fun1 int int) (fun1 int nil) (fun1 nil nil))\"");
		_test("(fun1 a b)", "b");
		_test("(fun1 1 3)", "4");
		_test("(fun1 (get-null) 3)", "4");
		_test("(out-to-file \"result/runtime/XRFactorDefunTest/test_fun_3_overload_null_arg_frame_1.txt\" (print-subject))");
	}

	@Test
	void test_fun_4_scope_a() {

		_setup();

		_test("(defun fun1 () (setq x (+ x 1)))", "fun1");
		_test("(name-of fun1)", "\"(fun1)\"");
		_test("(defvar x 10) x", "&x 10");
		_test("(fun1)");
		_test("x", "11");
	}

	@Test
	void test_fun_4_scope_b() {

		_setup();
		_test_script("result/runtime/XRFactorDefunTest/test_fun_4_scope_b.rulp");
	}

	@Test
	void test_fun_4_scope_c() {

		_setup();
		_test_script("result/runtime/XRFactorDefunTest/test_fun_4_scope_c.rulp");
	}

	@Test
	void test_fun_4_scope_d() {

		_setup();
		_test_script("result/runtime/XRFactorDefunTest/test_fun_4_scope_d.rulp");
	}

	@Test
	void test_fun_5_pass_expr() {

		_setup();

		_test("(defvar x 10) x", "&x 10");

		_test("(defun fun1 (?x) (print ?x))", "fun1");
		_test("(name-of fun1)", "\"(fun1 nil)\"");
		_test("(fun1 1)", "nil", "1"); // pass constant
		_test("(fun1 x)", "nil", "10"); // pass var
		_test("(fun1 &x)", "nil", "10"); // pass var ref
		_test("(fun1 (+ 1 2))", "nil", "3"); // pass expression
		_test("(fun1 $(+ 1 2))", "nil", "3"); // pass early expression

		_test("(defun fun2 ((?x expr)) (print ?x))", "fun2");
		_test("(name-of fun2)", "\"(fun2 expr)\"");
		_test("(fun2 (+ 1 2))", "nil", "(+ 1 2)"); // pass expression

		// pass early expression
		_test_error("(fun2 $(+ 1 2))",
				"the type<int> of 0 argument<3> not match <expr>\n" + "at main: (fun2 $(+ 1 2))");

	}

	@Test
	void test_fun_6_extend_body() {
		_setup();
		_test_script("result/runtime/XRFactorDefunTest/test_fun_6_extend_body.rulp");
	}
}
