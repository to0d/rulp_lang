package beta.test.frame;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;

public class TestFrameLoader extends RulpTestBase {

	@Test
	void test_frame_loader_1() {

		_setup();

		try {
			this._getInterpreter().getMainFrame().setFrameLoader((name) -> {
				switch (name) {
				case "v1":
					IRVar var = RulpFactory.createVar(name);
					var.setValue(RulpFactory.createInteger(1));
					return var;
				}

				return null;
			});
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());

		}

		_run_script();

	}

}
