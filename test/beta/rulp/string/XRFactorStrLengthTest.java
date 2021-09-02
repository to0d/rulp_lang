package beta.rulp.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrLengthTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-length \"abc5\")", "4");
		_test("(length-of \"abc5\")", "4");
	}
}
