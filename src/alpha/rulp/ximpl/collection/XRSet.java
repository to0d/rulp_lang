package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.A_SET;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRSet extends AbsRInstance {

	static final String F_MBR_SET_ADD = "_set_add";

	static final String F_MBR_SET_HAS = "_set_has";

	static final String F_MBR_SET_INIT = "_set_init";

	static final String F_MBR_SET_SIZE_OF = "_set_size_of";

	static XRSet asSet(IRObject obj) throws RException {

		if (!(obj instanceof XRSet)) {
			throw new RException("Can't convert object to set: " + obj);
		}

		return (XRSet) obj;
	}

	public static void init(IRInterpreter interpreter, IRFrame systemFrame) throws RException {

		IRClass setClass = RulpUtil.asClass(systemFrame.getEntry(A_SET).getValue());

		RulpUtil.setMember(setClass, F_MBR_SET_INIT, new AbsRFactorAdapter(F_MBR_SET_INIT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
				if (args.size() != 1) {
					throw new RException("Invalid parameters: " + args);
				}
				return RulpFactory.createInstanceOfSet(interpreter);
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(setClass, F_MBR_SET_ADD, new AbsRFactorAdapter(F_MBR_SET_ADD) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 3) {
					throw new RException("Invalid parameters: " + args);
				}

				XRSet set = asSet(interpreter.compute(frame, args.get(1)));
				IRObject obj = interpreter.compute(frame, args.get(2));

				set.add(obj);
				return O_Nil;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(setClass, F_MBR_SET_HAS, new AbsRFactorAdapter(F_MBR_SET_HAS) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 3) {
					throw new RException("Invalid parameters: " + args);
				}

				XRSet set = asSet(interpreter.compute(frame, args.get(1)));
				IRObject obj = interpreter.compute(frame, args.get(2));

				return RulpFactory.createBoolean(set.has(obj));
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(setClass, F_MBR_SET_SIZE_OF, new AbsRFactorAdapter(F_MBR_SET_SIZE_OF) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				XRSet set = asSet(interpreter.compute(frame, args.get(1)));

				return RulpFactory.createInteger(set.size());
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);
	}

	private Map<String, IRObject> uniqMap = new HashMap<>();

	public XRSet(IRClass noClass) {
		super(noClass, null, null);
	}

	public void add(IRObject newObj) throws RException {

		if (newObj == null) {
			return;
		}

		String key = RulpUtil.toUniqString(newObj);
		IRObject oldObj = uniqMap.get(key);
		if (oldObj == newObj) {
			return;
		}

		uniqMap.put(key, newObj);
		
		RulpUtil.incRef(newObj);
		RulpUtil.decRef(oldObj);
	}

	public boolean has(IRObject obj) throws RException {
		return uniqMap.containsKey(RulpUtil.toUniqString(obj));
	}

	public int size() {
		return uniqMap.size();
	}

}
