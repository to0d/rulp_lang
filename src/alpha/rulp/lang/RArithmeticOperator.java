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

	ADD(O_ADD, 0), /* + */
	AND(O_AND, 6), /* & */
	BY(O_BY, 2), /* * */
	DIV(O_DIV, 3), /* / */
	MOD(O_MOD, 4), /* % */
	NOT(O_NOT, 8), /* ~ */
	OR(O_OR, 7), /* | */
	POWER(O_POWER, 5), /* power */
	SUB(O_SUB, 1), /* - */
	XOR(O_XOR, 9); /* ^ */

	public static final int OP_INDEX_ADD = 0;

	public static final int OP_INDEX_SUB = 1;

	public static final int OP_INDEX_BY = 2;

	public static final int OP_INDEX_DIV = 3;

	public static final int OP_INDEX_MOD = 4;

	public static final int OP_INDEX_POWER = 5;

	public static final int OP_INDEX_AND = 6;

	public static final int OP_INDEX_OR = 7;

	public static final int OP_INDEX_NOT = 8;

	public static final int OP_INDEX_XOR = 9;

	public static final int OP_COUNT = 10;

	private IRAtom atom;

	public final int index;

	private RArithmeticOperator(IRAtom atom, int index) {
		this.atom = atom;
		this.index = index;
	}

	public IRAtom getAtom() {
		return atom;
	}
}
