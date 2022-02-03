/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.A_LOAD_JAR;
import static alpha.rulp.lang.Constant.A_LOAD_SCRIPT;
import static alpha.rulp.lang.Constant.A_LOAD_SYSTEM;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.JVMUtil;
import alpha.rulp.utils.LoadUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorLoad extends AbsAtomFactorAdapter implements IRFactor {

	static boolean _contain(IRList lsList, String name) throws RException {

		IRIterator<? extends IRObject> it = lsList.iterator();
		while (it.hasNext()) {
			IRObject obj = it.next();
			if (obj.getType() == RType.STRING && RulpUtil.asString(obj).asString().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public XRFactorLoad(String factorName) {
		super(factorName);
	}

	private void _load_jar(String path, IRInterpreter interpreter, IRFrame frame) throws RException {

		String absJarPath = RulpUtil.lookupFile(path, interpreter, frame);
		if (absJarPath == null) {
			throw new RException("jar not found: " + path);
		}

		IRList lsList = RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_LOAD_JAR)).getValue());

		/*************************************************/
		// Script can only be loaded once
		/*************************************************/
		if (_contain(lsList, absJarPath)) {
			return;
		}

		if (RuntimeUtil.isTrace(frame)) {
			System.out.println("loading jar: " + absJarPath);
		}

		JVMUtil.loadJar(absJarPath);

		lsList.add(RulpFactory.createString(absJarPath));
	}

	private void _load_script(String path, String charset, IRInterpreter interpreter, IRFrame frame) throws RException {

		String absPath = RulpUtil.lookupFile(path, interpreter, frame);
		if (absPath == null) {
			throw new RException("file not found: " + path);
		}

		IRList lsList = RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_LOAD_SCRIPT)).getValue());

		/*************************************************/
		// Script can only be loaded once
		/*************************************************/
		if (_contain(lsList, absPath)) {
			return;
		}

		if (RuntimeUtil.isTrace(frame)) {
			System.out.println("loading script: " + absPath);
		}

		LoadUtil.loadRulp(interpreter, absPath, charset);

		lsList.add(RulpFactory.createString(absPath));
	}

	private void _load_system(String name, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRList lsList = RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_LOAD_SYSTEM)).getValue());

		/*************************************************/
		// Script can only be loaded once
		/*************************************************/
		if (_contain(lsList, name)) {
			return;
		}

		String jarPath = "alpha/resource/" + name + ".rulp";

		try {

			if (RuntimeUtil.isTrace(frame)) {
				System.out.println("loading system: " + jarPath);
			}

			LoadUtil.loadRulpFromJar(interpreter, frame, jarPath, "utf-8");
//			if (loader != null) {
//				loader.load(interpreter, frame);
//			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new RException("fail to load <" + name + ">, err:" + e.toString());
		}

//		LoadUtil.loadSystem(interpreter, frame, name);
		lsList.add(RulpFactory.createString(name));
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject loadObj = interpreter.compute(frame, args.get(1));

		switch (loadObj.getType()) {
		case STRING:

			String charset = null;
			if (args.size() == 3) {
				charset = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
			}

			String name = RulpUtil.asString(loadObj).asString();
			if (name.endsWith(".jar")) {
				_load_jar(name, interpreter, frame);
			} else {
				_load_script(name, charset, interpreter, frame);
			}

			break;

		case ATOM:
			_load_system(RulpUtil.asAtom(loadObj).getName(), interpreter, frame);
			break;

		case CLASS:
			_load_system(RulpUtil.asClass(loadObj).getClassName(), interpreter, frame);
			break;

		default:
			throw new RException("Invalid parameters: " + args);
		}

		return O_Nil;
	}

}