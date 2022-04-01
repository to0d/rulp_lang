package beta.test.rclass;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorNewTest extends RulpTestBase {

	@Test
	public void test_new_1_err_duplicate_obj() {

		_setup();
		_run_script();

	}

	@Test
	public void test_new_2_no_instance_name() {

		_setup();
		_run_script();

	}

}
