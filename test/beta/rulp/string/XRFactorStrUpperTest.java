package beta.rulp.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrUpperTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-upper \"abc\")", "\"ABC\"");
	}

}
