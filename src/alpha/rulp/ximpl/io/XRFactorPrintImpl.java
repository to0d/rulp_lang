package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IROut;
import alpha.rulp.utils.FormatUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorPrintImpl extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorPrintImpl(String factorName) {
		super(factorName);
	}

	static void printFunc(IROut out, int level) throws RException {

		//
		if (level == -1) {

		}
	}

	static void printFunc(IROut out, IRFunction func) throws RException {

		if (func.isList()) {
			throw new RException("not support list function: " + func);
		}

		if (func.isLambda()) {
			throw new RException("not support lambda function: " + func);
		}

		StringBuffer sb = new StringBuffer();
		sb.append(String.format("(%s %s (", F_DEFUN, func.getName()));

		List<IRParaAttr> paraList = func.getParaAttrs();
		int index = 0;
		if (paraList != null && !paraList.isEmpty()) {
			if (index++ != 0) {
				sb.append(" ");
			}
			for (IRParaAttr para : paraList) {
				if (para.getParaType() == O_Nil) {
					sb.append(para.getParaName());
				} else {
					sb.append(String.format("(%s %s)", para.getParaName(), "" + para.getParaType()));
				}
			}
		}

		sb.append(")");

		List<String> lines = new ArrayList<>();
		lines.add(sb.toString());
		FormatUtil.format(func.getFunBody(), lines, 1);
		lines.add(")");

		for (String line : lines) {
			out.out(line + "\n");
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(1);
		if (obj.getType() == RType.ATOM) {
			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(obj).getName());
			if (entry != null) {
				obj = entry.getValue();
			}
		}

		switch (obj.getType()) {
		case ATOM:
			throw new RException("object not found: " + obj);

		case FUNC:
			printFunc(interpreter.getOut(), (IRFunction) obj);
			break;

		default:
			throw new RException("not support object: " + obj);

		}

		return O_Nil;
	}

	@Override
	public boolean isStable() {
		return false;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
