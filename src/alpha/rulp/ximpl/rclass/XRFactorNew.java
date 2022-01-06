/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.F_MBR_THIS;
import static alpha.rulp.lang.Constant.F_O_MBR;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRInstance;
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
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorNew extends AbsAtomFactorAdapter implements IRFactor {

	public static IRInstance newInstance(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		// (new class1 o3 '(1 2))
		// (new class1 o3))
		// (new class1 '(1 2))
		// (new class1)
		if (args.size() > 4) {
			throw new RException("Invalid parameters: " + args);
		}

		int argIndex = 1;
		IRObject argObj = null;
		IRFrame definedFrame = frame;

		/******************************************/
		// Class
		/******************************************/
		IRClass rClass = RulpUtil.asClass(interpreter.compute(frame, args.get(argIndex++)));

		/******************************************/
		// Instance Name
		/******************************************/
		String instanceName = null;
		if (argIndex < args.size()) {

			argObj = args.get(argIndex);

			switch (argObj.getType()) {
			case ATOM:
				instanceName = RulpUtil.asAtom(argObj).getName();
				++argIndex;
				break;

			// Create instance in the frame of the specified subject
			case MEMBER:

				IRMember mbr = RulpUtil.asMember(argObj);

				IRObject subObj = RuntimeUtil.compute(mbr.getSubject(), interpreter, frame);
				if (subObj == null) {
					throw new RException("subject<" + mbr.getSubject() + "> not found");
				} else {
					IRSubject sub = RulpUtil.asSubject(subObj);
					definedFrame = sub.getSubjectFrame();
					instanceName = mbr.getName();
					++argIndex;
				}

				break;

			// Create instance in member expression: '(:: sub mbr)
			case EXPR:

				IRExpr subExpr = RulpUtil.asExpression(argObj);
				IRObject e0 = subExpr.get(0);

				if (e0.asString().equals(F_O_MBR) && subExpr.size() == 3
						&& (e0.getType() == RType.ATOM || e0.getType() == RType.FACTOR)
						&& subExpr.get(2).getType() == RType.ATOM) {

					IRSubject sub = RulpUtil.asSubject(interpreter.compute(frame, subExpr.get(1)));
					definedFrame = sub.getSubjectFrame();
					instanceName = RulpUtil.asAtom(subExpr.get(2)).getName();
					++argIndex;
				}

				break;
			}

		}

		/******************************************/
		// argument list
		/******************************************/
		IRList initArgs = null;
		if (argIndex < args.size()) {
			initArgs = RulpUtil.asList(interpreter.compute(frame, args.get(argIndex++)));

			if (argIndex != args.size()) {
				throw new RException("Invalid parameters: " + args);
			}
		} else {
			initArgs = RulpFactory.emptyConstList();
		}

		/******************************************/
		// Create instance
		/******************************************/
		IRInstance instance = rClass.newInstance(instanceName, args, interpreter, definedFrame);
		RulpUtil.setMember(instance, F_MBR_THIS, instance);

		/******************************************/
		// Call Initialization member
		/******************************************/
		instance.init(initArgs, interpreter, frame);

		/******************************************/
		// Add into frame
		/******************************************/
		if (instanceName != null) {

			// Check instance exist
			IRFrameEntry oldEntry = definedFrame.getEntry(instanceName);
			if (oldEntry != null && oldEntry.getFrame() == definedFrame) {
				throw new RException(
						String.format("duplicate object<%s> found: %s", instanceName, oldEntry.getObject()));
			}

			definedFrame.setEntry(instanceName, instance);
		}

		return instance;
	}

	public XRFactorNew(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
		return newInstance(args, interpreter, frame);
	}

}
