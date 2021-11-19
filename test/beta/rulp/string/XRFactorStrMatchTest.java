package beta.rulp.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrMatchTest extends RulpTestBase {

	@Test
	public void test_str_match_1() {

		_setup();
		_test("(str-match \"*-*\" \"abc-xyz\")", "true");
		_test("(str-match \"*-*\" \"abc\")", "false");
		_test("(str-match \"* *\" \"abc xyz\")", "true");
	}

	@Test
	public void test_str_match_2() {

		_setup();
		_test("(defvar v1)");
		_test("(defvar v2)");
		_test("(str-match \"%?-%?\" \"abc-xyz\" '(&v1 (ref v2)))", "true");
		_test("v1", "\"abc\"");
		_test("v2", "\"xyz\"");
	}

	@Test
	public void test_str_match_3() {

		_setup();
		_test("(defvar v1)");
		_test("(defvar v2)");
		_test("(str-match \"%?-%?\" \"abc-xyz\" '(v1 v2))", "true");
		_test("v1", "\"abc\"");
		_test("v2", "\"xyz\"");
	}
}
