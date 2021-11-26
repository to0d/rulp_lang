package beta.rulp.namespace;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestNameSpace extends RulpTestBase {

	@Test
	public void test_namespace_1() {

		_setup();
		_test("(new namespace ns1)", "ns1");
		_test("(defvar var1 1)", "&var1");
		_test("(defun f1 () (return 1))", "f1");
		_test("(search-frame-of)", "'()");

		// define variable in name space
		_test("(defvar ns1::var1 2)", "&var1");
		_test("(defvar ns1::var2 3)", "&var2");
		_test("(value-of var1)", "1");
		_test("(value-of (value-of main::var1))", "1");
		_test("(value-of (value-of ns1::var1))", "2");
		_test("(value-of (value-of ns1::var2))", "3");
		_test("(ls)", "'(main::f1 main::main main::ns1 main::root main::system main::var1)");
		_test("(ls ns1)", "'(ns1::this ns1::var1 ns1::var2)");
		_test("(has-member ns1 var1)", "true");

		// define function in name space
		_test("(defun ns1::f1 () (return 2))", "ns1::f1");
		_test("(defun ns1::f2 () (return 3))", "ns1::f2");
		_test("(f1)", "1");
		_test("(main::f1)", "1");
		_test("(ns1::f1)", "2");
		_test("(ns1::f2)", "3");
		_test("(ls)", "'(main::f1 main::main main::ns1 main::root main::system main::var1)");
		_test("(ls ns1)", "'(ns1::f1 ns1::f2 ns1::this ns1::var1 ns1::var2)");

		// Can't define same name item
		_test_error("(defvar ns1::var1 1)", "duplicate member variable: ns1::var1\n" + "at main: (defvar ns1::var1 1)");
		_test_error("(defun ns1::f1 () (return 2))",
				"duplicate funcion: (f1)\n" + "at main: (defun ns1::f1 () (return 2))");
		_test("(ls)", "'(main::f1 main::main main::ns1 main::root main::system main::var1)");
		_test("(ls ns1)", "'(ns1::f1 ns1::f2 ns1::this ns1::var1 ns1::var2)");

		// Can't find item
		// - if the using the name space is not specified
		_test_error("(value-of var2)", "object not found: var2\n" + "at main: (value-of var2)");
		_test_error("(f2)", "factor not found: (f2)\n" + "at main: (f2)");
		_test("(ls)", "'(main::f1 main::main main::ns1 main::root main::system main::var1)");
		_test("(ls ns1)", "'(ns1::f1 ns1::f2 ns1::this ns1::var1 ns1::var2)");

		// Search item in a name space
		_test("(use namespace ns1)");
		_test("(search-frame-of)", "'(SF-ns1-0 main)");
		_test("(value-of (value-of main::var1))", "1");
		_test("(value-of (value-of ns1::var1))", "2");
		_test("(value-of (value-of ns1::var2))", "3");
		_test("(main::f1)", "1");
		_test("(ns1::f1)", "2");
		_test("(ns1::f2)", "3");

		// Item will be only define in the current frame
		// - if the using the name space is not specified
		_test("(defvar var3 4)", "&var3");
		_test("(defun f3 () (return 4))", "f3");

		// the latest name space higher priority
		_test("(value-of var1)", "2");
		_test("(f1)", "2");

		// Can find items when specify the using the name space
		_test("(value-of var2)", "3");
		_test("(f2)", "3");

		// change back, everything should work fine
		_test("(use namespace main)");
		_test("(search-frame-of)", "'(main SF-ns1-0)");
		_test("(value-of var1)", "1");
		_test("(value-of (value-of main::var1))", "1");
		_test("(value-of (value-of ns1::var1))", "2");
		_test("(value-of (value-of ns1::var2))", "3");
		_test("(value-of var3)", "4");
		_test("(f1)", "1");
		_test("(main::f1)", "1");
		_test("(ns1::f1)", "2");
		_test("(ns1::f2)", "3");
		_test("(f3)", "4");

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
		_test_script();
		_gInfo();
	}

	@Test
	public void test_namespace_4() {

		_test_error("(use namespace ns1)",
				"error: bad-type, namespace not found: ns1\n"
						+ "at _$fun$_use: (error bad-type (strcat \"namespace not found: \" (to-string ?ns)))\n"
						+ "at main: (use namespace ns1)");
	}

	@Test
	public void test_namespace_5_err_duplicate_ns() {

		_setup();
		_test("(new namespace ns1)", "ns1");
		_test_error("(new namespace ns1)", "duplicate object<ns1> found: ns1\n" + "at main: (new namespace ns1)");
	}

	@Test
	public void test_namespace_6_nested_1() {

		_setup();
		_test_script();
		_gInfo();
	}

	@Test
	public void test_namespace_6_nested_2() {

		_setup();
		_test_script();
		_gInfo();
	}
}
