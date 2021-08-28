package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorValueOfTest extends RulpTestBase {

	@Test
	void test_1() {

		_setup();
		_test("(value-of nil)", "nil");
		_test("(value-of true)", "true");
		_test("(value-of 1)", "1");
		_test("(value-of 1.1)", "1.1");
		_test("(value-of '(a b))", "'(a b)");
		_test("(value-of print-list)", "print-list");
		_test("(value-of \"abc\")", "\"abc\"");
		_test("(defvar v1 10)");
		_test("(value-of v1)", "10");
		_test_error("(value-of a)", "object not found: a\n" + "at main: (value-of a)");
	}

	@Test
	void test_2() {

		_setup();
		_test("(defvar v1 10)");
		_test("(foreach (?x (ls main)) (value-of ?x))", "'(main::main main::root main::system main::v1)");
		_test("(foreach (?x (ls main)) (type-of $(value-of ?x)))", "'(member member member member)");

		_test("(foreach (?x (ls main)) (value-of (value-of ?x)))", "'(main root system &v1)");
		_test("(foreach (?x (ls main)) (type-of $(value-of (value-of ?x))))", "'(frame frame frame var)");

		_test("(foreach (?x (ls main)) (value-of (value-of (value-of ?x))))", "'(main root system 10)");
		_test("(foreach (?x (ls main)) (type-of $(value-of (value-of (value-of ?x)))))", "'(frame frame frame int)");

		_test("(foreach (?x (ls main)) (value-of (value-of (value-of (value-of ?x)))))", "'(main root system 10)");
		_test("(foreach (?x (ls main)) (type-of $(value-of (value-of (value-of (value-of ?x))))))",
				"'(frame frame frame int)");
	}

	@Test
	public void test_3() {

		_setup();
		_test_script("result/factor/XRFactorValueOfTest/test_3.rulp");
	}
}
