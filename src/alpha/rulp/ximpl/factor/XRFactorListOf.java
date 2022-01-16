/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRFactorListOf extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorListOf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() > 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRSubject sub = frame;
		RType type = null;

		if (args.size() >= 2) {
			sub = RulpUtil.asSubject(interpreter.compute(frame, args.get(1)));
		}

		if (args.size() >= 3) {
			type = RType.toType(RulpUtil.asAtom(args.get(2)).getName());
			if (type == null) {
				throw new RException("Unknow type:" + args.get(2));
			}
		}

		Collection<? extends IRMember> mbrs = sub.listMembers();
		if (mbrs.isEmpty()) {
			return RulpFactory.emptyConstList();
		}

		List<IRMember> list = new LinkedList<>();
		if (type == null) {

			list.addAll(mbrs);

		} else {

			for (IRMember mbr : mbrs) {
				IRObject obj = mbr.getValue();
				if (obj == null || obj.getType() != type) {
					continue;
				}

				list.add(mbr);
			}
		}

		Collections.sort(list, (a, b) -> {
			return a.getName().compareTo(b.getName());
		});

		return RulpFactory.createList(list);
	}

}
