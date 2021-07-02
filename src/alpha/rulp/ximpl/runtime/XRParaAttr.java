package alpha.rulp.ximpl.runtime;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRParaAttr;

public class XRParaAttr implements IRParaAttr {

	private String paraName;

	private IRAtom paraType;

	public XRParaAttr(String paraName, IRAtom paraType) {
		super();
		this.paraName = paraName;
		this.paraType = paraType;
	}

	@Override
	public String getParaName() {
		return paraName;
	}

	@Override
	public IRAtom getParaType() {
		return paraType;
	}

}
