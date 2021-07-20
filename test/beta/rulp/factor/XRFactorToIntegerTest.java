package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorToIntegerTest extends RulpTestBase {

	@Test
	void test() {
		_test("(to-int 123)", "123");
		_test("(to-int \"123\")", "123");
		_test("(to-int 1.1)", "1");
	}
}
