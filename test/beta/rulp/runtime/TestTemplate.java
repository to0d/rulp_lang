package beta.rulp.runtime;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class TestTemplate extends RulpTestBase {

	@Test
	public void test_1() {

		_setup();

		try {
			RulpUtil.addTemplate(_getInterpreter().getMainFrame(), "f1", (_args, _interpreter, _frame) -> {
				return RulpFactory.createAtom("A" + _args.get(2).asString());
			}, 3, "a");

			RulpUtil.addTemplate(_getInterpreter().getMainFrame(), "f1", (_args, _interpreter, _frame) -> {
				return RulpFactory.createAtom("B" + _args.get(2).asString());
			}, 3, "b");

		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		_test("(f1 a 3)", "A3");
		_test("(f1 b 3)", "B3");

		_gInfo("result/runtime/TestTemplate/test_1_ginfo.txt");
	}
}
