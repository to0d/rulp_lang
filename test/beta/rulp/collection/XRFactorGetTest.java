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
		_test_script("result/collection/XRFactorGetTest/test_get_array_1_1.rulp");
	}

	@Test
	void test_get_array_2_1() {

		_setup();
		
		_test("(get {{a, b, c}, x, y} 0 2)", "c");
		_test("(get {{a, b, c}, x, y} 1 2)", "nil");

		_test_error("(get {{a, b, c}, x, y} -1 0)", "invalid");
		_test_error("(get {{a, b, c}, x, y} 3 0)", "invalid");
		_test_error("(get {{a, b, c}, x, y} 0 -1)", "invalid");
		_test_error("(get {{a, b, c}, x, y} 0 3)", "invalid");
	}
}
