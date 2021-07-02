package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorValueTypeOfTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(defvar var1 10)(value-type-of var1)", "&var1 int");
	}

}
