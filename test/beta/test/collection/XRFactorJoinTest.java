package beta.test.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorJoinTest extends RulpTestBase {

	@Test
	void test_join_1_atom_list() {
		_setup();
		_run_script();
	}

	@Test
	void test_join_2_list_list() {
		_setup();
		_run_script();
	}

}
