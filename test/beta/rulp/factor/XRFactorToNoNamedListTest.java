package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorToNoNamedListTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(to-nonamed-list '(b c))", "'(b c)");
		_test("(to-nonamed-list n1:'(b c))", "'(b c)");
	}
}
