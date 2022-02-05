package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.A_LOAD_CLASS;
import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorLoadClass extends AbsAtomFactorAdapter implements IRFactor {

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

	static IRList _getLoaderList(IRInterpreter interpreter) throws RException {
		return RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_LOAD_CLASS)).getValue());
	}

	public XRFactorLoadClass(String factorName) {
		super(factorName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		String loaderName = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();

		IRList ldList = _getLoaderList(interpreter);

		/*************************************************/
		// Script can only be loaded once
		/*************************************************/
		if (!_contain(ldList, loaderName)) {

			if (RulpUtil.isTrace(frame)) {
				System.out.println("loading class: " + loaderName);
			}

			try {

				Class<?> aClass = Class.forName(loaderName);
				if (!IRObjectLoader.class.isAssignableFrom(aClass)) {
					throw new RException("Invalid RLoader class: " + aClass);
				}

				Class<? extends IRObjectLoader> loaderClass = (Class<? extends IRObjectLoader>) aClass;
				IRObjectLoader loader = loaderClass.newInstance();
				loader.load(interpreter, frame);

				ldList.add(RulpFactory.createString(loaderName));

			} catch (Exception e) {
				e.printStackTrace();
				throw new RException(e.toString());
			}

		}

		return O_Nil;
	}

}
