/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.macro;

import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorDefMacro extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorDefMacro(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 4) {
			throw new RException("Invalid parameters: " + args);
		}

		String macroName = null;
		List<String> macroParaList = null;

		IRObject nameObj = args.get(1);
		IRObject paraObj = args.get(2);
		IRObject bodyObj = null;
		if (args.size() == 4) {
			bodyObj = args.get(3);
		} else {
			bodyObj = RulpUtil.toDoExpr(args.listIterator(3));
		}

		/*****************************************************/
		// Macro will always be defined at top frame (root/syste/main)
		/*****************************************************/
		IRFrame macroFrame = frame.getLevel() <= 2 ? frame : interpreter.getMainFrame();

		/*****************************************************/
		// Check macro name
		/*****************************************************/
		macroName = RulpUtil.asAtom(nameObj).getName();
		if (macroFrame.getEntry(macroName) != null) {
			throw new RException("Duplicated macro name: " + macroName + ", parameters: " + args);
		}

		/*****************************************************/
		// Check macro parameter list
		/*****************************************************/
		if (paraObj != null) {

			if (!RulpUtil.isPureAtomList(paraObj)) {
				throw new RException("Invalid para type: " + paraObj);

			} else {
				macroParaList = RulpUtil.toStringList(paraObj);
			}
		}

		IRMacro macro = RulpFactory.createMacro(macroName, macroParaList, RulpUtil.asExpression(bodyObj));
		macroFrame.setEntry(macroName, macro);
		return macro;
	}

}
