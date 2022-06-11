package beta.test.io;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInput;
import alpha.rulp.utils.RulpTestBase;

class XRFactorReadLineTest extends RulpTestBase {

	@Test
	void test_read_line_1() {

		_setup();

		ArrayList<String> lines = new ArrayList<>();
		lines.add("line1");
		lines.add("line2  xxx");
		lines.add("  line3");

		try {
			this._getInterpreter().setInput(new IRInput() {

				int idx = 0;

				@Override
				public String read() throws RException {

					if (idx >= lines.size()) {
						return "";
					}

					return lines.get(idx++);
				}
			});
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		_run_script();
	}
}
