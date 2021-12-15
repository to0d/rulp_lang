package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorWriteBlobTest extends RulpTestBase {

	@Test
	void test1() {

		_setup();
		_test("(defvar ?b1 (make-blob 4))", "&?b1");
		_test("?b1", "[00000000]");
		_test("(write-blob ?b1 0 (to-blob 1) 2 2) ", "2");
		_test("?b1", "[00010000]");
	}
}