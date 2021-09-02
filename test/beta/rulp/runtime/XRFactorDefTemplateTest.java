package beta.rulp.runtime;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDefTemplateTest extends RulpTestBase {

	@Test
	public void test_1() {

		_setup();
		_test_script("result/runtime/XRFactorDefTemplateTest/test_1.rulp");
		_gInfo("result/runtime/XRFactorDefTemplateTest/test_1_ginfo.txt");
	}

}
