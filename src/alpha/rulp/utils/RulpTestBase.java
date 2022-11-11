/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInput;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpUtil.RResultList;
import alpha.rulp.ximpl.error.REofException;
import alpha.rulp.ximpl.optimize.LCOUtil;
import alpha.rulp.ximpl.optimize.OptUtil;

public class RulpTestBase {

	public static interface ITActionInStrListOutStrList {
		public List<String> test(List<String> input) throws RException, IOException;
	}

	public static interface ITActionInStrOutStr {
		public String test(String input) throws RException, IOException;
	}

	protected static class XROut implements IROut {

		StringBuffer sb = new StringBuffer();

		public void clear() {
			sb.setLength(0);
		}

		public String getOut() {
			return sb.toString();
		}

		@Override
		public void out(String line) {
			sb.append(line);
		}
	}

	static final String BETA_TEST_PRE = "beta.test.";

	static final String IT_PRE_BGN = "BGN: ";

	static final String IT_PRE_EOF = "EOF";

	static final String IT_PRE_ERR = "ERR: ";

	static final String IT_PRE_INP = "INP: ";

	static final String IT_PRE_OUT = "OUT: ";

	static final String KEY_COMMENT = ";;;";

	static final String KEY_EOF = ";eof";

	static final String KEY_ERR = ";err";

	static final String KEY_ERR2 = ";err:";

	static final String KEY_IN = ";in:";

	static final String KEY_OUT = ";out:";

	static final String KEY_RESULT = ";=>";

	static final String V_SCRIPT_PATH = "?script-path";

	protected static String _getClassPathName(Class<?> c, int tailIndex) {

		if (c == null || tailIndex < 0) {
			return null;
		}

		String fullName = c.getCanonicalName();
		if (fullName == null) {
			return null;
		}

		String[] names = fullName.split("\\.");
		if (names == null || tailIndex >= names.length) {
			return null;
		}

		return names[names.length - tailIndex - 1];
	}

	protected static String _load(String path) {

		try {
			return RulpUtil.toOneLine(FileUtil.openTxtFile(path));
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
			return null;
		}
	}

	protected static String _load(String path, String charset) {

		try {
			return RulpUtil.toOneLine(FileUtil.openTxtFile(path, charset));
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
			return null;
		}
	}

	protected IRInterpreter _interpreter;

	protected XROut _out = null;;

	protected IRParser _parser = null;

	protected IRInterpreter _createInterpreter() throws RException, IOException {
		return RulpFactory.createInterpreter();
	}

	protected IRInterpreter _getInterpreter() throws RException, IOException {

		if (_interpreter == null) {
			_interpreter = _createInterpreter();
			_out = new XROut();
			_interpreter.setOutput(_out);
		}

		return _interpreter;
	}

	protected IRParser _getParser() {

		if (_parser == null) {
			_parser = RulpFactory.createParser();
		}
		return _parser;
	}

	protected void _gInfo() {
		_gInfo(getCachePath() + ".ginfo.txt");
	}

	protected void _gInfo(String outputPath) {

		try {
			String outoputInfo = TraceUtil.printGlobalInfo(_getInterpreter());
			ArrayList<String> lines = new ArrayList<>();
			lines.add(outoputInfo);
			FileUtil.saveTxtFile(outputPath, lines, "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	protected void _run_script() {
		_run_script(getCachePath() + ".rulp");
	}

	protected void _run_script(String inputScriptPath) {

		try {

			StringBuffer sb = new StringBuffer();

			boolean rc = true;

			List<String> lines = FileUtil.openTxtFile(inputScriptPath, "utf-8");
			ArrayList<String> outLines = new ArrayList<>();
			ArrayList<String> inLines = null;

			int size = lines.size();
			int index = 0;
			String inputStmt = null;
			IRInterpreter interpreter = _getInterpreter();

			LoadUtil.loadScript(interpreter, interpreter.getMainFrame(), "test/itest", "utf-8");
			RulpUtil.setLocalVar(interpreter.getMainFrame(), V_SCRIPT_PATH, RulpFactory.createString(inputScriptPath));

			for (; rc && index < size; ++index) {

				String line = lines.get(index);

				if (line.trim().equals(KEY_COMMENT)) {// ;;;
					inputStmt = sb.toString();
					sb.setLength(0);
					rc = _run_stmt(inputStmt, null, null, null, inLines, outLines, interpreter);
					inLines = null;

				} else if (line.startsWith(KEY_RESULT)) {// ;=>

					inputStmt = sb.toString();
					sb.setLength(0);
					String expectResult = line.substring(3);
					if (expectResult.trim().isEmpty()) {
						expectResult = null;
					}

					rc = _run_stmt(inputStmt, false, expectResult, null, inLines, outLines, interpreter);
					inLines = null;

				} else if (line.trim().equals(KEY_ERR)) {// ;err

					inputStmt = sb.toString();
					sb.setLength(0);

					rc = _run_stmt(inputStmt, true, null, null, inLines, outLines, interpreter);
					inLines = null;

				} else if (line.trim().equals(KEY_ERR2)) {// ;err:

					inputStmt = sb.toString();
					sb.setLength(0);

					StringBuffer expectSb = new StringBuffer();

					while (++index < size) {

						String nextLine = lines.get(index);
						if (nextLine.trim().equals(KEY_EOF)) {
							break;
						}

						if (expectSb.length() > 0) {
							expectSb.append('\n');
						}
						expectSb.append(nextLine);
					}

					rc = _run_stmt(inputStmt, true, null, expectSb.toString(), inLines, outLines, interpreter);
					inLines = null;

				} else if (line.trim().equals(KEY_IN)) {// ;in:

					inLines = new ArrayList<>();
					while (++index < size) {

						String nextLine = lines.get(index);
						if (nextLine.trim().equals(KEY_EOF)) {
							break;
						}
						inLines.add(nextLine);
					}

				} else {

					if (sb.length() != 0) {
						sb.append("\n");
					}

					sb.append(line);
				}
			}

			if (rc && sb.length() != 0) {
				inputStmt = sb.toString();
				sb.setLength(0);
				rc = _run_stmt(inputStmt, false, null, null, inLines, outLines, interpreter);
				inLines = null;
			}

			FileUtil.saveTxtFile(inputScriptPath + ".out", outLines, "utf-8");

			if (!rc) {
				fail(String.format("error found in line: %d, file=%s", index, inputScriptPath));
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("error found: %s, file=%s", e.toString(), inputScriptPath));
		}

	}

	protected boolean _run_stmt(String inputStmt, Boolean expectError, String expectResult, String expectErrorMessage,
			List<String> inLines, List<String> outLines, IRInterpreter interpreter) {

		RResultList rsultList = null;

		// init output
		_out.clear();

		// init input
		IRInput oldIn = null;

		try {

			if (inLines != null) {

				oldIn = interpreter.getInput();

				interpreter.setInput(new IRInput() {

					int idx = 0;

					@Override
					public String read() throws RException {

						if (idx >= inLines.size()) {
							throw new REofException();
						}

						String line = inLines.get(idx++);
						_out.out(line + "\n");

						return line;
					}
				});
			}

			outLines.add(inputStmt);
			if (inLines != null) {
				outLines.add(KEY_IN);
				for (String inLine : inLines) {
					outLines.add(inLine);
				}
				outLines.add(KEY_EOF);
			}

			rsultList = RulpUtil.compute(interpreter, inputStmt);
			String result = RulpUtil.toString(rsultList.results);
			outLines.add(KEY_RESULT + result);

			String output = _out.getOut();
			if (output != null && !output.isEmpty()) {
				outLines.add(KEY_OUT);
				outLines.add(output);
				outLines.add(KEY_EOF);
			}

			outLines.add("");

			if (expectResult != null && !result.equals(expectResult)) {
				return false;
			}

			if (expectError != null && expectError) {
				return false;
			}

		} catch (Exception e) {

			outLines.add(KEY_ERR2);
			outLines.add(e.getMessage());
			outLines.add(KEY_EOF);
			outLines.add("");

			if (expectError != null && !expectError) {
				e.printStackTrace();
				return false;
			}

			if (expectErrorMessage != null && !expectErrorMessage.equals(e.getMessage())) {
				e.printStackTrace();
				return false;
			}

		} catch (Error e) {

			e.printStackTrace();

			outLines.add(KEY_ERR2);
			outLines.add(e.toString());
			outLines.add(KEY_EOF);
			outLines.add("");

			if (expectError != null && !expectError) {
				e.printStackTrace();
				return false;
			}

			if (expectErrorMessage != null && !expectErrorMessage.equals(e.toString())) {
				e.printStackTrace();
				return false;
			}

		} finally {

			if (rsultList != null) {
				try {
					rsultList.free();
				} catch (RException e1) {
					e1.printStackTrace();
					fail(e1.toString());
				}
			}

			if (oldIn != null) {
				interpreter.setInput(oldIn);
			}
		}

		return true;
	}

	protected void _setup() {

		_interpreter = null;
		_parser = null;

		OptUtil.reset();
		LCOUtil.reset();

		RulpFactory.reset();
		RuntimeUtil.reset();
		FileUtil.reset();
	}

	protected void _test(ITActionInStrOutStr action) {

		String inputPath = getCachePath() + ".txt";
		String outputPath = getCachePath() + ".txt.out";

		ArrayList<String> outLines = new ArrayList<>();

		try {

			List<String> lines = FileUtil.openTxtFile(inputPath, "utf-8");

			StringBuffer multiBuf = new StringBuffer();
			int multiIndex = -1;

			for (int index = 0; index < lines.size(); ++index) {

				String line = lines.get(index);

				if (line.trim().isEmpty() || line.startsWith(";")) {
					continue;
				}

				if (line.startsWith(IT_PRE_INP)) {

					if (multiIndex != -1) {
						throw new IOException("multi line not end: " + multiIndex);
					}

					String input = line.substring(IT_PRE_INP.length());
					String output = "";

					try {
						output = IT_PRE_OUT + action.test(input);
					} catch (Exception e) {
						output = IT_PRE_ERR + e.toString();
					}

					outLines.add(output);

				} else if (line.startsWith(IT_PRE_BGN)) {

					if (multiIndex != -1) {
						throw new IOException("duplicated multi line: " + index);
					}

					multiBuf.append(line.substring(IT_PRE_BGN.length()));
					multiIndex = index;

				} else if (line.trim().equals(IT_PRE_EOF)) {

					if (multiIndex == -1) {
						throw new IOException("multi line not begin: " + index);
					}

					String input = multiBuf.toString();
					String output = "";

					try {
						output = IT_PRE_OUT + action.test(input);
					} catch (Exception e) {
						output = IT_PRE_ERR + e.toString();
					}

					outLines.add(output);
					multiBuf.setLength(0);
					multiIndex = -1;

				} else if (multiIndex != -1) {

					multiBuf.append('\n');
					multiBuf.append(line);

				} else {

					throw new IOException("Invalid input line: " + line);
				}
			}

			if (multiIndex != -1) {
				throw new IOException("multi line not end: " + multiIndex);
			}

			FileUtil.saveTxtFile(outputPath, outLines, "utf-8");

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	protected void _test(String input) {
		_test(input, null, null);
	}

	protected void _test(String input, String expectResult) {
		_test(input, expectResult, "");
	}

	protected void _test(String input, String expectResult, String expectOutput) {
//		System.out.println(input);
//		System.out.println(";=>");
//		System.out.println();

		RResultList rsultList = null;
		try {

			IRInterpreter interpreter = _getInterpreter();
			_out.clear();

			rsultList = RulpUtil.compute(interpreter, input);
			String output = _out.getOut();

			if (expectResult != null) {
				assertEquals(String.format("input=%s", input), expectResult, RulpUtil.toString(rsultList.results));
			}

			if (expectOutput != null) {
				assertEquals(String.format("input=%s", input), expectOutput, output);
			}

		} catch (Exception e) {

			e.printStackTrace();
			fail(e.toString());

		} finally {

			if (rsultList != null) {
				try {
					rsultList.free();
				} catch (RException e1) {
					e1.printStackTrace();
					fail(e1.toString());
				}
			}
		}
	}

	protected void _test_error(String input, String expectError) {

//		System.out.println(input);
//		System.out.println(";err");
//		System.out.println();

		try {

			IRInterpreter interpreter = _getInterpreter();
			_out.clear();
			interpreter.compute(input);
			fail("Should fail: " + input);

		} catch (Exception e) {
			assertEquals(String.format("input=%s", input), expectError, e.getMessage());
		} catch (Error e) {
			assertEquals(String.format("input=%s", input), expectError, e.toString());
		}
	}

	protected void _test_multi(ITActionInStrListOutStrList action) {

		String inputPath = getCachePath() + ".txt";
		String outputPath = getCachePath() + ".txt.out";

		ArrayList<String> outLines = new ArrayList<>();

		try {

			List<String> lines = FileUtil.openTxtFile(inputPath, "utf-8");

			try {
				outLines.addAll(action.test(lines));
			} catch (RException e) {
				outLines.add(IT_PRE_ERR + e.toString());
			}

			FileUtil.saveTxtFile(outputPath, outLines, "utf-8");

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	protected String getCachePath() {

		String packageName = getPackageName();

		if (!packageName.startsWith(BETA_TEST_PRE)) {
			throw new RuntimeException("Invalid package: " + packageName);
		}

		String packageShortName = packageName.substring(BETA_TEST_PRE.length());
		String className = _getClassPathName(this.getClass(), 0);
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();

		return "result" + File.separator + packageShortName + File.separator + className + File.separator + methodName;

	}

	protected String getPackageName() {
		return this.getClass().getPackage().getName();
	}

}
