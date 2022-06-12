package alpha.rulp.runtime;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRTemplate extends IRObject, IRCallable {

	public static class TemplatePara {

		public boolean isVar;
	
		public IRAtom paraType;
		
		public IRObject paraValue;

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();

			sb.append("'(");
			if (isVar) {
				sb.append('?');
			}

			if (paraValue != null) {
				sb.append(paraValue);
			}

			if (paraType != null && paraType != O_Nil) {
				sb.append(' ');
				sb.append(paraType);
			}

			sb.append(')');

			return sb.toString();
		}
	}

	public static class TemplateParaEntry {

		public IRCallable body;

		public int fixedParaCount;

		public TemplatePara[] fixedParas;

		public boolean isVary;

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append("'(");

			for (int i = 0; i < fixedParaCount; ++i) {

				TemplatePara tp = fixedParas[i];
				if (i != 0) {
					sb.append(' ');
				}

				sb.append(tp);
			}

			if (isVary) {
				sb.append(" ?...");
			}

			sb.append(')');

			return sb.toString();
		}
	}

	public void addEntry(TemplateParaEntry entry) throws RException;

	public IRFrame getDefineFrame();

	public String getName();

	public String getSignature() throws RException;

	public List<TemplateParaEntry> getTemplateParaEntryList();
}
