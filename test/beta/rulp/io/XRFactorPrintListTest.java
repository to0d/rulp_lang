package beta.rulp.io;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorPrintListTest extends RulpTestBase {

	@Test
	void test() {
		_test("(print-list '(a b c))", "nil", "a\nb\nc\n");
	}

}
