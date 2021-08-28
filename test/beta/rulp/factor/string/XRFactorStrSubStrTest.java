package beta.rulp.factor.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrSubStrTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-substr \"abc5\" 0)", "\"abc5\"");
		_test("(str-substr \"abc5\" 1)", "\"bc5\"");
		_test("(str-substr \"abc5\" 5)", "\"\"");
		_test("(str-substr \"abc5\" -1)", "\"abc5\"");
		_test("(str-substr \"abc5\" 0 5)", "\"abc5\"");
		_test("(str-substr \"abc5\" 2 4)", "\"c5\"");
		_test("(str-substr \"abc5\" 4 2)", "\"\"");
		_test("(str-substr \"abc5\" 3 3)", "\"\"");
	}
}
