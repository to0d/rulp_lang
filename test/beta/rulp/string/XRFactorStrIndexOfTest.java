package beta.rulp.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrIndexOfTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-index-of \"abc123a\" \"a\")", "0");
		_test("(str-index-of \"abc123a\" \"2\")", "4");
		_test("(str-index-of \"abc123a\" \"x\")", "-1");
	}
}
