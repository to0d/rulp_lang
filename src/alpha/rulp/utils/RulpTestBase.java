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

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
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

	protected void _test_script() {
		_test_script(getCachePath() + ".rulp");
	}

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
	}

	protected IRParser _getParser() {

		if (_parser == null) {
			_parser = RulpFactory.createParser();
		}
		return _parser;
	};

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

	protected void _setup() {

		_interpreter = null;
		_parser = null;

		CPSUtils.resetCpsLoopCount();
		RulpFactory.reset();
		RuntimeUtil.reset();
	}

	protected void _test(String input) {
		_test(input, null, null);
	}

	protected void _test(String input, String expectResult) {
		_test(input, expectResult, "");
	}

	protected void _test(String input, String expectResult, String expectOutput) {

		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();

			List<IRObject> result = interpreter.compute(input);
			String output = out.getOut();

			if (expectResult != null) {
				assertEquals(String.format("input=%s", input), expectResult, RulpUtil.toString(result));
			}

			if (expectOutput != null) {
				assertEquals(String.format("input=%s", input), expectOutput, output);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	protected void _test_error(String input, String expectError) {

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

		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();

			List<IRObject> result = interpreter.compute(input);
			String output = out.getOut();

			if (expectResult != null) {
				assertEquals(String.format("input=%s", input), expectResult, RulpUtil.toString(result));
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
		}
	}

	protected String getPackageName() {
		return this.getClass().getPackage().getName();
	}

	static String BETA_RULP_PRE = "beta.rulp.";

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

	protected void _test_script(String scriptPath) {

		String input = null;
		String result = null;
		String output = null;
		Exception exception = null;

		try {

			IRInterpreter interpreter = _getInterpreter();
			out.clear();

			List<String> lines = FileUtil.openTxtFile(scriptPath);
			int size = lines.size();

			for (int index = 0; index < size; ++index) {

				String line = lines.get(index);
				if ((line = line.trim()).isEmpty() || line.startsWith(";;")) {
					continue;
				}

				if (line.startsWith(";END")) {
					return;
				}

				if (line.startsWith(";=>")) {

					if (input == null) {
						throw new RException("Input not found: " + scriptPath);
					}

					String expectResult = line.substring(";=>".length());
					if (result == null) {

						if (exception != null) {
							exception.printStackTrace();
						}

						fail(String.format("no result found, line=%d, input=%s, expect=%s", index, input,
								expectResult));
					}

					assertEquals(String.format("unexpect result, line=%d, input=%s", index, input), expectResult,
							result);

				} else if (line.trim().equals(";out:")) {

					if (input == null) {
						throw new RException("Input not found: " + scriptPath);
					}

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

					if (output == null) {
						if (exception != null) {
							exception.printStackTrace();
						}
						fail(String.format("no output found, line=%d, input=%s, expect=%s", index, input, output));
					}

					while (output.endsWith("\n")) {
						output = output.substring(0, output.length() - 1);
					}

					String expectOut = expectSb.toString();
					assertEquals(String.format("unexpect out, line=%d, input=%s", index, input), expectOut, output);

				} else if (line.trim().equals(";err:")) {

					if (input == null) {
						throw new RException("Input not found: " + scriptPath);
					}

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

					String expectErr = expectSb.toString();
					if (exception == null) {
						fail(String.format("no exception found, line=%d, input=%s, expect=%s", index, input,
								expectErr));
					}

					assertEquals(String.format("unexpect out, line=%d, input=%s", index, input), expectErr,
							exception.getMessage());
					exception = null;

				} else {

					if (exception != null) {
						exception.printStackTrace();
						fail("unexpect exception: " + exception.getMessage() + ", input=" + input);
					}

					input = line;
					while ((index + 1) < size) {

						String nextLine = lines.get(index + 1);
						if (nextLine.trim().startsWith(";")) {
							break;
						}

						input += "\n" + nextLine;
						index++;
					}

					exception = null;
					output = null;
					result = null;

					try {
						out.clear();
						result = RulpUtil.toString(interpreter.compute(input));
						output = out.getOut();
					} catch (Exception e) {
						exception = e;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("error found in line: %s, file=%s", input, scriptPath));
		}
	}
}
