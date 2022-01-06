/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.control;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.A_FROM;
import static alpha.rulp.lang.Constant.F_FOR;
import static alpha.rulp.lang.Constant.F_IN;
import static alpha.rulp.lang.Constant.F_TO;
import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.error.RBreak;
import alpha.rulp.ximpl.error.RContinue;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorLoop extends AbsAtomFactorAdapter implements IRFactor {

	public static IRIterator<? extends IRObject> getLoop2DoList(IRList args) throws RException {
		return args.listIterator(8);
	}

	public static IRObject getLoop2FromObject(IRList args) throws RException {
		return args.get(4);
	}

	public static IRObject getLoop2ToObject(IRList args) throws RException {
		return args.get(6);
	}

	static void loop1(IRList args, IRInterpreter interpreter, IRFrame loopFrame) throws RException {

		String indexName = RulpUtil.asAtom(args.get(2)).getName();
		IRList values = RulpUtil.asList(interpreter.compute(loopFrame, args.get(4)));

		IRIterator<? extends IRObject> valIter = values.iterator();
		OUT_LOOP: while (valIter.hasNext()) {

			loopFrame.setEntry(indexName, valIter.next());
			IRIterator<? extends IRObject> argIter = args.listIterator(6);

			IRFrame loopDoFrame = RulpFactory.createFrame(loopFrame, "LOOP-DO");
			RulpUtil.incRef(loopDoFrame);

			try {

				while (argIter.hasNext()) {
					IRObject expr = argIter.next();
					if (expr.getType() != RType.EXPR) {
						throw new RException("Invalid expr in loop: " + expr);
					}
					interpreter.compute(loopDoFrame, expr);
				}

			} catch (RBreak r) {
				break OUT_LOOP;
			} catch (RContinue c) {
				continue OUT_LOOP;
			} finally {
				loopDoFrame.release();
				RulpUtil.decRef(loopDoFrame);
			}
		}
	}

	static void loop2(IRList args, IRInterpreter interpreter, IRFrame loopFrame) throws RException {

		String indexName = RulpUtil.asAtom(args.get(2)).getName();
		int fromIndex = MathUtil.toInt(RulpUtil.asInteger(interpreter.compute(loopFrame, getLoop2FromObject(args))));
		int toIndex = MathUtil.toInt(RulpUtil.asInteger(interpreter.compute(loopFrame, getLoop2ToObject(args))));

		OUT_LOOP: for (int i = fromIndex; i <= toIndex; ++i) {

			loopFrame.setEntry(indexName, RulpFactory.createInteger(i));
			IRIterator<? extends IRObject> iter = getLoop2DoList(args);

			IRFrame loopDoFrame = RulpFactory.createFrame(loopFrame, "LOOP-DO");
			RulpUtil.incRef(loopDoFrame);

			try {

				while (iter.hasNext()) {

					IRObject expr = iter.next();
					if (expr.getType() != RType.EXPR) {
						throw new RException("Invalid expr in loop: " + expr);
					}
					interpreter.compute(loopDoFrame, expr);
				}

			} catch (RBreak r) {
				break OUT_LOOP;

			} catch (RContinue c) {
				continue OUT_LOOP;

			} finally {
				loopDoFrame.release();
				RulpUtil.decRef(loopDoFrame);
			}
		}
	}

	static void loop3(IRList args, IRInterpreter interpreter, IRFrame loopFrame) throws RException {

		IRIterator<? extends IRObject> iter = null;

		IRFrame loopDoFrame = null;

		try {

			OUT_LOOP: while (true) {

				if (iter == null || !iter.hasNext()) {

					iter = args.listIterator(1); // skip head

					if (loopDoFrame != null) {
						loopDoFrame.release();
						RulpUtil.decRef(loopDoFrame);
					}

					loopDoFrame = RulpFactory.createFrame(loopFrame, "LOOP-DO");
					RulpUtil.incRef(loopDoFrame);
				}

				IRObject expr = iter.next();
				if (expr.getType() != RType.EXPR) {
					throw new RException("Invalid expr in loop: " + expr);
				}

				try {
					interpreter.compute(loopDoFrame, expr);
				} catch (RBreak r) {
					break OUT_LOOP;
				} catch (RContinue c) {
					continue OUT_LOOP;
				}
			}

		} finally {

			if (loopDoFrame != null) {
				loopDoFrame.release();
				RulpUtil.decRef(loopDoFrame);
			}
		}
	}

	public XRFactorLoop(String factorName) {
		super(factorName);
	}

	public static boolean isLoop2(IRList args) throws RException {

		// (loop for x from 1 to 3 do ...)
		return args.size() >= 9 && RulpUtil.isAtom(args.get(1), F_FOR) && RulpUtil.isAtom(args.get(2))
				&& RulpUtil.isAtom(args.get(3), A_FROM) && RulpUtil.isAtom(args.get(5), F_TO)
				&& RulpUtil.isAtom(args.get(7), A_DO);
	}

	public static boolean isLoop1(IRList args) throws RException {

		// (loop for x in '(1 2 3) do ...)
		return args.size() >= 7 && RulpUtil.isAtom(args.get(1), F_FOR) && RulpUtil.isAtom(args.get(2))
				&& RulpUtil.isAtom(args.get(3), F_IN) && RulpUtil.isAtom(args.get(5), A_DO);

	}

	public static boolean isLoop3(IRList args) throws RException {
		return args.size() == 1 || args.get(1).getType() == RType.EXPR;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRFrame loopFrame = RulpFactory.createFrame(frame, "LOOP");
		RulpUtil.incRef(loopFrame);

		try {

			// (loop for x in '(1 2 3) do (print x))
			if (isLoop1(args)) {
				loop1(args, interpreter, loopFrame);
				return O_Nil;
			}

			// (loop for x from 1 to 3 do ...)
			if (isLoop2(args)) {
				loop2(args, interpreter, loopFrame);
				return O_Nil;
			}

			// (loop stmt1 ...)
			if (isLoop3(args)) {
				loop3(args, interpreter, loopFrame);
				return O_Nil;
			}

			throw new RException("Invalid loop expr: " + args);

		} finally {

			loopFrame.release();
			RulpUtil.decRef(loopFrame);
		}
	}

}
