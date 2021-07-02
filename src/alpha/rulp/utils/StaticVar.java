package alpha.rulp.utils;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;

public class StaticVar {

	private IRObject curValue;

	private IRObject defaultValue;

	private IRVar var;

	private String varName;

	public StaticVar(String varName, IRObject value) {
		super();
		this.varName = varName;
		this.defaultValue = value;
		this.curValue = value;
	}

	public boolean getBoolValue() throws RException {
		return RulpUtil.asBoolean(curValue).asBoolean();
	}

	public IRObject getValue() {
		return curValue;
	}

	public IRVar getVar() throws RException {

		if (var == null) {

			this.var = RulpFactory.createVar(varName, defaultValue);
			this.var.addVarListener((_var, _oldVal, _newVal) -> {
				curValue = _newVal;
			});

			this.curValue = defaultValue;
		}

		return var;
	}

	public void init(IRFrame frame) throws RException {
		var = null;
		RulpUtil.addFrameObject(frame, getVar());
	}

	public void setBoolValue(boolean newVal) throws RException {

		if (var == null) {
			this.curValue = RulpFactory.createBoolean(newVal);
		} else {
			var.setValue(RulpFactory.createBoolean(newVal));
		}
	}
}
