/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.F_OUT_TO_FILE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorOutToFile extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorOutToFile(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String outPath = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
		IROut oldOut = interpreter.getOut();

		if (RulpUtil.isTrace(frame)) {
			interpreter.out(String.format("%s: saving out to %s\n", F_OUT_TO_FILE, outPath));
		}

		int lastPos = outPath.lastIndexOf(File.separatorChar);
		if (lastPos != -1) {
			String folder = outPath.substring(0, lastPos);
			if (!new File(folder).exists()) {
				new File(folder).mkdirs();
			}
		}

		try (PrintStream out = new PrintStream(outPath)) {

			interpreter.setOutput(new IROut() {
				@Override
				public void out(String line) {
					out.print(line);
				}
			});

			return interpreter.compute(frame, args.get(2));

		} catch (FileNotFoundException e) {
			if (RulpUtil.isTrace(frame)) {
				e.printStackTrace();
			}
			throw new RException(e.toString());

		} finally {
			interpreter.setOutput(oldOut);// recovery old out
		}
	}

}