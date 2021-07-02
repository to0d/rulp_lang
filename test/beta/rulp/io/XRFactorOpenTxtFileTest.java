package beta.rulp.io;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorOpenTxtFileTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(open-txt-file \"result/io/XRFactorOpenTxtFileTest/open-text-file-1.txt\")",
				"'(\"123\" \"abc\" \"XYZ\")");
	}

}
