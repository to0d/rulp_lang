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

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorNew extends AbsRFactorAdapter implements IRFactor {

	public XRFactorNew(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		// (new class1 o3 '(1 2))
		if (args.size() != 3 && args.size() != 4) {
			throw new RException("Invalid parameters: " + args);
		}

		IRClass rClass = RulpUtil.asClass(interpreter.compute(frame, args.get(1)));
		String instanceName = RulpUtil.asAtom(args.get(2)).getName();

		/******************************************/
		// Check instance exist
		/******************************************/
		IRFrameEntry oldEntry = frame.getEntry(instanceName);
		if (oldEntry != null) {
			throw new RException(String.format("duplicate object<%s> found: %s", instanceName, oldEntry.getObject()));
		}

		/******************************************/
		// Create instance
		/******************************************/
		IRInstance instance = rClass.newInstance(instanceName, args, interpreter, frame);
		RulpUtil.setMember(instance, F_MBR_THIS, instance);
		instance.init(args, interpreter, frame);

		/******************************************/
		// Add into frame
		/******************************************/
		frame.setEntry(instanceName, instance);

		return instance;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
