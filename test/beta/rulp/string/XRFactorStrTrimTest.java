package beta.rulp.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrTrimTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-trim \"\")", "\"\"");
		_test("(str-trim \"abc\")", "\"abc\"");
		_test("(str-trim \"abc \")", "\"abc\"");
		_test("(str-trim \"abc  \")", "\"abc\"");
		_test("(str-trim \" abc \")", "\"abc\"");

		_test("(str-trim-head \"\")", "\"\"");
		_test("(str-trim-head \"abc\")", "\"abc\"");
		_test("(str-trim-head \"abc \")", "\"abc \"");
		_test("(str-trim-head \" abc \")", "\"abc \"");
		_test("(str-trim-head \"  abc\")", "\"abc\"");
		_test("(str-trim-head \" abc \")", "\"abc \"");

		_test("(str-trim-tail \"\")", "\"\"");
		_test("(str-trim-tail \"abc\")", "\"abc\"");
		_test("(str-trim-tail \"abc \")", "\"abc\"");
		_test("(str-trim-tail \" abc \")", "\" abc\"");
		_test("(str-trim-tail \"abc  \")", "\"abc\"");
		_test("(str-trim-tail \" abc \")", "\" abc\"");
	}
}
