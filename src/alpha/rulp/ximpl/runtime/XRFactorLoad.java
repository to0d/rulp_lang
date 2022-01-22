/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.HashSet;
import java.util.Set;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.LoadUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorLoad extends AbsAtomFactorAdapter implements IRFactor {

	private Set<String> loadedScriptPaths = new HashSet<>();

	public XRFactorLoad(String factorName) {
		super(factorName);
	}

	private void _load_system_script(String name, IRInterpreter interpreter, IRFrame frame) throws RException {

		/*************************************************/
		// Script can only be loaded once
		/*************************************************/
		if (loadedScriptPaths.contains(name)) {
			return;
		}

		LoadUtil.loadSystem(interpreter, frame, name);
		loadedScriptPaths.add(name);
	}

	private void _load_user_script(String path, IRList args, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String charset = null;
		if (args.size() == 3) {
			charset = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
		}

		String absPath = RulpUtil.lookupFile(path, interpreter, frame);
		if (absPath == null) {
			throw new RException("file not found: " + path);
		}

		/*************************************************/
		// Script can only be loaded once
		/*************************************************/
		if (loadedScriptPaths.contains(absPath)) {
			return;
		}

		if (RuntimeUtil.isTrace(frame)) {
			System.out.println("loading: " + absPath);
		}

		LoadUtil.loadRulp(interpreter, absPath, charset);
		loadedScriptPaths.add(absPath);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject loadObj = interpreter.compute(frame, args.get(1));

		switch (loadObj.getType()) {
		case STRING:
			_load_user_script(RulpUtil.asString(loadObj).asString(), args, interpreter, frame);
			break;

		case ATOM:
			_load_system_script(RulpUtil.asAtom(loadObj).getName(), interpreter, frame);
			break;

		case CLASS:
			_load_system_script(RulpUtil.asClass(loadObj).getClassName(), interpreter, frame);
			break;

		default:
			throw new RException("Invalid parameters: " + args);
		}

		return O_Nil;
	}

}