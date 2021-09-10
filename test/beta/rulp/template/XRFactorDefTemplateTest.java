package beta.rulp.template;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDefTemplateTest extends RulpTestBase {

	@Test
	public void test_1() {

		_setup();
		_test_script("result/template/XRFactorDefTemplateTest/test_1.rulp");
		_gInfo("result/template/XRFactorDefTemplateTest/test_1_ginfo.txt");
	}

	@Test
	public void test_2_mbr() {

		_setup();
		_test_script("result/template/XRFactorDefTemplateTest/test_2_mbr.rulp");
		_gInfo("result/template/XRFactorDefTemplateTest/test_2_mbr_ginfo.txt");
	}

	@Test
	public void test_3_scope_a() {

		_setup();
		_test_script("result/template/XRFactorDefTemplateTest/test_3_scope_a.rulp");
	}

	@Test
	public void test_4_ns() {

		_setup();
		_test_script("result/template/XRFactorDefTemplateTest/test_4_ns.rulp");
		_gInfo("result/template/XRFactorDefTemplateTest/test_4_ns_ginfo.txt");
	}

	@Test
	public void test_5_overwrite_1() {

		_setup();
		_test_script("result/template/XRFactorDefTemplateTest/test_5_overwrite_1.rulp");
		_gInfo("result/template/XRFactorDefTemplateTest/test_5_overwrite_1_ginfo.txt");
	}
}
