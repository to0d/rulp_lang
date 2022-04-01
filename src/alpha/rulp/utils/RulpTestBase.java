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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alpha.common.file.FileUtil;
import alpha.common.test.AlphaTestBase;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpUtil.RResultList;
import alpha.rulp.ximpl.optimize.CCOUtil;
import alpha.rulp.ximpl.optimize.EROUtil;
import alpha.rulp.ximpl.optimize.LCOUtil;
import alpha.rulp.ximpl.optimize.OptUtil;
import alpha.rulp.ximpl.optimize.TCOUtil;

public class RulpTestBase extends AlphaTestBase {

	static final String V_SCRIPT_PATH = "?script-path";

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

	protected XROut _out = null;

	protected IRParser _parser = null;

	protected IRInterpreter _createInterpreter() throws RException, IOException {
		return RulpFactory.createInterpreter();
	}

	protected IRInterpreter _getInterpreter() throws RException, IOException {

		if (_interpreter == null) {
			_interpreter = _createInterpreter();
			_out = new XROut();
			_interpreter.setOutput(_out);

			IRFrame mainFrame = _interpreter.getMainFrame();

//			RulpUtil.setLocalVar(mainFrame, V_TEST_SCRIPT, O_Nil);

//			RulpUtil.addFrameObject(mainFrame, new AbsRFactorAdapter("script_out") {
//
//				@Override
//				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
//
//					if (args.size() > 3) {
//						throw new RException("Invalid parameters: " + args);
//					}
//
//					String scriptPath = RulpUtil.asString(RulpUtil.getVarValue(frame, V_TEST_SCRIPT)).asString();
//					String outName = FileUtil.getFilePreName(scriptPath);
//					if (args.size() > 1) {
//						outName += "_" + RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
//					}
//
//					outName += ".ginfo.txt";
//					_gInfo(outName);
//					return O_Nil;
//				}
//			});
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

	protected void _run_script(String inputScriptPath) {

		try {

			StringBuffer sb = new StringBuffer();

			boolean rc = true;

			List<String> lines = FileUtil.openTxtFile(inputScriptPath, "utf-8");
			ArrayList<String> outLines = new ArrayList<>();

			int size = lines.size();
			int index = 0;
			String inputStmt = null;
			IRInterpreter interpreter = _getInterpreter();

			LoadUtil.loadScript(interpreter, interpreter.getMainFrame(), "test/itest", "utf-8");
			RulpUtil.setLocalVar(interpreter.getMainFrame(), V_SCRIPT_PATH, RulpFactory.createString(inputScriptPath));

			for (; rc && index < size; ++index) {

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
			ArrayList<String> outLines, IRInterpreter interpreter) {

		RResultList rsultList = null;

		try {

			_out.clear();
			outLines.add(inputStmt);

			rsultList = RulpUtil.compute(interpreter, inputStmt);
			String result = RulpUtil.toString(rsultList.results);
			outLines.add(";=>" + result);

			String output = _out.getOut();
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

		} catch (Error e) {

			e.printStackTrace();

			outLines.add(";err:");
			outLines.add(e.toString());
			outLines.add(";eof");
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
		}

		return true;
	}

	protected void _setup() {

		_interpreter = null;
		_parser = null;

		TCOUtil.reset();
		CCOUtil.reset();
		EROUtil.reset();
		OptUtil.reset();
		LCOUtil.reset();

		RulpFactory.reset();
		RuntimeUtil.reset();

		super._setup();
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

		System.out.println(input);
		System.out.println(";err");
		System.out.println();

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

}
