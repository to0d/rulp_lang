package alpha.rulp.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRObjectLoader;

public class LoadUtil {

	static Map<String, String> loadLineMap = new HashMap<>();

	public static IRList loadRulp(IRInterpreter interpreter, String path, String charset) throws RException {
		try {

			IRList rstList = RulpFactory.createVaryList();

			interpreter.compute(StringUtil.toOneLine(FileUtil.openTxtFile(path, charset)), (rst) -> {
				rstList.add(rst);
			});

			return rstList;

		} catch (IOException e) {
			throw new RException(e.toString());
		}
	}

	public static void loadRulpFromJar(IRInterpreter interpreter, IRFrame sysFrame, String jarPath, String charset)
			throws RException, IOException {

		String line = null;

		synchronized (loadLineMap) {

			line = loadLineMap.get(jarPath);
			if (line == null) {
				line = StringUtil.toOneLine(FileUtil.openTxtFileFromJar(jarPath, charset));
				loadLineMap.put(jarPath, line);
			}
		}

		List<IRObject> rsts = new LinkedList<>();
		for (IRObject obj : interpreter.getParser().parse(line)) {
			rsts.add(interpreter.compute(sysFrame, obj));
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadRulpLoader(String jarBinPath, String confPath) throws RException, IOException {

		for (String line : FileUtil.openTxtFile(confPath, "utf-8")) {

			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			List<String> values = StringUtil.splitStringByChar(line, ' ');
			if (values.size() != 2) {
				throw new RException("invalid conf line: " + line);
			}

			String jarName = values.get(0);
			String className = values.get(1);

			if (!jarName.endsWith(".jar")) {
				throw new RException("invalid jar name: " + line);
			}

			String fullJarPath = FileUtil.toValidPath(jarBinPath) + jarName;
			JVMUtil.loadJar(fullJarPath);

			try {

				Class<?> aClass = Class.forName(className);
				if (!IRObjectLoader.class.isAssignableFrom(aClass)) {
					throw new RException("Invalid RLoader class: " + aClass);
				}

				RulpFactory.registerLoader((Class<? extends IRObjectLoader>) aClass);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RException(e.toString());
			}
		}
	}
}
