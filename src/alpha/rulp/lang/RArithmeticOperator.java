package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.O_ADD;
import static alpha.rulp.lang.Constant.O_AND;
import static alpha.rulp.lang.Constant.O_BY;
import static alpha.rulp.lang.Constant.O_DIV;
import static alpha.rulp.lang.Constant.O_MOD;
import static alpha.rulp.lang.Constant.O_NOT;
import static alpha.rulp.lang.Constant.O_OR;
import static alpha.rulp.lang.Constant.O_POWER;
import static alpha.rulp.lang.Constant.O_SUB;
import static alpha.rulp.lang.Constant.O_XOR;

public enum RArithmeticOperator {

	ADD(O_ADD), /* + */
	AND(O_AND), /* & */
	BY(O_BY), /* * */
	DIV(O_DIV), /* / */
	MOD(O_MOD), /* % */
	NOT(O_NOT), /* ~ */
	OR(O_OR), /* | */
	POWER(O_POWER), /* power */
	SUB(O_SUB), /* - */
	XOR(O_XOR); /* ^ */

	private IRAtom atom;

	private RArithmeticOperator(IRAtom atom) {
		this.atom = atom;
	}

	public IRAtom getAtom() {
		return atom;
	}
}
