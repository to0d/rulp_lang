package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDoWhenObjDeletedTest extends RulpTestBase {

	@Test
	void test_1() {

		_setup();
		_test_script("result/factor/XRFactorDoWhenObjDeletedTest/test_1.rulp");
		_gInfo("result/factor/XRFactorDoWhenObjDeletedTest/test_1_ginfo.txt");
	}

}
