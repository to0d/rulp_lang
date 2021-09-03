package beta.rulp.string;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrReplaceTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(str-replace \"XX/YY/ZZ\" \"/\" \"_\")", "\"XX_YY_ZZ\"");
	}
}
