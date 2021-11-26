package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDefConstTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defconst x 10)", "x");
		_test("(+ x 1)", "11");
	}
}
