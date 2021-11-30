package beta.rulp.rclass;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class TestNativeClass extends RulpTestBase {

	static final String A_CLASS1 = "nclass1";

	static final String A_MBR_FUNC1 = "_fun1";

	static class TestClass1 {

		public static void init(IRInterpreter interpreter, IRFrame frame) throws RException {

			IRClass testClass = RulpUtil.asClass(frame.getEntry(A_CLASS1).getValue());

			RulpUtil.setMember(testClass, A_MBR_FUNC1, (args, _interpreter, _frame) -> {
				if (args.size() != 1) {
					throw new RException("Invalid parameters: " + args);
				}

				return RulpFactory.createInteger(1);
			}, RAccessType.PRIVATE);
		}
	}

	void _load_class1() {

		_test_script("result/rclass/TestNativeClass/native_class_1.rulp");
		try {
			IRInterpreter interpreter = _getInterpreter();
			TestClass1.init(interpreter, interpreter.getMainFrame());
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void test_native_class_1() {

		_setup();
		_load_class1();
		_test("(nclass1::fun1)", "1");
	}

}
