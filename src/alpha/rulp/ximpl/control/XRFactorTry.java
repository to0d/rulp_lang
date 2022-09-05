/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.control;

import static alpha.rulp.lang.Constant.A_CATCH;
import static alpha.rulp.lang.Constant.C_ERROR_DEFAULT;
import static alpha.rulp.lang.Constant.C_HANDLE;
import static alpha.rulp.lang.Constant.C_HANDLE_ANY;
import static alpha.rulp.lang.Constant.F_TRY;
import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorTry extends AbsAtomFactorAdapter implements IRFactor {

	private static void _addCatchExpr(IRFrame tryFrame, IRExpr expr) throws RException {

		// (e1 (action1) (action2))
		IRAtom errId = RulpUtil.asAtom(expr.get(1));

		if (RulpUtil.isVarAtom(errId)) {

			tryFrame.setEntry(C_HANDLE_ANY, expr);

			// Save default error id
			tryFrame.setEntry(C_ERROR_DEFAULT, errId);

		} else {

			tryFrame.setEntry(C_HANDLE + errId.getName(), expr);
		}

	}

	private static boolean _isCatchExpr(IRExpr expr) throws RException {
		return expr.size() >= 3 && RulpUtil.isAtom(expr.get(0), A_CATCH) && expr.get(1).getType() == RType.ATOM;
	}

	public XRFactorTry(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRFrame tryFrame = RulpFactory.createFrame(frame, F_TRY);
		int stmtEnd = args.size() - 1;

		try {

			RulpUtil.incRef(tryFrame);
			int catchCount = 0;

			while (stmtEnd > 0) {

				IRExpr expr = RulpUtil.asExpression(args.get(stmtEnd));
				if (!_isCatchExpr(expr)) {
					break;
				}

				_addCatchExpr(tryFrame, expr);
				stmtEnd--;
				catchCount++;
			}

			if (stmtEnd == 0) {
				throw new RException("no action expr: " + args);
			}

			if (catchCount == 0) {
				throw new RException("no catch expr: " + args);
			}

			IRObject rst = O_Nil;

			for (int i = 1; i <= stmtEnd; ++i) {
				rst = interpreter.compute(tryFrame, args.get(i));
			}

			return rst;

		} catch (RError err) {

			IRObject rst = RulpUtil.handle_error(err.getError(), interpreter, tryFrame);
			if (rst == null) {
				throw err;
			}

			return rst;

		} finally {

			tryFrame.release();
			RulpUtil.decRef(tryFrame);
		}

	}
}
