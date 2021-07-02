package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestSet extends RulpTestBase {

	@Test
	void test_set_1() {

		_setup();

		_test("(new set set1)", "set1");
		_test("(name-of set1)", "\"set1\"");

		_test("(set1::size-of)", "0");

		_test("(set1::add 1)", "nil");
		_test("(set1::size-of)", "1");
		_test("(set1::has 1)", "true");
		_test("(set1::has 2)", "false");

		_test("(set1::add a)", "nil");
		_test("(set1::size-of)", "2");
		_test("(set1::has a)", "true");
		_test("(set1::has b)", "false");

		_test("(set1::add \"str1\")", "nil");
		_test("(set1::size-of)", "3");
		_test("(set1::has \"str1\")", "true");
		_test("(set1::has \"str2\")", "false");

		_test("(set1::add '(a))", "nil");
		_test("(set1::size-of)", "4");
		_test("(set1::has '(a))", "true");
		_test("(set1::has '(b))", "false");

		_gInfo("result/collection/TestSet/test_set_1_ginfo.txt");
	}

	@Test
	void test_set_2() {

		_setup();

		_test("(defmacro macro_add (?set ?v) (?set::add ?v))");
		_test("(new set set1)");
		_test("(macro_add set1 1)", "nil");
		_test("(set1::size-of)", "1");
		_test("(set1::has 1)", "true");
	}

	@Test
	void test_set_3() {

		_setup();

		_test("(defun fun_add (?set ?v) (?set::add ?v))");
		_test("(new set set1)");
		_test("(fun_add set1 1)", "nil");
		_test("(set1::size-of)", "1");
		_test("(set1::has 1)", "true");
	}

	@Test
	void test_set_4() {

		_setup();

		_test("(new set setx)", "setx");
		_test("(setx::add 2)", "nil");

		_test("(new set set1)", "set1");
		_test("(set1::size-of)", "0");

		_test("(set1::add 1)", "nil");
		_test("(set1::size-of)", "1");
		_test("(set1::has 1)", "true");
		_test("(set1::has 2)", "false");
	}

	@Test
	void test_set_5() {

		_setup();
		_test("(ls set)",
				"'(set::?impl set::_set_add set::_set_has set::_set_init set::_set_size_of set::add set::has set::init set::size-of)");

		_test("(new set set1)", "set1");
		_test("(ls set1)", "'(set1::?impl set1::init set1::this)");

		_test("(set1::add 1)", "nil");
		_test("(ls set1)", "'(set1::?impl set1::add set1::init set1::this)");

		_test("(ls-print set)", "nil", _load("result/collection/TestSet/set_ls_print.txt") + "\n");
	}

	@Test
	void test_set_6_final() {

		_setup();
		_test_script("result/collection/TestSet/test_set_6_final_set.rulp");
	}

	@Test
	void test_set_7_property() {

		_setup();
		_test("(property-of set)", "'(final)");
	}

	@Test
	void test_set_8_ref_a() {

		_setup();
		_test_script("result/collection/TestSet/test_set_8_ref_a.rulp");
		_test("?x", "0");
		_test("(rulp-object-count frame)", "5");
		_test("(rulp-object-count instance)", "3");
		_gInfo("result/collection/TestSet/test_set_8_ref_a_ginfo_1.txt");

		_test("(delete s1)", "nil", "");
		_test("?x", "0");
		_test("(rulp-object-count frame)", "5");
		_test("(rulp-object-count instance)", "3");
		_gInfo("result/collection/TestSet/test_set_8_ref_a_ginfo_2.txt");
	}

}
