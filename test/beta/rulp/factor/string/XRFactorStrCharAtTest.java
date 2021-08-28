package beta.rulp.factor.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrCharAtTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-char-at \"abc5\" 0)", "97");
	}
}