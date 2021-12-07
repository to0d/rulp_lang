package beta.rulp.namespace;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestNameSpace extends RulpTestBase {

	@Test
	public void test_namespace_1() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_namespace_2() {

		_setup();

		_test("(new namespace ns1)", "ns1");
		_test("(new namespace ns2)", "ns2");

		_test("(defvar ns1::var1 1)", "&var1");
		_test("(defun ns1::f1 () (return 1))", "ns1::f1");

		_test("(defvar ns2::var1 2)", "&var1");
		_test("(defun ns2::f1 () (return 2))", "ns2::f1");

		_test("(use namespace ns1)", "nil");
		_test("(value-of var1)", "1");
		_test("(f1)", "1");

		_test("(use namespace ns2)", "nil");
		_test("(value-of var1)", "2");
		_test("(f1)", "2");

		_test("(use namespace ns1)", "nil");
		_test("(value-of var1)", "1");
		_test("(f1)", "1");

		_gInfo();
	}

	@Test
	public void test_namespace_3() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_namespace_4() {

		_setup();
		_run_script();
	}

	@Test
	public void test_namespace_5_err_duplicate_ns() {

		_setup();
		_run_script();
	}

	@Test
	public void test_namespace_6_nested_1() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_namespace_6_nested_2() {

		_setup();
		_run_script();
		_gInfo();
	}
}
