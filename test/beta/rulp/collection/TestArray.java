package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestArray extends RulpTestBase {

	@Test
	void test_array_1() {

		_setup();
		_test("(size-of {a,b})", "2");
		_test("(is-empty {a,b})", "false");
		_test("(to-list {a,b})", "'(a b)");
		_test("(to-array '(a b))", "{a,b}");
	}

	@Test
	public void test_array_2_add() {
		_setup();
		_test("(add {} a b)", "{a,b}");
		_test("(add {a,b} c)", "{a,b,c}");
		_test("(add {a,b} c d)", "{a,b,c,d}");
	}

	@Test
	public void test_array_3_seta() {
		_setup();
		_test("(seta {a, b} 1 x)", "{a,x}");
	}
	
	@Test
	public void test_array_4_const() {

		_setup();
		_run_script();
		_gInfo();
	}
}
