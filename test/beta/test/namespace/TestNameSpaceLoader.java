package beta.test.namespace;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class TestNameSpaceLoader extends RulpTestBase {

	static class NS1Loader implements IRObjectLoader {

		@Override
		public void load(IRInterpreter interpreter, IRFrame frame) throws RException, IOException {
			interpreter.compute("(defvar ns1::var1 1)");
		}

	}

	@Test
	public void test_ns_loader_1() {

		_setup();

		try {
			RulpUtil.registerNameSpaceLoader(_getInterpreter(), _getInterpreter().getMainFrame(), "ns1",
					new NS1Loader());
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		_gInfo("result/namespace/TestNameSpaceLoader/test_ns_loader_1_ginfo_1.txt");
		_test("(value-of (value-of ns1::var1))", "1");
		_gInfo("result/namespace/TestNameSpaceLoader/test_ns_loader_1_ginfo_2.txt");
	}

}
