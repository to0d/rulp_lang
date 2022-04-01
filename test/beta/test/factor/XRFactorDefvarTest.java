package beta.test.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDefvarTest extends RulpTestBase {

	@Test
	void test_defvar_1() {

		_setup();
		_run_script();
	}

	@Test
	void test_defvar_2_redefine() {

		_setup();
		_run_script();
	}

	@Test
	void test_defvar_3_override() {

		_setup();
		_run_script();
	}
}
