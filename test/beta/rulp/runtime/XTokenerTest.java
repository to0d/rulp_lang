
package beta.rulp.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.runtime.IRTokener.Token;
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

	void _test_token(String input, String expect) {

		IRTokener tokener = RulpFactory.createTokener();
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
	public void test_char_type() {
		_test_char_type("哈利·托特达夫", "0060000");
		_test_char_type("abc123\'\"  %?\n\r", "00011163226677");
	}

	@Test
	public void test_token_escape() {
		_test_token("\"\\\"", "[B:2:0:3:\"\"];");
		_test_token("\"a\\\"b\"", "[T:5:0:6:\"a\"b\"];");
	}

	@Test
	public void test_token_operator() {

		_test_token("<", "[S:1:0:1:<];");
		_test_token("++", "[S:1:0:1:+]; [S:1:0:2:+];");
		_test_token("--", "[S:1:0:1:-]; [S:1:0:2:-];");

	}

	@Test
	public void test_token_parse() {

		_test_token("1.1d", "[F:3:0:3:1.1]; [N:1:0:4:d];");

		_test_token("\"'hello'\"", "[T:9:0:9:\"'hello'\"];");

		_test_token("a\nb", "[N:1:0:1:a]; [N:1:0:3:b];");

		_test_token("a-b", "[N:1:0:1:a]; [S:1:0:2:-]; [N:1:0:3:b];");

		_test_token("ABC123", "[N:6:0:6:ABC123];");

		_test_token("123ABC", "[N:6:0:6:123ABC];");

		_test_token("ABC 123", "[N:3:0:3:ABC]; [X:1:0:4: ]; [I:3:0:7:123];");

		_test_token("ABC -123", "[N:3:0:3:ABC]; [X:1:0:4: ]; [S:1:0:5:-]; [I:3:0:8:123];");

		_test_token("ABC -123.5", "[N:3:0:3:ABC]; [X:1:0:4: ]; [S:1:0:5:-]; [F:5:0:10:123.5];");

		_test_token("123 ABC", "[I:3:0:3:123]; [X:1:0:4: ]; [N:3:0:7:ABC];");

		_test_token("123_ABC", "[N:7:0:7:123_ABC];");

		_test_token("ABC_123", "[N:7:0:7:ABC_123];");

		_test_token("ABC_123___", "[N:10:0:10:ABC_123___];");

		_test_token("_ABC123", "[N:7:0:7:_ABC123];");

		_test_token("____ABC123", "[N:10:0:10:____ABC123];");

		_test_token("____ABC123___", "[N:13:0:13:____ABC123___];");

		_test_token("__", "[N:2:0:2:__];");

		_test_token("_123ABC", "[N:7:0:7:_123ABC];");

		_test_token("XXX 123ABC", "[N:3:0:3:XXX]; [X:1:0:4: ]; [N:6:0:10:123ABC];");

		_test_token("XXX 123 ABC", "[N:3:0:3:XXX]; [X:1:0:4: ]; [I:3:0:7:123]; [X:1:0:8: ]; [N:3:0:11:ABC];");

		_test_token("XXX 123.5 ABC", "[N:3:0:3:XXX]; [X:1:0:4: ]; [F:5:0:9:123.5]; [X:1:0:10: ]; [N:3:0:13:ABC];");

		_test_token(".2 A", "[S:1:0:1:.]; [I:1:0:2:2]; [X:1:0:3: ]; [N:1:0:4:A];");

		_test_token("ABC \" ' 12\" ", "[N:3:0:3:ABC]; [X:1:0:4: ]; [T:7:0:11:\" ' 12\"]; [X:1:0:12: ];");

		_test_token("{}", "[S:1:0:1:{]; [S:1:0:2:}];");

		_test_token("{};", "[S:1:0:1:{]; [S:1:0:2:}]; [S:1:0:3:;];");

	}

	@Test
	public void test_token_parse_any() {

		_test_token("(3.2万)", "[S:1:0:1:(]; [F:3:0:4:3.2]; [N:1:0:5:万]; [S:1:0:6:)];");
	}

	@Test
	public void test_token_parse_cn() {

		_test_token("哈利·托特达夫", "[N:2:0:2:哈利]; [S:1:0:3:·]; [N:4:0:7:托特达夫];");
	}

	@Test
	public void test_token_parse_strict() {

		_test_token("\")", "[B:2:0:2:\")];");
		_test_token("(PU \")", "[S:1:0:1:(]; [N:2:0:3:PU]; [X:1:0:4: ]; [B:2:0:6:\")];");
	}

	@Test
	public void test_token_parse_strict_not() {

		_test_token_not_strict("\")", "[N:1:0:1:\"]; [S:1:0:2:)];");
		_test_token_not_strict("(PU \")", "[S:1:0:1:(]; [N:2:0:3:PU]; [X:1:0:4: ]; [N:1:0:5:\"]; [S:1:0:6:)];");
	}

	@Test
	public void test_token_parse2() {

		_test_token("&name", "[S:1:0:1:&]; [N:4:0:5:name];");
		_test_token("-NONE-", "[S:1:0:1:-]; [N:4:0:5:NONE]; [S:1:0:6:-];");
		_test_token("*pro*", "[S:1:0:1:*]; [N:3:0:4:pro]; [S:1:0:5:*];");
		_test_token("*T*-2", "[S:1:0:1:*]; [N:1:0:2:T]; [S:1:0:3:*]; [S:1:0:4:-]; [I:1:0:5:2];");

	}

	@Test
	public void test_token_parse_array() {
		_test_token("{a b c,x y,z}",
				"[S:1:0:1:{]; [N:1:0:2:a]; [X:1:0:3: ]; [N:1:0:4:b]; [X:1:0:5: ]; [N:1:0:6:c]; [S:1:0:7:,]; [N:1:0:8:x]; [X:1:0:9: ]; [N:1:0:10:y]; [S:1:0:11:,]; [N:1:0:12:z]; [S:1:0:13:}];");
	}
}
