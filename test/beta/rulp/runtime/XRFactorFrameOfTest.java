package beta.rulp.runtime;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorFrameOfTest extends RulpTestBase {

	@Test
	public void test_frame_of_1() {

		_setup();
		_test_script("result/runtime/XRFactorFrameOfTest/test_frame_of_1.rulp");
		_gInfo("result/runtime/XRFactorFrameOfTest/test_frame_of_1_ginfo.txt");
	}

}
