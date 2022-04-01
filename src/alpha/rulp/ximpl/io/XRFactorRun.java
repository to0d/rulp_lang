/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.F_RUN;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.error.RReturn;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorRun extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorRun(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String path = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
		String charset = "utf-8";
		if (args.size() == 3) {
			charset = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
		}

		String absPath = RulpUtil.lookupFile(path, interpreter, frame);
		if (absPath == null) {
			throw new RException("file not found: " + path);
		}

		if (RulpUtil.isTrace(frame)) {
			System.out.println("running: " + absPath);
		}

		IRFrame runFrame = RulpFactory.createFrame(frame, F_RUN);
		try {

			RulpUtil.incRef(runFrame);

			for (IRObject obj : interpreter.getParser()
					.parse(RulpUtil.toOneLine(FileUtil.openTxtFile(path, charset)))) {
				interpreter.compute(runFrame, obj);
			}

			return O_Nil;

		} catch (IOException e) {

			if (RulpUtil.isTrace(frame)) {
				e.printStackTrace();
			}

			throw new RException(e.toString());

		} catch (RReturn r) {
			return r.returnValue(frame);

		} finally {

			runFrame.release();
			RulpUtil.decRef(runFrame);
		}
	}

}