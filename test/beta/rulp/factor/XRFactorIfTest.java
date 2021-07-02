package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorIfTest extends RulpTestBase {

	@Test
	void test1() {
		_setup();
		_test("(if (> 2 1) 100 101)", "100");
		_test("(if (> 1 2) 100 101)", "101");
	}

	@Test
	void test2() {
		_setup();
		_test("(defvar v1 1) v1", "&v1 1");
		_test("(defvar v2 2) v2", "&v2 2");
		_test("(if (> 2 1) (do (setq v1 (+ v1 1)) (setq v2 (+ v2 2)))) v1 v2", "&v2 2 4");
	}

}
