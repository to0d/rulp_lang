package beta.rulp.math;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorRandomTest extends RulpTestBase {

	@Test
	void test() {
		_test("(type-of $(random))", "double");
	}
}
