package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrEndWithTest extends RulpTestBase {

	@Test
	void test() {
		_test("(str-end-with \"abc5\" \"bc5\")", "true");
	}
}
