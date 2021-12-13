package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorArithmeticTest extends RulpTestBase {

	@Test
	void test_1_add() {
		_setup();
		_test("(+ 1 2 3)", "6");
		_test("(+ 1 (+ 2 3))", "6");
		_test("(+ 1 2)", "3");
		_test("(% 10 2)", "0");
	}

	@Test
	void test_2() {
		_setup();
		_test("(+ 1.0 1.0)", "2.0");
	}

	@Test
	void test_3() {
		_setup();
		_test("(+ 100L 10L)", "110L");
	}

	@Test
	void test_4() {
		_setup();
		_test("(+ 100D 10D)", "110.0D");
	}

	@Test
	void test_5() {
		_setup();
		_test("(^ 10 2)", "100");
		_test("(^ 10L 3)", "1000L");
		_test("(^ 1.1 2)", "1.21");
		_test("(^ 1.1D 2)", "1.2100000000000002D");
	}

}
