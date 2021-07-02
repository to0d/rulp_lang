package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorComparisonTest extends RulpTestBase {

	@Test
	void test() {
		_test("(> 1 2)", "false");
		_test("(< 1 1.5)", "true");
		_test("(> 1 2) (< 1 1.5)", "false true");
	}

}
