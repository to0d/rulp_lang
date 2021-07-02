package beta.rulp.io;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorSaveTxtFileTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(save-txt-file \"result/io/XRFactorSaveTxtFileTest/save-text-file-1.txt\" '(\"XYZ\" \"abc\" \"123\"))");
	}

}
