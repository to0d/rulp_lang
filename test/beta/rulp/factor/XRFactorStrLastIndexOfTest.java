package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrLastIndexOfTest extends RulpTestBase {

	@Test
	void test() {
		_test("(str-last-index-of \"abc123a\" \"a\")", "6");
		_test("(str-last-index-of \"abc123a\" \"2\")", "4");
		_test("(str-last-index-of \"abc123a\" \"x\")", "-1");
	}
}