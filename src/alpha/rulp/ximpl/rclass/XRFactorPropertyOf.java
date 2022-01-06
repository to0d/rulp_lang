/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.O_Final;
import static alpha.rulp.lang.Constant.O_Static;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorPropertyOf extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorPropertyOf(String factorName) {
		super(factorName);
	}

	static IRObject getPropertyList(IRObject obj) throws RException {

		List<IRObject> properties = new ArrayList<>();

		switch (obj.getType()) {
		case MEMBER:

			IRMember mbr = RulpUtil.asMember(obj);
			properties.add(RAccessType.toObject(mbr.getAccessType()));

			if (RulpUtil.isPropertyFinal(mbr)) {
				properties.add(O_Final);
			}

			if (RulpUtil.isPropertyStatic(mbr)) {
				properties.add(O_Static);
			}

			break;

		case CLASS:
			IRClass rclass = RulpUtil.asClass(obj);
			if (rclass.isFinal()) {
				properties.add(O_Final);
			}

			break;

		default:
		}

		return RulpFactory.createList(properties);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(1);
		switch (obj.getType()) {
		case MEMBER:
			obj = RuntimeUtil.getActualMember((IRMember) obj, interpreter, frame);
			break;

		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtil.asAtom(obj).getName());
			if (entry != null) {
				obj = entry.getValue();
			}

			break;

		default:
		}

		return getPropertyList(obj);
	}

}
