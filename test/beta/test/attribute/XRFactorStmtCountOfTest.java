package beta.test.attribute;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStmtCountOfTest extends RulpTestBase {

	@Test
	void test_stmt_count_of_0_attr() {
		_setup();
		_run_script();
	}

	@Test
	void test_stmt_count_of_0_func() {
		_setup();
		_run_script();
	}

	@Test
	void test_stmt_count_of_1_expr() {
		_setup();
		_run_script();
	}

	@Test
	void test_stmt_count_of_1_fun() {
		_setup();
		_run_script();
	}
}
