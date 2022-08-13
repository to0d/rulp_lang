package beta.test.utils;

import java.util.List;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.FormatUtil;
import alpha.rulp.utils.RulpTestBase;

public class FormatUtilTest extends RulpTestBase {

	List<String> _format_1(List<String> lines) throws RException {
		return FormatUtil.format(lines);
	}

	@Test
	void test_format_1() {

		_setup();

		_test_multi((input) -> {
			return _format_1(input);
		});
	}

}
