package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrCatTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(strcat)", "\"\"");
		_test("(strcat \"abc\")", "\"abc\"");
		_test("(strcat \"abc\" \"123\")", "\"abc123\"");
		_test("(strcat \"abc\" \"123\" \"def\")", "\"abc123def\"");

		_test("(strcat \"abc\" \" 123\")", "\"abc 123\"");
		_test("(strcat \"abc\" (to-string 123))", "\"abc123\"");
		_test("(strcat \"abc\" \"-\" (date \"2020-01-01\"))", "\"abc-2020-01-01\"");
	}

}