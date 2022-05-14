package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alpha.rulp.lang.IRExpr;
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
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRListener1;
import alpha.rulp.utils.FormatUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorPrintImpl extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorPrintImpl(String factorName) {
		super(factorName);
	}

	static void visit(IRObject obj, IRListener1<IRObject> visitor) throws RException {

		if (obj == null) {
			return;
		}

		visitor.doAction(obj);

		switch (obj.getType()) {
		case EXPR:
		case LIST:
			IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
			while (it.hasNext()) {
				visit(it.next(), visitor);
			}
			break;

		default:
		}
	}

	static void printFunc(IRInterpreter interpreter, IRFunction func) throws RException {

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

			for (IRParaAttr para : paraList) {

				if (index++ != 0) {
					sb.append(" ");
				}

				if (para.getParaType() == O_Nil) {
					sb.append(para.getParaName() + RulpUtil.formatAttribute(para));
				} else {
					sb.append(String.format("(%s %s)", para.getParaName() + RulpUtil.formatAttribute(para),
							"" + para.getParaType()));
				}
			}
		}

		sb.append(")");

		List<String> lines = new ArrayList<>();
		lines.add(sb.toString());

		/**************************************************/
		// Output function body
		/**************************************************/
		IRExpr bodyExpr = func.getFunBody();

		// (do ...)
		if (bodyExpr.size() > 1 && RulpUtil.isFactor(bodyExpr.get(0), A_DO)) {

			IRIterator<? extends IRObject> it = bodyExpr.listIterator(1);
			while (it.hasNext()) {
				FormatUtil.format(it.next(), lines, 1);
			}

		} else {

			FormatUtil.format(func.getFunBody(), lines, 1);
		}

		/**************************************************/
		// print
		/**************************************************/
		lines.add(")" + RulpUtil.formatAttribute(func));

		for (String line : lines) {
			interpreter.out(line + "\n");
		}

		Set<IRObject> ccObjs = new HashSet<>();

		visit(func.getFunBody(), (obj) -> {

			if (obj.getType() != RType.FACTOR) {
				return;
			}

			if (!(obj instanceof IRRebuild)) {
				return;
			}

			if (ccObjs.contains(obj)) {
				return;
			}

			interpreter.out(String.format("rebuild: %s\n", ((IRRebuild) obj).getRebuildInformation()));

			ccObjs.add(obj);
		});
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
			printFunc(interpreter, (IRFunction) obj);
			break;

		default:
			throw new RException("not support object: " + obj);
		}

		return O_Nil;
	}

}
