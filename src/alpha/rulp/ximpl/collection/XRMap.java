package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.A_MAP;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMap;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;
import alpha.rulp.ximpl.rclass.XRDefInstance;

public class XRMap extends XRDefInstance implements IRCollection {

	static class RMapEntry implements Entry<IRObject, IRObject> {

		IRObject key;
		IRObject value;

		public RMapEntry(IRObject key, IRObject value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public IRObject getKey() {
			return key;
		}

		@Override
		public IRObject getValue() {
			return value;
		}

		@Override
		public IRObject setValue(IRObject value) {
			this.value = value;
			return value;
		}
	}

	static class RRMap implements IRMap {

		XRMap rMap;

		public RRMap(XRMap rMap) {
			super();
			this.rMap = rMap;
		}

		@Override
		public void clear() {
			throw new RuntimeException("not support");
		}

		@Override
		public boolean containsKey(Object key) {

			try {
				return rMap.containsKey((IRObject) key);

			} catch (RException e) {

				if (TRACE) {
					e.printStackTrace();
				}
				throw new RuntimeException(e.toString());
			}
		}

		@Override
		public boolean containsValue(Object value) {
			throw new RuntimeException("not support");
		}

		@Override
		public Set<Entry<IRObject, IRObject>> entrySet() {
			return new HashSet<>(rMap.uniqMap.values());
		}

		@Override
		public IRObject get(Object key) {
			try {
				return rMap.get((IRObject) key);
			} catch (RException e) {
				if (TRACE) {
					e.printStackTrace();
				}
				throw new RuntimeException(e.toString());
			}
		}

		@Override
		public boolean isEmpty() {
			return rMap.size() == 0;
		}

		@Override
		public Set<IRObject> keySet() {
			throw new RuntimeException("not support");
		}

		@Override
		public IRObject put(IRObject key, IRObject value) {

			try {
				rMap.put(key, value);
				return value;
			} catch (RException e) {
				if (TRACE) {
					e.printStackTrace();
				}
				throw new RuntimeException(e.toString());
			}
		}

		@Override
		public void putAll(Map<? extends IRObject, ? extends IRObject> m) {
			throw new RuntimeException("not support");
		}

		@Override
		public IRObject remove(Object key) {
			throw new RuntimeException("not support");
		}

		@Override
		public int size() {
			return rMap.size();
		}

		@Override
		public Collection<IRObject> values() {
			throw new RuntimeException("not support");
		}

	}

	static final String A_MBR_MAP_IMPL = "?impl";

	static final String F_MBR_MAP_GET = "_map_get";

	static final String F_MBR_MAP_INIT = "_map_init";

	static final String F_MBR_MAP_PUT = "_map_put";

	static final String F_MBR_MAP_SIZE_OF = "_map_size_of";

	static final String F_MBR_MAP_IS_EMPTY = "_map_is_empty";

	static final String F_MBR_MAP_CLEAR = "_map_clear";

	public static boolean TRACE = false;

	public static void init(IRInterpreter interpreter, IRFrame systemFrame) throws RException {

		IRClass mapClass = RulpUtil.asClass(systemFrame.getEntry(A_MAP).getValue());

		RulpUtil.setMember(mapClass, F_MBR_MAP_INIT, new AbsRFactorAdapter(F_MBR_MAP_INIT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 1) {
					throw new RException("Invalid parameters: " + args);
				}

				return RulpFactory.createInstanceOfMap(interpreter);
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_MAP_PUT, new AbsRFactorAdapter(F_MBR_MAP_PUT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 4) {
					throw new RException("Invalid parameters: " + args);
				}

				XRMap map = RulpUtil.asMap(interpreter.compute(frame, args.get(1)));
				IRObject key = interpreter.compute(frame, args.get(2));
				IRObject value = interpreter.compute(frame, args.get(3));
				map.put(key, value);
				return O_Nil;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_MAP_GET, new AbsRFactorAdapter(F_MBR_MAP_GET) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 3) {
					throw new RException("Invalid parameters: " + args);
				}

				XRMap map = RulpUtil.asMap(interpreter.compute(frame, args.get(1)));
				IRObject key = interpreter.compute(frame, args.get(2));
				IRObject value = map.get(key);
				return value == null ? O_Nil : value;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_MAP_SIZE_OF, new AbsRFactorAdapter(F_MBR_MAP_SIZE_OF) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				return RulpFactory.createInteger(RulpUtil.asMap(interpreter.compute(frame, args.get(1))).size());
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_MAP_IS_EMPTY, new AbsRFactorAdapter(F_MBR_MAP_IS_EMPTY) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				return RulpFactory.createBoolean(RulpUtil.asMap(interpreter.compute(frame, args.get(1))).isEmpty());
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_MAP_CLEAR, new AbsRFactorAdapter(F_MBR_MAP_CLEAR) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				RulpUtil.asMap(interpreter.compute(frame, args.get(1))).clear();

				return O_Nil;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}

		}, RAccessType.PRIVATE);
	}

	public static IRMap toImplMap(IRInstance instance) throws RException {

		if (!A_MAP.equals(instance.getRClass().getClassName())) {
			throw new RException("not map instance: class=" + instance.getRClass().getClassName());
		}

		IRMember implMbr = instance.getMember(A_MBR_MAP_IMPL);
		if (implMbr == null) {
			throw new RException("impl not found: instance=" + instance.getInstanceName());
		}

		IRVar var = RulpUtil.asVar(implMbr.getValue());
		if (var.getValue() == null) {
			throw new RException("impl not init: instance=" + instance.getInstanceName());
		}

		IRObject val = var.getValue();
		if (!(val instanceof XRMap)) {
			throw new RException("invalid impl class: " + var.getClass());
		}

		return new RRMap((XRMap) val);
	}

	private Map<String, RMapEntry> uniqMap = new HashMap<>();

	public XRMap(IRClass noClass) {
		super(noClass, null, null);
	}

	public boolean containsKey(IRObject key) throws RException {
		return uniqMap.containsKey(RulpUtil.toUniqString(key));
	}

	public IRObject get(IRObject key) throws RException {
		String uniName = RulpUtil.toUniqString(key);
		RMapEntry entry = uniqMap.get(uniName);
		return entry == null ? null : entry.value;
	}

	public void put(IRObject key, IRObject value) throws RException {
		String uniName = RulpUtil.toUniqString(key);
		uniqMap.put(uniName, new RMapEntry(key, value));
	}

	public int size() {
		return uniqMap.size();
	}

	@Override
	public void clear() {
		uniqMap.clear();
	}

	@Override
	public boolean isEmpty() {
		return uniqMap.isEmpty();
	}

}
