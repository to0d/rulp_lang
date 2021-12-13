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
		_test("(+ 1.0 1.0)", "2.0");
		_test("(+ 100L 10L)", "110L");
		_test("(+ 100D 10D)", "110.0D");
	}

	@Test
	void test_2_sub() {
		_setup();
		_test("(- 2 1)", "1");
		_test("(- 9.0 0.5)", "8.5");
		_test("(- 100L 1L)", "99L");
		_test("(- 100D 10D)", "90.0D");
	}

	@Test
	void test_3_by() {
		_setup();
		_test("(* 2 1)", "2");
		_test("(* 9.0 0.5)", "4.5");
		_test("(* 100L 1L)", "100L");
		_test("(* 100D 10D)", "1000.0D");
	}

	@Test
	void test_4_div() {
		_setup();
		_test("(/ 2 1)", "2");
		_test("(/ 9.0 0.5)", "18.0");
		_test("(/ 100L 1L)", "100L");
		_test("(/ 100D 10D)", "10.0D");
	}

	@Test
	void test_5_mod() {
		_setup();
		_test("(% 10 3)", "1");
		_test("(% 9.0 2.0)", "1.0");
		_test("(% 100L 33L)", "1L");
		_test("(% 100D 3D)", "1.0D");
	}

	@Test
	void test_6_power() {
		_setup();
		_test("(power 10 2)", "100");
		_test("(power 10L 3)", "1000L");
		_test("(power 1.1 2)", "1.21");
		_test("(power 1.1D 2)", "1.2100000000000002D");
	}

	@Test
	void test_7_and() {
		_setup();
		_test("(& 10 3)", "2");
		_test("(& 100L 33L)", "32L");
		_test("(& 0xF0 0x0F)", "0");
	}

	@Test
	void test_8_or() {
		_setup();
		_test("(| 10 3)", "11");
		_test("(| 100L 33L)", "101L");
		_test("(| 0xF0 0x0F)", "255");
	}

	@Test
	void test_9_xor() {
		_setup();
		_test("(^ 10 3)", "9");
		_test("(^ 100L 33L)", "69L");
		_test("(^ 0xF0 0x0F)", "255");
	}

	@Test
	void test_a_not() {
		_setup();
		_test("(~ 10)", "-11");
		_test("(~ 100L)", "-101L");
		_test("(~ 0xF0)", "-241");
	}
}
