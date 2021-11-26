package beta.rulp.blob;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorMakeBlobTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defvar ?b (make-blob 3))", "&?b");
		_test("?b", "[000000]");
		_test("(length-of ?b)", "3");
	}
}