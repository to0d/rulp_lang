package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorToBlobTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(to-blob 1)", "[00000001]");
		_test("(to-blob 1L)", "[0000000000000001]");
		_test("(to-blob 1.0)", "[3F800000]");
		_test("(to-blob 1.0d)", "[3FF0000000000000]");
		_test("(to-blob \"abc\")", "[616263]");
	}
}