
package beta.rulp.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.runtime.IRTokener.Token;
import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.ximpl.runtime.XRTokener;

public class XTokenerTest extends RulpTestBase {

	void _test_char_type(String input, String expect) {

		String output = "";
		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			output += XRTokener.getCharType(c);
		}

		assertEquals(expect, output);
	}

	void _test_pase_token() {
		_test_pase_token(getCachePath() + ".txt");
	}

	void _test_pase_token(String path) {

		ArrayList<String> outlines = new ArrayList<>();

		try {

			for (String line : FileUtil.openTxtFile(path, "utf-8")) {

				outlines.add("in : " + line);

				IRTokener tokener = RulpFactory.createTokener();
				tokener.setContent(line);
				StringBuilder sb = new StringBuilder();

				Token token = null;
				while ((token = tokener.next()) != null) {
					sb.append(token.toString());
					sb.append(" ");
				}

				outlines.add("out: " + sb.toString());
				outlines.add("");
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("error found: %s, file=%s", e.toString(), path));
		} finally {
			try {
				FileUtil.saveTxtFile(path + ".out", outlines, "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void _test_token_not_strict(String input, String expect) {

		IRTokener tokener = RulpFactory.createTokener();
		tokener.setStrictMode(false);
		tokener.setContent(input);
		StringBuilder sb = new StringBuilder();

		Token token = null;
		try {

			while ((token = tokener.next()) != null) {
				sb.append(token.toString());
				sb.append("; ");
			}

		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		sb.setLength(sb.length() - 1);
		assertEquals(expect, sb.toString());
	}

	@Test
	public void test_token_1_char_type() {
		_setup();
		_test_char_type("¹þÀû¡¤ÍÐÌØ´ï·ò", "0060000");
		_test_char_type("abc123\'\"  %?\n\r", "00011163226677");
	}

	@Test
	public void test_token_2() {
		_setup();
		_test_pase_token();
	}

	@Test
	public void test_token_3_cn() {
		_setup();
		_test_pase_token();
	}

	@Test
	public void test_token_4_strict() {
		_setup();
		_test_pase_token();
	}

	@Test
	public void test_token_5_not_strict() {
		_setup();
		_test_token_not_strict("\")", "[N:1:0:1:\"]; [S:1:0:2:)];");
		_test_token_not_strict("(PU \")", "[S:1:0:1:(]; [N:2:0:3:PU]; [X:1:0:4: ]; [N:1:0:5:\"]; [S:1:0:6:)];");
	}

	@Test
	public void test_token_6_array() {
		_setup();
		_test_pase_token();
	}

	@Test
	public void test_token_7_operator() {
		_setup();
		_test_pase_token();
	}

	@Test
	public void test_token_8_escape() {
		_setup();
		_test_pase_token();
	}

	@Test
	public void test_token_9_attr() {
		_setup();
		_test_pase_token();
	}
}
