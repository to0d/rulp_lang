package beta.rulp.basic;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestObjectRef extends RulpTestBase {

	@Test
	public void test_ref_1_return_1() {
		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_ref_2_loop_1() {
		_setup();
		_run_script();
		_gInfo();
	}
}