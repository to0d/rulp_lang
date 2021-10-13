package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorGetTest extends RulpTestBase {

	@Test
	void test_get_list() {
		_setup();
		_test("(get '(a b c) 1)", "b");
	}

	@Test
	void test_get_array_1_1() {

		_setup();
		_test_script();
	}

	@Test
	void test_get_array_2_1() {

		_setup();
		_test_script();
	}
}
