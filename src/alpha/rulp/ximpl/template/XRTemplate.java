package alpha.rulp.ximpl.template;

import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.T_Atom;
import static alpha.rulp.lang.Constant.T_Expr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRTemplate;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.runtime.AbsRefCallableAdapter;

public class XRTemplate extends AbsRefCallableAdapter implements IRTemplate {

	static class InputData {

		public IRObject[] actualValues;

		public IRFrame frame;

		public IRList inputArgs;

		public IRInterpreter interpreter;

		public InputData(IRList inputArgs, IRInterpreter interpreter, IRFrame frame) throws RException {
			super();
			this.inputArgs = inputArgs;
			this.interpreter = interpreter;
			this.frame = frame;
			this.actualValues = new IRObject[inputArgs.size()];
		}

		public IRObject getActualValue(int index) throws RException {

			IRObject inputPara = inputArgs.get(index);

			IRObject actualValue = actualValues[index];
			if (actualValue == null) {
				actualValue = interpreter.compute(frame, inputPara);
				actualValues[index] = actualValue;
			}

			return actualValue;
		}

		public boolean match(TemplateParaEntry tpEntry) throws RException {

			/******************************************/
			// Check parameter count
			/******************************************/
			if (tpEntry.isVary) {
				if ((tpEntry.fixedParaCount + 1) > inputArgs.size())
					return false;
			} else {
				if ((tpEntry.fixedParaCount + 1) != inputArgs.size())
					return false;
			}

			/******************************************/
			// Check fixed parameters
			/******************************************/
			if (tpEntry.fixedParaCount > 0) {

				for (int i = 0; i < tpEntry.fixedParaCount; ++i) {

					TemplatePara tp = tpEntry.fixedParas[i];

					if (!tp.isVar && (tp.paraType == O_Nil || tp.paraType == T_Atom)) {

						IRObject inputPara = inputArgs.get(i + 1);
						if (inputPara.getType() != RType.ATOM) {
							return false;
						}

						if (!RulpUtil.equal(tp.paraValue, inputPara)) {
							return false;
						}

					} else {

						// Check type
						if (!matchType(tp, i + 1)) {
							return false;
						}

						// Check value
						if (!tp.isVar && !RulpUtil.equal(tp.paraValue, getActualValue(i + 1))) {
							return false;
						}
					}
				}
			}

			return true;
		}

		public boolean matchType(TemplatePara tp, int index) throws RException {

			IRObject inputPara = inputArgs.get(index);

			// match EXPR type
			if (tp.paraType == T_Expr) {
				return inputPara.getType() == RType.EXPR;
			}

			// match result object
			return RulpUtil.matchType(tp.paraType, getActualValue(index));
		}

	}

	protected IRFrame defineFrame;

	protected String signature = null;

	protected String templateName;

	protected ArrayList<TemplateParaEntry> templateParaEntryList = new ArrayList<>();

	public XRTemplate(String templateName, IRFrame defineFrame) {
		this.templateName = templateName;
		this.defineFrame = defineFrame;
	}

	@Override
	protected void _delete() throws RException {

		Iterator<TemplateParaEntry> it = templateParaEntryList.iterator();
		while (it.hasNext()) {
			IRCallable body = it.next().body;
			it.remove();
			RulpUtil.decRef(body);
		}

		super._delete();
	}

	@Override
	public void addEntry(TemplateParaEntry entry) throws RException {

		if (entry == null || entry.body == null) {
			throw new RException("invalid entry");
		}

		this.templateParaEntryList.add(entry);
		RulpUtil.incRef(entry.body);
		this.signature = null;
	}

	@Override
	public String asString() {
		return templateName;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		TemplateParaEntry matchedTpEntry = null;

		// Search matched template entry
		{
			InputData inputData = new InputData(args, interpreter, frame);
			for (TemplateParaEntry tpEntry : templateParaEntryList) {
				if (inputData.match(tpEntry)) {
					matchedTpEntry = tpEntry;
					break;
				}
			}
		}

		if (matchedTpEntry == null) {
			throw new RException("no template match");
		}

		if (matchedTpEntry.body.getType() == RType.FUNC) {
			return RuntimeUtil.computeFun((IRFunction) matchedTpEntry.body, args, interpreter, frame);

		} else {
			return RuntimeUtil.computeCallable(matchedTpEntry.body, args, interpreter, frame);
		}
	}

	@Override
	public IRFrame getDefineFrame() {
		return defineFrame;
	}

	@Override
	public String getName() {
		return templateName;
	}

	@Override
	public String getSignature() throws RException {

		if (signature == null) {

			ArrayList<String> allSignatures = new ArrayList<>();
			for (TemplateParaEntry tpEntry : templateParaEntryList) {
				allSignatures.add(tpEntry.toString());
			}

			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(templateName);
			for (String sig : allSignatures) {
				sb.append(' ');
				sb.append(sig);
			}

			sb.append(')');
			signature = sb.toString();
		}

		return signature;
	}

	@Override
	public List<TemplateParaEntry> getTemplateParaEntryList() {
		return templateParaEntryList;
	}

	@Override
	public RType getType() {
		return RType.TEMPLATE;
	}

	public String toString() {
		return templateName;
	}
}
