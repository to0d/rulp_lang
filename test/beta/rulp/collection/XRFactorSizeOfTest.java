package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorSizeOfTest extends RulpTestBase {

	@Test
	void test_size_of_list_1() {
		_setup();
		_test("(size-of '(a b c))", "3");
	}

	@Test
	void test_size_of_map_1() {
		_setup();

		_test("(new map map1)", "map1");
		_test("(map1::put 1 2)", "nil");
		_test("(map1::size-of)", "1");

		_test("(size-of map1)", "1");
	}

	@Test
	void test_size_of_set_1() {

		_setup();

		_test("(new set set1)", "set1");
		_test("(set1::add 1)", "nil");
		_test("(set1::size-of)", "1");
		_test("(size-of set1)", "1");
	}

	@Test
	void test_size_of_array_1() {

		_setup();

		_test("(size-of {a, b, c})", "3");
		_test("(size-of {a, b, c} 0)", "3");

	}

	@Test
	void test_size_of_array_2() {

		_setup();
		_run_script();
	}
}
