package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrCharAtTest extends RulpTestBase {

	@Test
	void test() {
		_test("(str-char-at \"abc5\" 0)", "97");
	}
}