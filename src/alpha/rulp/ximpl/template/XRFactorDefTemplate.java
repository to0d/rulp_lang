package alpha.rulp.ximpl.template;

import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.T_Atom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRTemplate.TemplatePara;
import alpha.rulp.runtime.IRTemplate.TemplateParaEntry;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.function.XRFactorDefun;

public class XRFactorDefTemplate extends AbsAtomFactorAdapter implements IRFactor {

	static final String TP_TMP_VAR_NAME = "?_TTVN_";

	public static IRObject defTemplate(IRList args, String templateName, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		/*****************************************************/
		// Template parameter list
		/*****************************************************/
		List<IRParaAttr> paraAttrs = XRFactorDefun.buildAttrList(args.get(2), interpreter, frame);

		int fixedParaCount = paraAttrs.size();

		// Add entry
		TemplateParaEntry paraEntry = new TemplateParaEntry();
		paraEntry.isVary = false;
		paraEntry.fixedParaCount = fixedParaCount;
		paraEntry.fixedParas = new TemplatePara[fixedParaCount];

		// Check parameters
		if (paraAttrs.size() > 1) {

			Set<String> paraNames = new HashSet<>();

			for (int i = 0; i < fixedParaCount; ++i) {

				IRParaAttr pa = paraAttrs.get(i);

				// Check duplicated name
				String paraName = pa.getParaName();
				if (paraNames.contains(paraName)) {
					throw new RException("duplicate parameter: " + paraName);
				}
				paraNames.add(paraName);

				IRAtom paraType = pa.getParaType();
				IRObject paraValue = null;
				boolean isVar = false;

				// var para
				if (RulpUtil.isVarName(paraName)) {

					isVar = true;

				}
				// atom para
				else {

					isVar = false;

					if (paraType == null || paraType == O_Nil) {

						IRFrameEntry fixParaEntry = RuntimeUtil.lookupFrameEntry(frame, paraName);
						if (fixParaEntry != null) {

							IRObject fixParaObj = fixParaEntry.getObject();
							paraType = RulpUtil.getObjectType(fixParaObj);
							paraValue = fixParaObj;

						} else {

							paraType = T_Atom;
							paraValue = RulpFactory.createAtom(paraName);
						}

					} else if (paraType == T_Atom) {

						paraType = O_Nil;
						paraValue = RulpFactory.createAtom(paraName);

					} else {

						IRObject val = interpreter.compute(frame, RulpFactory.createAtom(paraName));
						if (RulpUtil.getObjectType(val) != pa.getParaType()) {
							throw new RException(
									String.format("unmatch parameter type: value=%s, type=%s", val, pa.getParaType()));
						}

						paraType = pa.getParaType();
						paraValue = val;
					}

					IRParaAttr newPa = RulpFactory.createParaAttr(RulpFactory.createUniqName(TP_TMP_VAR_NAME),
							paraType);
					paraAttrs.set(i, newPa);

				} // atom para

				TemplatePara tp = new TemplatePara();
				tp.isVar = isVar;
				tp.paraValue = paraValue;
				tp.paraType = paraType;

				paraEntry.fixedParas[i] = tp;

			} // for

		} // Check parameters

		/*****************************************************/
		// Template body
		/*****************************************************/
		IRExpr templateBody = null;
		if (args.size() == 4) {
			templateBody = RulpUtil.asExpression(args.get(3));

		} else if (args.size() > 4) {
			templateBody = RulpUtil.toDoExpr(args.listIterator(3));

		} else {
			throw new RException("Invalid args size: " + args.size());
		}

		paraEntry.body = RulpFactory.createFunction(frame, templateName, paraAttrs, templateBody);

		return RulpUtil.addTemplate(frame, templateName, paraEntry);
	}

	public XRFactorDefTemplate(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 4) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject nameObj = args.get(1);

		if (nameObj.getType() == RType.ATOM) {
			return defTemplate(args, RulpUtil.asAtom(args.get(1)).getName(), interpreter, frame);
		}

		if (nameObj.getType() == RType.MEMBER) {

			IRMember funMbr = RulpUtil.asMember(args.get(1));
			IRSubject sub = RulpUtil.asSubject(interpreter.compute(frame, funMbr.getSubject()));

			IRFrame templateFrame;
			if (sub.getType() == RType.FRAME) {
				templateFrame = RulpUtil.asFrame(sub);
			} else {
				templateFrame = sub.getSubjectFrame();
			}

			return defTemplate(args, funMbr.getName(), interpreter, templateFrame);
		}

		throw new RException("Invalid parameters: " + args);
	}

}
