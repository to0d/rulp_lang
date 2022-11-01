package beta.test.optimize;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class TestOptimizeERO extends RulpTestBase {

	@Test
	void test_ero_1_num_expr_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_1_num_expr_2() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_2_str_expr_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_3_recursion_fun_1a() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_3_recursion_fun_1b() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_4_if_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_4_if_2() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_4_if_3_multi_return_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_4_if_3_multi_return_2() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_4_if_3_multi_return_3() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_5_case_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_6_do_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_2() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_3() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_4_multi_return_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_4_multi_return_2() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_5_empty_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_5_empty_2() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_6() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_7() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_7_loop_8() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_8_infinite_loop_detected_loop() {

		_setup();
		_run_script();
	}

	@Test
	void test_ero_8_infinite_loop_detected_recursion() {

		// should fail

		_setup();
		_run_script();
	}

	@Test
	void test_ero_9_lt_1_lt000105_map() {

		_setup();
		_run_script();
	}
}
