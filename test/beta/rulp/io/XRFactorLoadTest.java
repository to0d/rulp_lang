package beta.rulp.io;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorLoadTest extends RulpTestBase {

	@Test
	void test_1() {

		_setup();
		_test("(load \"load/load1.rulp\")", "'(&x1)");
		_test("(ls)", "'(main::main main::root main::system main::x1)");

		_test("(load \"load/load1.rulp\")", "'()");
		_test("(ls)", "'(main::main main::root main::system main::x1)");
	}

	@Test
	void test_2_add_load_path() {

		_setup();

		_test_error("(load \"load2.rulp\")", "file not found: load2.rulp\n" + "at main: (load \"load2.rulp\")");
		_test("(print ?load-paths)", "nil", "'()");
		_test("(setq ?load-paths (add ?load-paths \"load/path2\"))", "&?load-paths");
		_test("(load \"load2.rulp\")", "'(&x2)");
	}

	@Test
	void test_3() {

		_setup();
		_test("(do (load \"load/load1.rulp\"))", "'(&x1)");
		_test("(ls)", "'(main::main main::root main::system main::x1)");
	}
}
