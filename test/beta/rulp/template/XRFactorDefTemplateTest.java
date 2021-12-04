package beta.rulp.template;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDefTemplateTest extends RulpTestBase {

	@Test
	public void test_1() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_2_mbr() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_3_scope_a() {

		_setup();
		_run_script();
	}

	@Test
	public void test_4_ns() {

		_setup();
		_run_script();
		_gInfo();
	}

	@Test
	public void test_5_overwrite_1() {

		_setup();
		_run_script();
		_gInfo();
	}
}
