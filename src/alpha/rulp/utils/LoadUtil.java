/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.A_LOAD_CLASS;
import static alpha.rulp.lang.Constant.A_LOAD_JAR;
import static alpha.rulp.lang.Constant.A_LOAD_SCRIPT;
//import static alpha.rulp.lang.Constant.A_LOAD_SYSTEM;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRObjectLoader;

public class LoadUtil {

	static Map<String, String> loadLineMap = new HashMap<>();

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

	public static void loadClass(IRObjectLoader loader, IRInterpreter interpreter, IRFrame frame)
			throws RException, IOException {

		String className = loader.getClass().getName();
		IRList ldList = RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_LOAD_CLASS)).getValue());

		// Script can only be loaded once
		if (_contain(ldList, className)) {
			return;
		}

		if (RulpUtil.isTrace(frame)) {
			System.out.println("loading class: " + className);
		}

		loader.load(interpreter, frame);
		ldList.add(RulpFactory.createString(className));
	}

	public static void loadJar(IRInterpreter interpreter, IRFrame frame, String path) throws RException {

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

		if (RulpUtil.isTrace(frame)) {
			System.out.println("loading jar: " + absJarPath);
		}

		JVMUtil.loadJar(absJarPath);

		lsList.add(RulpFactory.createString(absJarPath));
	}

	public static IRList loadRulp(IRInterpreter interpreter, String path, String charset) throws RException {

		try {

			IRList rstList = RulpFactory.createVaryList();

			interpreter.compute(RulpUtil.toOneLine(FileUtil.openTxtFile(path, charset)), (rst) -> {
				rstList.add(rst);
			});

			return rstList;

		} catch (IOException e) {
			throw new RException(e.toString());
		}
	}

	public static void loadRulpFromJar(IRInterpreter interpreter, IRFrame frame, String jarPath, String charset)
			throws RException, IOException {

		String line = null;

		synchronized (loadLineMap) {
			line = loadLineMap.get(jarPath);
			if (line == null) {

				if (!JarUtil.containFileInJar(jarPath)) {
					throw new RException("jar file not found: " + jarPath);
				}

				line = RulpUtil.toOneLine(JarUtil.openTxtFileFromJar(jarPath, charset));
				loadLineMap.put(jarPath, line);
			}
		}

		List<IRObject> rsts = new LinkedList<>();

		for (IRObject obj : interpreter.getParser().parse(line)) {
			rsts.add(interpreter.compute(frame, obj));
		}
	}

	public static void loadScript(IRInterpreter interpreter, IRFrame frame, String path, String charset)
			throws RException {

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

		if (RulpUtil.isTrace(frame)) {
			System.out.println("loading script: " + absPath);
		}

		LoadUtil.loadRulp(interpreter, absPath, charset);

		lsList.add(RulpFactory.createString(absPath));
	}

}
