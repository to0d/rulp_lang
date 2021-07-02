package beta.rulp.rclass;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorNewTest extends RulpTestBase {

	@Test
	public void test_err_1_duplicate_obj() {
		_setup();
		_test_script("result/rclass/XRFactorNewTest/test_err_1_duplicate_obj.rulp");
	}

}
