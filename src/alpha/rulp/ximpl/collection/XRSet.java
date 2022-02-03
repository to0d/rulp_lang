package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.A_SET;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRSet extends AbsRInstance implements IRCollection {

	public static class Loader implements IRObjectLoader {

		@Override
		public void load(IRInterpreter interpreter, IRFrame frame) throws RException, IOException {

			IRClass setClass = RulpUtil.asClass(frame.getEntry(A_SET).getValue());

			RulpUtil.setMember(setClass, F_MBR_SET_INIT, new AbsAtomFactorAdapter(F_MBR_SET_INIT) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
					if (args.size() != 1) {
						throw new RException("Invalid parameters: " + args);
					}
					return RulpFactory.createInstanceOfSet(interpreter);
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(setClass, F_MBR_SET_ADD, new AbsAtomFactorAdapter(F_MBR_SET_ADD) {

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

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(setClass, F_MBR_SET_HAS, new AbsAtomFactorAdapter(F_MBR_SET_HAS) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 3) {
						throw new RException("Invalid parameters: " + args);
					}

					XRSet set = asSet(interpreter.compute(frame, args.get(1)));
					IRObject obj = interpreter.compute(frame, args.get(2));

					return RulpFactory.createBoolean(set.has(obj));
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(setClass, F_MBR_SET_SIZE_OF, new AbsAtomFactorAdapter(F_MBR_SET_SIZE_OF) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return RulpFactory.createInteger(asSet(interpreter.compute(frame, args.get(1))).size());
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(setClass, F_MBR_SET_IS_EMPTY, new AbsAtomFactorAdapter(F_MBR_SET_IS_EMPTY) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return RulpFactory.createBoolean(asSet(interpreter.compute(frame, args.get(1))).isEmpty());
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(setClass, F_MBR_SET_CLEAR, new AbsAtomFactorAdapter(F_MBR_SET_CLEAR) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					asSet(interpreter.compute(frame, args.get(1))).clear();
					return O_Nil;
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(setClass, F_MBR_SET_TO_LIST, new AbsAtomFactorAdapter(F_MBR_SET_TO_LIST) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return asSet(interpreter.compute(frame, args.get(1))).toList();
				}

			}, RAccessType.PRIVATE);
		}

	}

	static final String F_MBR_SET_ADD = "_set_add";

	static final String F_MBR_SET_CLEAR = "_set_clear";

	static final String F_MBR_SET_HAS = "_set_has";

	static final String F_MBR_SET_INIT = "_set_init";

	static final String F_MBR_SET_IS_EMPTY = "_set_is_empty";

	static final String F_MBR_SET_SIZE_OF = "_set_size_of";

	static final String F_MBR_SET_TO_LIST = "_set_to_list";

	static XRSet asSet(IRObject obj) throws RException {

		if (!(obj instanceof XRSet)) {
			throw new RException("Can't convert object to set: " + obj);
		}

		return (XRSet) obj;
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

	@Override
	public void clear() {
		uniqMap.clear();
	}

	public boolean has(IRObject obj) throws RException {
		return uniqMap.containsKey(RulpUtil.toUniqString(obj));
	}

	@Override
	public boolean isEmpty() {
		return uniqMap.isEmpty();
	}

	public int size() {
		return uniqMap.size();
	}

	public IRList toList() throws RException {
		return RulpFactory.createList(uniqMap.values());
	}

}
