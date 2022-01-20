/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_OPT_TCO;
import static alpha.rulp.lang.Constant.F_CPS;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.error.RReturn;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorCPS extends AbsAtomFactorAdapter implements IRRebuild, IRFactor {

	private final int id;

	private int callCount = 0;

	private int cpsCount = 0;

	public XRFactorCPS(int id) {
		super(F_CPS);
		this.id = id;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		++callCount;
		TCOUtil.incCallCount();

		IRObject rst = TCOUtil.makeCPS(RulpUtil.asExpression(args.get(1)), frame);
		if (rst.getType() == RType.EXPR) {
			AttrUtil.addAttribute(rst, A_OPT_TCO);
			++cpsCount;
		}

		throw new RReturn(this, frame, rst);
	}

	@Override
	public String getRebuildInformation() {
		return String.format("id=%d, name=%s, call=%d, expand=%d", id, getName(), callCount, cpsCount);
	}
}
