package beta.rulp.rclass;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorHasMbrTest extends RulpTestBase {

	@Test
	public void test_1() {

		_setup();
		_test("(defclass class1 () (defvar ?value 0))", "class1");
		_test("(new class1 o1)", "o1");
		_test("(has-member o1 ?value)", "false");
		_test("(+ (value-of (value-of o1::?value)) 0)", "0");
		_test("(has-member o1 ?value)", "true");
	}

}
