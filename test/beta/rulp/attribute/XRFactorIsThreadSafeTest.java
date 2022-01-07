package beta.rulp.attribute;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorIsThreadSafeTest extends RulpTestBase {

	@Test
	public void test_is_thread_unsafe_0() {
		_setup();
		_run_script();
	}

}
