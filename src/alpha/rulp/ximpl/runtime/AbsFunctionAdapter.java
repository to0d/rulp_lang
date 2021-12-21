package alpha.rulp.ximpl.runtime;

import alpha.rulp.runtime.IRFunction;

public abstract class AbsFunctionAdapter extends AbsRefCallableAdapter implements IRFunction {

	protected Boolean isStable = null;

	public Boolean getIsStable() {
		return isStable;
	}

	public void setIsStable(Boolean isStable) {
		this.isStable = isStable;
	}

}
