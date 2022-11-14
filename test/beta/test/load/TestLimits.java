package beta.test.load;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestLimits extends RulpTestBase {

	@Test
	void test_limit_1_list() {
		_setup();
		_run_script();
	}

	@Test
	void test_limit_2_int_max_min() {
		_setup();
		_run_script();
	}

	@Test
	void test_limit_3_long_max_min() {
		_setup();
		_run_script();
	}
}
