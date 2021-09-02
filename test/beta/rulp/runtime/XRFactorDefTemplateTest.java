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

	@Test
	public void test_2_mbr() {

		_setup();
		_test_script("result/runtime/XRFactorDefTemplateTest/test_2_mbr.rulp");
		_gInfo("result/runtime/XRFactorDefTemplateTest/test_2_mbr_ginfo.txt");
	}
	
	@Test
	public void test_3_scope_a() {

		_setup();
		_test_script("result/runtime/XRFactorDefTemplateTest/test_3_scope_a.rulp");
	}
}
