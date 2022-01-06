package alpha.rulp.ximpl.attribute;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class ThreadSafeUtil {

	public static boolean isThreadSafe(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {
		case FACTOR:
		case FUNC:
		case MACRO:
		case TEMPLATE:
			return ((IRCallable) obj).isThreadSafe();

		case ATOM:

			String atomName = RulpUtil.asAtom(obj).getName();

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, atomName);
			// is pure atom
			if (entry == null) {
				return true;
			}

			IRObject entryValue = entry.getObject();
			if (entryValue == null || entryValue.getType() == RType.ATOM) {
				return true;
			}

			return isThreadSafe(entryValue, frame);

		default:
			return false;

		}
	}
}
