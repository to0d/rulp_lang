/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.string;

import java.util.ArrayList;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.StringUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorStrMatch extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorStrMatch(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3 && args.size() != 4) {
			throw new RException("Invalid parameters: " + args);
		}

		String mode = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
		String content = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
		boolean rc;

		if (args.size() == 3) {
			rc = StringUtil.matchFormat(mode, content);

		} else {

			IRList outObjs = RulpUtil.asList(args.get(3));
			if (outObjs.isEmpty()) {
				throw new RException("Invalid parameters: " + args);
			}

			ArrayList<IRVar> outVars = new ArrayList<>();
			for (IRObject var : RulpUtil.toList(outObjs.iterator())) {

				if (var.getType() == RType.ATOM) {

					IRFrameEntry entry = frame.getEntry(RulpUtil.asAtom(var).getName());
					if (entry == null) {
						throw new RException("var not found: " + var);
					}

					if (entry.getObject().getType() != RType.VAR) {
						throw new RException("not var: " + entry.getObject());
					}

					outVars.add(RulpUtil.asVar(entry.getObject()));

				} else {
					outVars.add(RulpUtil.asVar(interpreter.compute(frame, var)));
				}

			}

			ArrayList<String> values = new ArrayList<>();

			rc = StringUtil.matchFormat(mode, content, values);

			if (values.size() != outVars.size()) {
				return RulpFactory.createBoolean(false);
			}

			for (int i = 0; i < values.size(); ++i) {
				outVars.get(i).setValue(RulpFactory.createString(values.get(i)));
			}
		}

		return RulpFactory.createBoolean(rc);
	}

}