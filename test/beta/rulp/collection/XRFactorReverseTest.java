package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorReverseTest extends RulpTestBase {

	@Test
	public void test1() {
		_setup();
		_test("(reverse '(1 2 3))", "'(3 2 1)");
		_test("(reverse '(a b c))", "'(c b a)");

	}

}
