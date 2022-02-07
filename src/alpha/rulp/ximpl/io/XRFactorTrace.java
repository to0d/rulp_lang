package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorTrace extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorTrace(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (RulpUtil.isTrace(frame) && args.size() > 1) {

			StringBuffer sb = new StringBuffer();

			IRIterator<? extends IRObject> it = args.listIterator(1);
			while (it.hasNext()) {

				IRObject obj = interpreter.compute(frame, it.next());
				if (obj.getType() == RType.STRING) {
					sb.append(RulpUtil.asString(obj).asString());
				} else {
					sb.append(obj.toString());
				}
			}

			interpreter.out(sb.toString() + "\n");
		}

		return O_Nil;
	}

}
