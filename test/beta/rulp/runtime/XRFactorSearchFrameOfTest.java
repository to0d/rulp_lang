package beta.rulp.runtime;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorSearchFrameOfTest extends RulpTestBase {

	@Test
	public void test_search_frame_of_1() {

		_setup();
		_test_script("result/runtime/XRFactorSearchFrameOfTest/test_search_frame_of_1.rulp");
		_gInfo("result/runtime/XRFactorSearchFrameOfTest/test_search_frame_of_1_ginfo.txt");
	}

}
