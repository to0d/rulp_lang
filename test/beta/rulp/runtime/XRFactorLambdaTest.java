package beta.rulp.runtime;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorLambdaTest extends RulpTestBase {

	@Test
	void test_lambda_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_lambda_2() {
		_setup();
		_run_script();
	}

	@Test
	void test_lambda_3() {

		_setup();
		_run_script();
	}

	@Test
	void test_lambda_4() {

		_setup();
		_run_script();
	}

	@Test
	void test_5() {

		_setup();
		_run_script();
		_gInfo("result/runtime/XRFactorLambdaTest/test_5_ginfo.txt");
		_test("(setq ?f nil)", "&?f");
		_gInfo("result/runtime/XRFactorLambdaTest/test_5_ginfo_b.txt");
	}

	@Test
	void test_6_extend_body() {
		_setup();
		_run_script();
	}

}
