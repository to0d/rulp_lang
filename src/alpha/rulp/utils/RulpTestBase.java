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
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpUtil.RResultList;
import alpha.rulp.ximpl.optimize.CPSUtils;

public class RulpTestBase {

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

	static String BETA_RULP_PRE = "beta.rulp.";

	protected static String _load(String path) {

		try {
			return StringUtil.toOneLine(FileUtil.openTxtFile(path));
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
			return null;
		}
	}

	protected static String _load(String path, String charset) {

		try {
			return StringUtil.toOneLine(FileUtil.openTxtFile(path, charset));
		} catch (RException | IOException e) {
			e.printStackTrace();
			fail(e.toString());
			return null;
		}
	}

	protected static String getClassPathName(Class<?> c, int tailIndex) {

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

	protected IRInterpreter _interpreter;

	protected IRParser _parser = null;

	protected XROut out = null;

	protected IRInterpreter _createInterpreter() throws RException, IOException {
		return RulpFactory.createInterpreter();
	}

	protected IRInterpreter _getInterpreter() throws RException, IOException {

		if (_interpreter == null) {
			_interpreter = _createInterpreter();
			out = new XROut();
			_interpreter.setOutput(out);
		}

		return _interpreter;
	};

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

	protected boolean _run_stmt(String inputStmt, Boolean expectError, String expectResult, String expectErrorMessage,
			ArrayList<String> outLines, IRInterpreter interpreter) {

		RResultList rsultList = null;

		try {

			out.clear();
			outLines.add(inputStmt);

			rsultList = RulpUtil.compute(interpreter, inputStmt);
			String result = RulpUtil.toString(rsultList.results);
			outLines.add(";=>" + result);

			String output = out.getOut();
			if (output != null && !output.isEmpty()) {
				outLines.add(";out:");
				outLines.add(output);
				outLines.add(";eof");
			}

			outLines.add("");

			if (expectResult != null && !result.equals(expectResult)) {
				return false;
			}

			if (expectError != null && expectError) {
				return false;
			}

		} catch (Exception e) {

			outLines.add(";err:");
			outLines.add(e.getMessage());
			outLines.add(";eof");
			outLines.add("");

			if (expectError != null && !expectError) {
				e.printStackTrace();
				return false;
			}

			if (expectErrorMessage != null && !expectErrorMessage.equals(e.getMessage())) {
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
		}

		return true;
	}

	protected void _run_script(String inputScriptPath) {

		try {

			StringBuffer sb = new StringBuffer();

			boolean rc = true;

			List<String> lines = FileUtil.openTxtFile(inputScriptPath);
			ArrayList<String> outLines = new ArrayList<>();

			int size = lines.size();
			int index = 0;
			String inputStmt = null;
			IRInterpreter interpreter = _getInterpreter();

			TEST: for (; rc && index < size; ++index) {

				String line = lines.get(index);

				if (line.trim().equals(";;;")) {
					inputStmt = sb.toString();
					sb.setLength(0);
					rc = _run_stmt(inputStmt, null, null, null, outLines, interpreter);

				} else if (line.startsWith(";=>")) {

					inputStmt = sb.toString();
					sb.setLength(0);
					String expectResult = line.substring(3);
					if (expectResult.trim().isEmpty()) {
						expectResult = null;
					}

					rc = _run_stmt(inputStmt, false, expectResult, null, outLines, interpreter);

				} else if (line.trim().equals(";err")) {

					inputStmt = sb.toString();
					sb.setLength(0);

					rc = _run_stmt(inputStmt, true, null, null, outLines, interpreter);

				} else if (line.trim().equals(";err:")) {

					inputStmt = sb.toString();
					sb.setLength(0);

					StringBuffer expectSb = new StringBuffer();

					while (++index < size) {

						String nextLine = lines.get(index);
						if (nextLine.trim().equals(";eof")) {
							break;
						}

						if (expectSb.length() > 0) {
							expectSb.append('\n');
						}
						expectSb.append(nextLine);
					}

					rc = _run_stmt(inputStmt, true, null, expectSb.toString(), outLines, interpreter);

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
				rc = _run_stmt(inputStmt, false, null, null, outLines, interpreter);
			}

			FileUtil.saveTxtFile(inputScriptPath + ".out", outLines);

			if (!rc) {
				fail(String.format("error found in line: %d, file=%s", index, inputScriptPath));
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("error found: %s, file=%s", e.toString(), inputScriptPath));
		}
	}

	protected void _setup() {

		_interpreter = null;
		_parser = null;

		CPSUtils.resetCpsLoopCount();
		RulpFactory.reset();
		RuntimeUtil.reset();
	}

	protected void _test(String input) {
		System.out.println(input);
		System.out.println(";;;");
		System.out.println();
		_test(input, null, null);
	}

	protected void _test(String input, String expectResult) {
		System.out.println(input);
		System.out.println(";=>");
		System.out.println();
		_test(input, expectResult, "");
	}

	protected void _test(String input, String expectResult, String expectOutput) {

		RResultList rsultList = null;
		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();

			rsultList = RulpUtil.compute(interpreter, input);
			String output = out.getOut();

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
//		System.out.println(";err:");
//		System.out.println();

		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();
			interpreter.compute(input);
			fail("Should fail: " + input);

		} catch (RException | IOException e) {
			assertEquals(String.format("input=%s", input), expectError, e.getMessage());
		}
	}

	protected void _test_match(String input, String expectResult, String expecFile) {

		RResultList rsultList = null;
		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();

			rsultList = RulpUtil.compute(interpreter, input);

			String output = out.getOut();
			if (expectResult != null) {
				assertEquals(String.format("input=%s", input), expectResult, RulpUtil.toString(rsultList.results));
			}

			if (expecFile != null) {

				List<String> outputLines = new ArrayList<>();
				for (String line : StringUtil.splitStringByChar(output, '\n')) {
					if (expecFile.trim().isEmpty()) {
						continue;
					}
					outputLines.add(line.trim());
				}

				List<String> expectLines = new ArrayList<>();
				for (String line : FileUtil.openTxtFile(expecFile)) {
					if (expecFile.trim().isEmpty()) {
						continue;
					}
					expectLines.add(line.trim());
				}

				if (outputLines.size() != expectLines.size()) {
					assertEquals(String.format("input=%s", input), StringUtil.toOneLine(expectLines), output);
				}

				int size = expectLines.size();
				for (int i = 0; i < size; ++i) {
					String expectLine = expectLines.get(i);
					String outputLine = outputLines.get(i);
					if (!StringUtil.matchFormat(expectLine, outputLine)) {
						assertEquals(String.format("input=%s", input), StringUtil.toOneLine(expectLines), output);
					}
				}

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

//	protected void _test_script2() {
//		_test_script2(getCachePath() + ".rulp");
//	}
//
//	protected void _test_script2(String scriptPath) {
//
//		String input = null;
//		String result = null;
//		String output = null;
//		Exception exception = null;
//
//		try {
//
//			IRInterpreter interpreter = _getInterpreter();
//			out.clear();
//
//			List<String> lines = FileUtil.openTxtFile(scriptPath);
//			int size = lines.size();
//
//			for (int index = 0; index < size; ++index) {
//
//				String line = lines.get(index);
//				if ((line = line.trim()).isEmpty() || line.startsWith(";;")) {
//					continue;
//				}
//
//				if (line.startsWith(";END")) {
//					return;
//				}
//
//				if (line.startsWith(";=>")) {
//
//					if (input == null) {
//						throw new RException("Input not found: " + scriptPath);
//					}
//
//					String expectResult = line.substring(";=>".length());
//					if (result == null) {
//
//						if (exception != null) {
//							exception.printStackTrace();
//						}
//
//						fail(String.format("no result found, line=%d, input=%s, expect=%s", index, input,
//								expectResult));
//					}
//
//					assertEquals(String.format("unexpect result, line=%d, input=%s", index, input), expectResult,
//							result);
//
//				} else if (line.trim().equals(";out:")) {
//
//					if (input == null) {
//						throw new RException("Input not found: " + scriptPath);
//					}
//
//					StringBuffer expectSb = new StringBuffer();
//
//					while (++index < size) {
//
//						String nextLine = lines.get(index);
//						if (nextLine.trim().equals(";eof")) {
//							break;
//						}
//
//						if (expectSb.length() > 0) {
//							expectSb.append('\n');
//						}
//						expectSb.append(nextLine);
//					}
//
//					if (output == null) {
//						if (exception != null) {
//							exception.printStackTrace();
//						}
//						fail(String.format("no output found, line=%d, input=%s, expect=%s", index, input, output));
//					}
//
//					while (output.endsWith("\n")) {
//						output = output.substring(0, output.length() - 1);
//					}
//
//					String expectOut = expectSb.toString();
//					assertEquals(String.format("unexpect out, line=%d, input=%s", index, input), expectOut, output);
//
//				} else if (line.trim().equals(";err:")) {
//
//					if (input == null) {
//						throw new RException("Input not found: " + scriptPath);
//					}
//
//					StringBuffer expectSb = new StringBuffer();
//
//					while (++index < size) {
//
//						String nextLine = lines.get(index);
//						if (nextLine.trim().equals(";eof")) {
//							break;
//						}
//
//						if (expectSb.length() > 0) {
//							expectSb.append('\n');
//						}
//
//						expectSb.append(nextLine);
//					}
//
//					String expectErr = expectSb.toString();
//					if (exception == null) {
//						fail(String.format("no exception found, line=%d, input=%s, expect=%s", index, input,
//								expectErr));
//					}
//
//					assertEquals(String.format("unexpect out, line=%d, input=%s", index, input), expectErr,
//							exception.getMessage());
//					exception = null;
//
//				} else {
//
//					if (exception != null) {
//						exception.printStackTrace();
//						fail("unexpect exception: " + exception.getMessage() + ", input=" + input);
//					}
//
//					input = line;
//					while ((index + 1) < size) {
//
//						String nextLine = lines.get(index + 1);
//						if (nextLine.trim().startsWith(";")) {
//							break;
//						}
//
//						input += "\n" + nextLine;
//						index++;
//					}
//
//					exception = null;
//					output = null;
//					result = null;
//
//					try {
//						out.clear();
//						result = RulpUtil.toString(interpreter.compute(input));
//						output = out.getOut();
//					} catch (Exception e) {
//						exception = e;
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(String.format("error found in line: %s, file=%s", input, scriptPath));
//		}
//	}

	protected String getCachePath() {

		String packageName = getPackageName();

		if (!packageName.startsWith(BETA_RULP_PRE)) {
			throw new RuntimeException("Invalid package: " + packageName);
		}

		String packageShortName = packageName.substring(BETA_RULP_PRE.length());
		String className = getClassPathName(this.getClass(), 0);
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();

		return "result" + File.separator + packageShortName + File.separator + className + File.separator + methodName;

	}

	protected String getPackageName() {
		return this.getClass().getPackage().getName();
	}
}
