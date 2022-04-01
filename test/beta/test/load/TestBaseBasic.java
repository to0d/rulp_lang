package beta.test.load;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestBaseBasic extends RulpTestBase {

	@Test
	void test_inc_dec_1() {
		_setup();
		_run_script();
	}

	@Test
	void test_inc_dec_2() {
		_setup();
		_run_script();
	}

}
