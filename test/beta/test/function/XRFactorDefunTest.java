package beta.test.function;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

class XRFactorDefunTest extends RulpTestBase {

	@Test
	void test_fun_0_bad_para_1_dup_para_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_0_bad_para_1_undfine_type_1() {
		_setup();
		_run_script();

	}

	@Test
	void test_fun_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_2_ref_1() {

		_setup();
		_run_script();

	}

	@Test
	void test_fun_2_ref_2() {

		_setup();
		_run_script();

	}

	@Test
	void test_fun_3_overload_arg_number() {

		_setup();
		_run_script();

	}

	@Test
	void test_fun_3_overload_arg_type_1() {

		_setup();
		_run_script();

	}

	@Test
	void test_fun_3_overload_arg_type_2() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_3_overload_cross_frame_1_override() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_3_overload_cross_frame_1_override2() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_3_overload_cross_frame_2() {
		_setup();
		_run_script();
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

		_run_script();
	}

	@Test
	void test_fun_4_scope_a() {

		_setup();
		_run_script();

	}

	@Test
	void test_fun_4_scope_b() {

		_setup();
		_run_script();
	}

	@Test
	void test_fun_4_scope_c() {

		_setup();
		_run_script();
	}

	@Test
	void test_fun_4_scope_d() {

		_setup();
		_run_script();
	}

	@Test
	void test_fun_5_pass_expr() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_6_extend_body() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_7_pass_nil() {
		_setup();
		_run_script();
	}

	@Test
	void test_fun_8_unmatch_atom_to_obj() {
		_setup();
		_run_script();
	}

}
