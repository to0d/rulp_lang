package beta.rulp.error;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorErrorTest extends RulpTestBase {

	@Test
	void test_1() {
		_setup();
		_test("(try (if (> 2 1) (error e1 \"msg1\") (error e2 \"msg2\"))" + " (e1 (print e1)) (e2 (print e2)))", "nil",
				"error: e1, msg1");
	}

	@Test
	void test_default_handle_case() {

		_setup();
		_test("(try (if (> 2 3) (error e1 \"msg1\") (error e2 \"msg2\")) (?e (print ?e)))", "nil", "error: e2, msg2");
	}

	@Test
	void test_unhandle_case_1() {
		_setup();
		_test_script("result/error/XRFactorErrorTest/test_unhandle_case_1.rulp");
	}
}
