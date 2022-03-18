package beta.rulp.rclass;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.ximpl.runtime.XRInterpreter;

public class TestClass extends RulpTestBase {

	@Test
	public void test_class_1() {

		_setup();
		_run_script();
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
	public void test_class_6_super_1() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_6_super_2_inherit_init() {

		_setup();
		_run_script();
	}

	@Test
	public void test_class_6_super_3() {

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
	}
	
	@Test
	public void test_class_8_init_2() {
		_setup();
		_run_script();
	}

	@Test
	public void test_class_9_delete_1() {
		_setup();
		_run_script();
	}

	@Test
	public void test_class_9_delete_2() {
		_setup();
		_run_script();
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
	public void test_class_c_mbr_1() {
		_setup();
		_run_script();
	}
	
	@Test
	public void test_class_c_mbr_2_const_var() {
		_setup();
		XRInterpreter.TRACE = true;
		_run_script();
	}

	@Test
	public void test_class_d_same_name() {
		_setup();
		_run_script();
	}
	
	@Test
	public void test_class_e_polymorphism() {
		_setup();
		_run_script();
	}
	
	@Test
	public void test_class_f_recursion() {
		_setup();
		_run_script();
	}
	
	@Test
	public void test_class_g_default_para_1() {
		_setup();
		_run_script();
		_gInfo();
	}
}
