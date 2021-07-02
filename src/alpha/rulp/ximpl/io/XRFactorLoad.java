/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.A_LOAD_PATHS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.LoadUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.SystemUtil;
import alpha.rulp.utils.TraceUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorLoad extends AbsRFactorAdapter implements IRFactor {

	private Set<String> loadedFilePaths = new HashSet<>();

	public XRFactorLoad(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String path = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();

		if (!FileUtil.isExistFile(path)) {

			if (FileUtil.isAbsPath(path)) {
				throw new RException("file not exist: " + path);
			}

			boolean find = false;

			/*************************************************/
			// Search in "load-paths"
			/*************************************************/
			IRList lpList = RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_LOAD_PATHS)).getValue());

			IRIterator<? extends IRObject> it = lpList.iterator();
			while (it.hasNext()) {

				String aPath = RulpUtil.asString(it.next()).asString();
				if (FileUtil.isExistDirectory(aPath)) {

					String newPath = FileUtil.toValidPath(aPath) + path;
					if (FileUtil.isExistFile(newPath)) {
						path = newPath;
						find = true;
						break;
					}
				}
			}

			/*************************************************/
			// Search in System env "PATH"
			/*************************************************/
			if (!find) {

				for (String aPath : SystemUtil.getEnvPaths()) {
					if (FileUtil.isExistDirectory(aPath)) {

						String newPath = FileUtil.toValidPath(aPath) + path;
						if (FileUtil.isExistFile(newPath)) {
							path = newPath;
							find = true;
							break;
						}
					}
				}
			}

			if (!find) {
				throw new RException("file not found: " + path);
			}

		}

		/*************************************************/
		// File can only be loaded once
		/*************************************************/
		if (loadedFilePaths.contains(path)) {
			return RulpFactory.createList();
		}

		String charset = args.size() == 3 ? RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString()
				: null;

		if (TraceUtil.isTrace()) {
			System.out.println("loading: " + path);
		}

		List<IRObject> rst = LoadUtil.loadRulp(interpreter, path, charset);
		loadedFilePaths.add(path);

		return RulpFactory.createList(rst);
	}

	public boolean isThreadSafe() {
		return false;
	}
}