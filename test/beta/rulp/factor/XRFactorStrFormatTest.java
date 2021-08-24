package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrFormatTest extends RulpTestBase {

	@Test
	void test_1() {
		_setup();
		_test("(str-format \"%d\" 1)", "\"1\"");
		_test("(str-format \"%b\" true)", "\"true\"");
		_test("(str-format \"%1.2f\" 1.1111)", "\"1.11\"");
		_test("(str-format \"%d\" 1L)", "\"1\"");
		_test("(str-format \"%1.2f\" 1.123D)", "\"1.12\"");
	}

}
