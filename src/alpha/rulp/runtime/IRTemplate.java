package alpha.rulp.runtime;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRTemplate extends IRObject, IRCallable {

	public static class TemplatePara {
		public boolean isVar;
		public IRObject paraValue;
		public IRAtom paraType;
	}

	public static class TemplateParaEntry {

		public TemplatePara[] fixedParas;
		public int fixedParaCount;
		public boolean isVary;
		public IRCallable body;
	}

	public void addEntry(TemplateParaEntry entry) throws RException;

	public String getName();
}
