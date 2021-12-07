package beta.rulp.rclass;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestClass extends RulpTestBase {

	@Test
	public void test_class_1() {

		_setup();

		_test("(load \"result/rclass/TestClass/test_rclass_1.rulp\")");

		_test("(ls class1)", "'(class1::?value class1::add class1::get class1::set class1::sub)");
		_test("(new class1 o1)", "o1");
		_test("(ls o1)", "'(o1::this)");
		_test("(o1::get)", "0");
		_test("(ls o1)", "'(o1::?rv o1::?value o1::get o1::this)");
		_test("(o1::set 5)", "nil");
		_test("(o1::get)", "5");
		_test("(o1::add 1)", "nil");
		_test("(o1::get)", "6");
		_test("(o1::sub 2)", "nil");
		_test("(o1::get)", "4");
		_test("(ls o1)", "'(o1::?rv o1::?value o1::add o1::get o1::set o1::sub o1::this)");
		_test("((:: o1 get))", "4");

		_test("(ls class1)", "'(class1::?value class1::add class1::get class1::set class1::sub)");
		_test("(ls o1)", "'(o1::?rv o1::?value o1::add o1::get o1::set o1::sub o1::this)");

		_test("(out-to-file \"result/rclass/TestClass/test_class_1_a.txt\" (print-subject o1 -1))");

		_gInfo("result/rclass/TestClass/test_class_1_ginfo_a.txt");

		_test("(type-of o1)", "instance");
		_test("(rulp-object-count frame)", "2");

		_test("(delete o1)");
		_test("(type-of o1)", "atom");
		_test("(rulp-object-count frame)", "1");

		_gInfo("result/rclass/TestClass/test_class_1_ginfo_b.txt");
	}

	@Test
	public void test_class_2_def_outside() {

		_setup();

		_test("(load \"result/rclass/TestClass/test_rclass_1.rulp\")");
		_test("(new class1 o1)", "o1");
		_test("(ls class1)", "'(class1::?value class1::add class1::get class1::set class1::sub)");
		_test("(ls o1)", "'(o1::this)");

		_test("(defvar var1 0)", "&var1");
		_test("(defvar o1::var1 1)", "&var1");
		_test("(ls class1)", "'(class1::?value class1::add class1::get class1::set class1::sub)");
		_test("(ls o1)", "'(o1::this o1::var1)");

		_test("(value-of var1)", "0");
		_test("(value-of (value-of o1::var1))", "1");
	}

	@Test
	public void test_class_3_access_control() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_4_final() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_5_static() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_6_super() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_7_overload_1() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_7_overload_2() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_7_overload_3_static_fun() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_class_8_init_1() {
		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_class_9_delete_1() {
		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_class_9_delete_2() {
		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_class_a_extend_body() {
		_setup();
		_run_script();
	}

	@Test
	public void test_class_b_bad_para() {
		_setup();
		_run_script();
	}

	@Test
	public void test_class_c_mbr() {
		_setup();
		_run_script();
		_gInfo();
	}

}
