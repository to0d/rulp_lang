package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorAddAllTest extends RulpTestBase {

	@Test
	public void test1() {
		_setup();
		_test("(add-all '() '(a b))", "'(a b)");
		_test("(add-all '(a b) '(c))", "'(a b c)");
	}

}

