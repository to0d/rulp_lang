package beta.test.load;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestLimits extends RulpTestBase {

	@Test
	void test_limit_list_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_limit_int_max_min_1() {
		_setup();
		_run_script();
	}

}
