package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRDouble;
import alpha.rulp.lang.RType;

public class XRDouble extends AbsAtomObject implements IRDouble {

	private double value;

	public XRDouble(double value) {
		super();
		this.value = value;
	}

	@Override
	public double asDouble() {
		return value;
	}

	@Override
	public String asString() {
		return "" + value + "D";
	}

	@Override
	public RType getType() {
		return RType.DOUBLE;
	}

}
