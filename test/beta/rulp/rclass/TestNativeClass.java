package beta.rulp.rclass;

import static alpha.rulp.lang.Constant.P_STATIC;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class TestNativeClass extends RulpTestBase {

	static final String A_CLASS1 = "nclass1";

	static class TestClass1 {
	}

	void _load_native_class_1(RAccessType accessType, int property) {

		_setup();

		_run_script("result/rclass/TestNativeClass/native_class_1.rulp");
		try {

			IRInterpreter interpreter = _getInterpreter();
			IRClass testClass = RulpUtil.asClass(interpreter.getMainFrame().getEntry(A_CLASS1).getValue());
			RulpUtil.setMember(testClass, "_fun1", (args, _interpreter, _frame) -> {
				if (args.size() != 1) {
					throw new RException("Invalid parameters: " + args);
				}
				return RulpFactory.createInteger(1);
			}, accessType, property);

		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void test_native_class_1() {

		_setup();

		// private, non-static function
		_load_native_class_1(RAccessType.PRIVATE, 0);

		// should not access from class static public function??? @TODO
		_test("(nclass1::static_fun1)", "1");
	}

	@Test
	public void test_native_class_2() {

		_setup();

		// private, static function
		_load_native_class_1(RAccessType.PRIVATE, P_STATIC);

		// should can access from class static public function
		_test("(nclass1::static_fun1)", "1");
	}

}
