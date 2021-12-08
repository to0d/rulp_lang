//package alpha.rulp.ximpl.lang;
//
//import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;
//
//import alpha.rulp.lang.IRList;
//import alpha.rulp.lang.IRObject;
//import alpha.rulp.lang.RException;
//import alpha.rulp.lang.RType;
//import alpha.rulp.runtime.IRIterator;
//import alpha.rulp.utils.RulpUtil;
//
//public class XRListEmpty extends AbsAtomObject implements IRList {
//
//	static IRIterator<? extends IRObject> emptyIterator = new IRIterator<IRObject>() {
//		@Override
//		public boolean hasNext() throws RException {
//			return false;
//		}
//
//		@Override
//		public IRObject next() throws RException {
//			return null;
//		}
//	};
//
//	protected String _asString = null;
//
//	protected String _toString = null;
//
//	private String name;
//
//	public XRListEmpty(String name) {
//		super();
//		this.name = name;
//	}
//
//	@Override
//	public String asString() {
//
//		if (_asString == null) {
//
//			try {
//				_asString = RulpUtil.toString(this);
//			} catch (RException e) {
//				e.printStackTrace();
//				_asString = e.toString();
//			}
//		}
//
//		return _asString;
//	}
//
//	@Override
//	public IRObject get(int index) throws RException {
//		return null;
//	}
//
//	@Override
//	public String getNamedName() {
//		return name;
//	}
//
//	@Override
//	public RType getType() {
//		return RType.LIST;
//	}
//
//	@Override
//	public boolean isEmpty() throws RException {
//		return true;
//	}
//
//	@Override
//	public IRIterator<? extends IRObject> iterator() {
//		return emptyIterator;
//	}
//
//	@Override
//	public IRIterator<? extends IRObject> listIterator(int fromIndex) {
//		return emptyIterator;
//	}
//
//	@Override
//	public int size() throws RException {
//		return 0;
//	}
//
//	@Override
//	public String toString() {
//
//		if (_toString == null) {
//			try {
//				_toString = RulpUtil.toString(this, MAX_TOSTRING_LEN);
//			} catch (RException e) {
//				e.printStackTrace();
//				_toString = e.toString();
//			}
//		}
//
//		return _toString;
//	}
//}
