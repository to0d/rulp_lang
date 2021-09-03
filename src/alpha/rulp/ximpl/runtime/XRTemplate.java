package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.T_Expr;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRTemplate;
import alpha.rulp.utils.RulpUtil;

public class XRTemplate extends AbsAtomCallableAdapter implements IRTemplate {

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

	@Override
	public List<TemplateParaEntry> getTemplateParaEntryList() {
		return templateParaEntryList;
	}

	public XRTemplate(String templateName, IRFrame defineFrame) {
		this.templateName = templateName;
		this.defineFrame = defineFrame;
	}

	@Override
	public void addEntry(TemplateParaEntry entry) throws RException {

		if (entry == null || entry.body == null) {
			throw new RException("invalid entry");
		}

		this.templateParaEntryList.add(entry);
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

		return matchedTpEntry.body.compute(args, interpreter, frame);
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
	public RType getType() {
		return RType.TEMPLATE;
	}

	@Override
	public boolean isStable() {
		return false;
	}

	public boolean isThreadSafe() {
		return false;
	}

	public String toString() {
		return templateName;
	}
}
