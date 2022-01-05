package beta.rulp.basic;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestToolRulp extends RulpTestBase {

	@Test
	void test_tool_1_ls_print_usr_vars() {
		_setup();
		_run_script();
	}

	@Test
	void test_tool_1_ls_print_frame() {
		_setup();
		_run_script();
	}
}
