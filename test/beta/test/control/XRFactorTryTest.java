package beta.test.control;

import static alpha.rulp.lang.Constant.O_Nil;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

class XRFactorTryTest extends RulpTestBase {

	@Test
	void test_try_1_default() {
		_setup();
		_run_script();
	}

	@Test
	void test_try_2_user_define() {
		_setup();
		_run_script();
	}

	@Test
	void test_try_3_unhandle() {
		_setup();
		_run_script();
	}

	@Test
	void test_try_4_native_exception() {

		_setup();

		try {

			RulpUtil.addFrameObject(_getInterpreter().getMainFrame(), new AbsAtomFactorAdapter("bad_factor") {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
					return RulpUtil.throw_error(interpreter, frame, RulpFactory.createAtom("e1"), O_Nil, this);
				}

			});

		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		_run_script();
	}

	@Test
	void test_try_5_try_return() {
		_setup();
		_run_script();
	}
}
