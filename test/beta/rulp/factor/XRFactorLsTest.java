package beta.rulp.factor;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class XRFactorLsTest extends RulpTestBase {

	@Test
	public void test_0() {

		_setup();

		_test("(ls)", "'(main::main main::root main::system)");
		_test("(ls local)", "'(main::local main::main main::root main::system)");
		_test("(ls main)", "'(main::local main::main main::root main::system)");

		_test("(value-of main::parent)", "system");
		_test("(value-of system::parent)", "root");

		_test("(out-to-file \"result/factor/XRFactorLsTest/frame_root.txt\" (print-subject root))");
		_test("(out-to-file \"result/factor/XRFactorLsTest/frame_system.txt\" (print-subject system))");
		_test("(out-to-file \"result/factor/XRFactorLsTest/frame_main.txt\" (print-subject main))");
	}

	@Test
	public void test_1_main() {

		_setup();
		_test("(print-list (ls root))", "nil", _load("result/factor/XRFactorLsTest/root_ls.txt") + "\n");
		_test("(print-list (ls system))", "nil", _load("result/factor/XRFactorLsTest/system_ls.txt") + "\n");
		_test_match("(ls-print root)", "nil", "result/factor/XRFactorLsTest/root_ls_print.txt");
		_test_match("(ls-print system)", "nil", "result/factor/XRFactorLsTest/system_ls_print.txt");
	}

	@Test
	public void test_2_fun() {

		_setup();
		_test("(defun ls1 () (return (ls)))");
		_test("(defun ls2 () (return (ls local)))");
		_test("(defun ls3 (?v1) (return (ls)))");
		_test("(defun ls4 (?v2) (return (ls local)))");
		_test("(defun ls5 () (print-list (ls root)))");
		_test("(defun ls6 () (print-list (ls system)))");
		_test("(defun ls7 () (print-list (ls main)))");
		_test("(ls1)", "'()");
		_test("(ls2)", "'(_$fun$_ls2::local)");
		_test("(ls3 1)", "'(_$fun$_ls3::?v1)");
		_test("(ls4 2)", "'(_$fun$_ls4::?v2 _$fun$_ls4::local)");
		_test("(ls5)", "nil", _load("result/factor/XRFactorLsTest/root_ls.txt") + "\n");
		_test("(ls6)", "nil", _load("result/factor/XRFactorLsTest/system_ls.txt") + "\n");
		_test("(ls7)", "nil", _load("result/factor/XRFactorLsTest/main_ls.txt") + "\n");

	}

	@Test
	public void test_3_env_vars() {

		_setup();
		_test_script();
	}

	@Test
	public void test_4_usr_vars() {

		_setup();
		_test("(defvar x 10)", "&x");
		_test("(defvar y)", "&y");
		_test("(ls local)", "'(main::local main::main main::root main::system main::x main::y)");
		_test("(ls-print local var)", "nil", _load("result/factor/XRFactorLsTest/local_ls_print_1.txt") + "\n");
	}
}
