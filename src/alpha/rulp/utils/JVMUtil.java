package alpha.rulp.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;

public class JVMUtil {

	static Map<String, String> jvmVarMap = new HashMap<>();

	private static void _mappingJvmVar(IRFrame rootFrame, String varName, String jvmVarName)
			throws RException, IOException {

		String varVal = null;

		synchronized (jvmVarMap) {
			varVal = jvmVarMap.get(varName);
			if (varVal == null) {
				varVal = System.getProperty(jvmVarName);
				jvmVarMap.put(varName, varVal);
			}
		}

		RulpUtil.addFrameObject(rootFrame, RulpFactory.createVar(varName, RulpFactory.createString(varVal)));
	}

	public static void loadJar(String jarPath) throws RException {

		File jarFile = new File(jarPath);
		if (jarFile.exists() == false) {
			throw new RException("jar file not found.");
		}

		Method method = null;
		try {
			method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}

		boolean accessible = method.isAccessible();
		try {

			if (accessible == false) {
				method.setAccessible(true);
			}

			URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			URL url = jarFile.toURI().toURL();
			method.invoke(classLoader, url);

		} catch (Exception e) {

			e.printStackTrace();

			throw new RException(e.toString());

		} finally {

			method.setAccessible(accessible);
		}

	}

	public static void loadJVMVars(IRInterpreter interpreter, IRFrame rootFrame) throws RException, IOException {

		// Load JVM Var
		_mappingJvmVar(rootFrame, "?user.home", "user.home");
		_mappingJvmVar(rootFrame, "?user.name", "user.name");
		_mappingJvmVar(rootFrame, "?user.dir", "user.dir");
		_mappingJvmVar(rootFrame, "?file.separator", "file.separator");
		_mappingJvmVar(rootFrame, "?path.separator", "path.separator");
		_mappingJvmVar(rootFrame, "?os.name", "os.name");
		_mappingJvmVar(rootFrame, "?os.version", "os.version");
	}
}
