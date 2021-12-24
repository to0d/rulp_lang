package beta.rulp.optimize;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeCC1 extends RulpTestBase {

	@Test
	public void test_cc1_1_stable_fun() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cc1_2_dup_fun() {

		_setup();
		_run_script();
	}
}
