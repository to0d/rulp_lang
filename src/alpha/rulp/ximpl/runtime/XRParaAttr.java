package alpha.rulp.ximpl.runtime;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.ximpl.lang.XRVar;

public class XRParaAttr extends XRVar implements IRParaAttr {

	private IRAtom paraType;

	public XRParaAttr(String paraName, IRAtom paraType) {
		super(paraName);
		this.paraType = paraType;
	}

	@Override
	public String getParaName() {
		return this.getName();
	}

	@Override
	public IRAtom getParaType() {
		return paraType;
	}

}
