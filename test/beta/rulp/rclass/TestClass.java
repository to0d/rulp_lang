package beta.rulp.rclass;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestClass extends RulpTestBase {

	@Test
	public void test_class_1() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_class_2_def_outside() {

		_setup();
		_run_script();
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
	public void test_class_7_overload_4_same_name_fun() {

		_setup();
		_run_script();
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
