package beta.rulp.thread;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorSleepTest extends RulpTestBase {

	@Test
	void test() {
		
		_setup();
		_test("(defvar t1 (sys-time))", "&t1");
		_test("(sleep 100)", "nil");
		_test("(defvar t2 (- (sys-time) t1))", "&t2");
		_test("(and (>= t2 100) (< t2 150))", "true");
	}

}
