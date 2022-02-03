//package alpha.rulp.ximpl.io;
//
//import static alpha.rulp.lang.Constant.*;
//import static alpha.rulp.lang.Constant.O_Nil;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import alpha.rulp.lang.IRFrame;
//import alpha.rulp.lang.IRList;
//import alpha.rulp.lang.IRObject;
//import alpha.rulp.lang.RException;
//import alpha.rulp.lang.RType;
//import alpha.rulp.runtime.IRFactor;
//import alpha.rulp.runtime.IRInterpreter;
//import alpha.rulp.runtime.IRIterator;
//import alpha.rulp.utils.JVMUtil;
//import alpha.rulp.utils.RulpUtil;
//import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
//
//public class XRFactorLoadJar extends AbsAtomFactorAdapter implements IRFactor {
//
//	public XRFactorLoadJar(String factorName) {
//		super(factorName);
//	}
//
//	static boolean _contain(IRList lsList, String name) throws RException {
//
//		IRIterator<? extends IRObject> it = lsList.iterator();
//		while (it.hasNext()) {
//			IRObject obj = it.next();
//			if (obj.getType() == RType.STRING && RulpUtil.asString(obj).asString().equals(name)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	static IRList _getLoadJarList(IRInterpreter interpreter) throws RException {
//		return RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_LOAD_JARS)).getValue());
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
//
//		if (args.size() != 2) {
//			throw new RException("Invalid parameters: " + args);
//		}
//
//		String jarFilePath = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
//		String className = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
//
//		String absJarFilePath = RulpUtil.lookupFile(jarFilePath, interpreter, frame);
//		if (absJarFilePath == null) {
//			throw new RException("jar not found: " + absJarFilePath);
//		}
//		
//	 
//		if (!loadedJarPaths.contains(absJarFilePath)) {
//
//			JVMUtil.loadJar(absJarFilePath);
//
////			try {
////
////				Class<?> aClass = Class.forName(className);
////				if (!IRObjectLoader.class.isAssignableFrom(aClass)) {
////					throw new RException("Invalid RLoader class: " + aClass);
////				}
////
////				Class<? extends IRObjectLoader> loaderClass = (Class<? extends IRObjectLoader>) aClass;
////				IRObjectLoader loader = loaderClass.newInstance();
////				loader.load(interpreter, frame);
////				RulpFactory.registerLoader(loaderClass);
////				
////				loadedJarPaths.add(absJarFilePath);
////
////			} catch (Exception e) {
////				e.printStackTrace();
////				throw new RException(e.toString());
////			}
//		}
//
//		return O_Nil;
//	}
//
//}
