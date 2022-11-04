package beta.test.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.StringUtil;
import alpha.rulp.utils.TimeUtil;

class TimeUtilTest extends RulpTestBase {

	@Test
	void test_formatStandardTime() {

		_setup();

		String path = "src\\alpha\\rulp\\utils\\TimeUtil.java";
		assertTrue(FileUtil.isExistFile(path));

		long time = FileUtil.getLastModifiedTime(path);
		assertTrue(time > 0);

		String out = TimeUtil.formatStandardTime(time);

		// Check output format: yyyy-MM-dd HH:mm:ss
		assertTrue(StringUtil.matchFormat("%d-%d-%d %d:%d:%d", out), out);
	}

}
