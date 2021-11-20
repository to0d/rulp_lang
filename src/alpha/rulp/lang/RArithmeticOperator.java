package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.O_ADD;
import static alpha.rulp.lang.Constant.O_BY;
import static alpha.rulp.lang.Constant.O_DIV;
import static alpha.rulp.lang.Constant.O_MOD;
import static alpha.rulp.lang.Constant.O_POWER;
import static alpha.rulp.lang.Constant.O_SUB;

public enum RArithmeticOperator {

	ADD(O_ADD), // +

	BY(O_BY), // *

	DIV(O_DIV), // divide /

	MOD(O_MOD), // %

	POWER(O_POWER), // ^

	SUB(O_SUB); // -

	private IRAtom atom;

	private RArithmeticOperator(IRAtom atom) {
		this.atom = atom;
	}

	public IRAtom getAtom() {
		return atom;
	}
}
