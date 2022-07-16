/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.string;

public enum CharType {

	NUMBER(0), // number

	EN_CHAR(10), // English char

	EN_SYMBOL(11), // English symbol

	CN_CHAR(20), // Chinese char

	CN_SYMBOL(21), // Chinese symbol

	TW_ZHUYIN(33), // TW zhuyin

	JP_SYMBOL(41), // JP symbol

	ROMAN_NUM(53), // Roman number

	OTHER(99); // other

	public final int index;

	private CharType(int index) {
		this.index = index;
	}
}
