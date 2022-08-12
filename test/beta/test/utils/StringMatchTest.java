package beta.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.StringUtil;

public class StringMatchTest extends RulpTestBase {

	@Test
	void test_matchFormat_1() {

		assertTrue(StringUtil.matchFormat("00000     00420042 0042...*",
				"00000     00420042 0042.... ........ ........   - .?.?.?..........        "));

		assertTrue(StringUtil.matchFormat("00000     00420042 0042...*",
				"00000     00420042 0042.... ........ ........   - .?.?.?..........        "));

	}

	@Test
	void test_matchFormat_2() {

		ArrayList<String> result = new ArrayList<>();
		assertTrue(StringUtil.matchFormat("%?.ol", "sql_fix_bug_73_usr.ol", result));
		assertEquals(1, result.size());
	}

	@Test
	void test_matchFormat_3() {

		ArrayList<String> result = new ArrayList<>();
		assertTrue(StringUtil.matchFormat(" Start Service Job (STRSRVJOB JOB(%?)).*",
				"   Start Service Job (STRSRVJOB JOB(096326/LIUYAN/SEPCB)). Then do Start Debug  ", result));

		assertTrue(result.size() == 1);
		assertEquals("096326/LIUYAN/SEPCB", result.get(0));

		assertFalse(StringUtil.matchFormat("--[DBG][%?]%?;%?", "--[DBG][           @AUTO]@AUTO;", result));

		assertTrue(
				StringUtil.matchFormat(" %%INCLUDE INCLIB(" + "TEMDCO" + ")*", "  %INCLUDE INCLIB(TEMDCO);", result));
	}

	@Test
	void test_matchFormat_4() {

		assertTrue(StringUtil.matchFormat("Overview of the integrated file system%$",
				"Overview of the integrated file system"));

		assertFalse(StringUtil.matchFormat("Overview of the integrated file system%$",
				"Overview of the integrated file system. . . . . . 2"));
	}

	@Test
	void test_matchFormat_5() {

		// *|version|*[*|%d|]|*|*%|'%?'
		// version [ 4] | 'V7R2'
		ArrayList<String> values = new ArrayList<>();

		assertTrue(StringUtil.matchFormat("*sectorSize*%|* %? *%dx",
				"sectorSize    |                 512              200x", values));

		assertEquals("[512]", values.toString());

	}

	@Test
	void test_matchFormat_6() {

		String txt = "      Job 204881/LIUYAN/TESTEST started on 11/22/14 at 21:48:23 in subsystem    ";
		ArrayList<String> result = new ArrayList<>();
		assertTrue(StringUtil.matchFormat(" Job %?/%?/%? started on *", txt, result));

		assertEquals("204881", result.get(0));
		assertEquals("LIUYAN", result.get(1));
		assertEquals("TESTEST", result.get(2));
	}

	@Test
	void test_matchFormat_7_new_line() {

		ArrayList<String> values = new ArrayList<>();

		assertTrue(StringUtil.matchFormat("%?%n%?", "abc\nedf", values));
		assertEquals("[abc, edf]", values.toString());

		assertTrue(StringUtil.matchFormat("%?%r%?", "abc\redf", values));
		assertEquals("[abc, edf]", values.toString());

		assertTrue(StringUtil.matchFormat("%?%r%n%?", "abc\r\nedf", values));
		assertEquals("[abc, edf]", values.toString());

	}

}
