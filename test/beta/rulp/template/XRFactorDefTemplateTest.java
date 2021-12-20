package beta.rulp.template;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDefTemplateTest extends RulpTestBase {

	@Test
	public void test_deftemplate_1() {

		_setup();
		_run_script(); 
	}

	@Test
	public void test_deftemplate_2_mbr() {

		_setup();
		_run_script(); 
	}

	@Test
	public void test_deftemplate_3_scope_a() {

		_setup();
		_run_script();
	}

	@Test
	public void test_deftemplate_4_ns() {

		_setup();
		_run_script();
	}

	@Test
	public void test_deftemplate_5_overwrite_1() {

		_setup();
		_run_script(); 
	}
}
