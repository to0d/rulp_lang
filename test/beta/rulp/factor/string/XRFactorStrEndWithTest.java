package beta.rulp.factor.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrEndWithTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-end-with \"abc5\" \"bc5\")", "true");
	}
}
