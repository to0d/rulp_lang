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
		_run_script();
	}
}
