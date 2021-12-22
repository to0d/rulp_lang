package alpha.rulp.ximpl.factor;

import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.ximpl.runtime.AbsRefCallableAdapter;

public abstract class AbsRefFactorAdapter extends AbsRefCallableAdapter implements IRFactor {

	protected String factorName;

	public AbsRefFactorAdapter(String factorName) {
		super();
		this.factorName = factorName;
	}

	@Override
	public String asString() {
		return factorName;
	}

	@Override
	public String getName() {
		return factorName;
	}

	@Override
	public RType getType() {
		return RType.FACTOR;
	}

	@Override
	public boolean isConst() {
		return true;
	}

	@Override
	public boolean isStable() {
		return false;
	}

	public boolean isThreadSafe() {
		return false;
	}

	@Override
	public String toString() {
		return factorName;
	}
}