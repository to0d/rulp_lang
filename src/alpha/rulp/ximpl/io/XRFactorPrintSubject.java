/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.TraceUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorPrintSubject extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorPrintSubject(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() > 3) {
			throw new RException("Invalid parameters: " + args);
		}

		if (args.size() == 1) {
			printFrame(frame, 1, interpreter);
			return O_Nil;
		}

		int prtLevel = 1;

		IRObject obj = interpreter.compute(frame, args.get(1));
		switch (obj.getType()) {
		case INT:

			if (args.size() != 2) {
				throw new RException("Invalid parameters: " + args);
			}

			prtLevel = RulpUtil.asInteger(obj).asInteger();

			printFrame(frame, prtLevel, interpreter);
			break;

		case FRAME:

			if (args.size() > 2) {
				prtLevel = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();

			}

			printFrame((IRFrame) obj, prtLevel, interpreter);
			break;

		default:

			if (obj instanceof IRSubject) {

				if (args.size() > 2) {
					prtLevel = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();

				}

				printSubject((IRSubject) obj, prtLevel, interpreter);

			} else {
				throw new RException("Invalid parameters: " + args);
			}

			break;
		}

		return O_Nil;
	}

	void printFrame(IRFrame frame, int prtLevel, IRInterpreter interpreter) throws RException {

		IRFrame curframe = frame;

		if (prtLevel < 0) {
			prtLevel = frame.getLevel() + 1;
		}

		while (prtLevel > 0) {
			interpreter.out(TraceUtil.printFrame(curframe) + "\n");
			curframe = curframe.getParentFrame();
			prtLevel--;
		}
	}

	void printSubject(IRSubject subject, int prtLevel, IRInterpreter interpreter) throws RException {

		if (prtLevel < 0) {
			prtLevel = subject.getLevel() + 1;
		}

		IRSubject curSubject = subject;

		while (prtLevel > 0 && curSubject != null) {
			interpreter.out(TraceUtil.printSubject(curSubject) + "\n");
			curSubject = curSubject.getParent();
			prtLevel--;
		}
	}

}
