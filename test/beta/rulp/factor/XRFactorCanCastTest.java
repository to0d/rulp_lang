package beta.rulp.factor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorCanCastTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		_test("(can-cast int true)", "false");
		_test("(can-cast int 123)", "true");
		_test("(can-cast int \"abc\")", "false");
		_test("(can-cast int \"123\")", "true");
	}

}
