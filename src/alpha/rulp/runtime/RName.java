/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.runtime;

public class RName {

	public final String fullName;

	public final String nameSpace;

	public final String prefix;

	public final String subName;

	private String _shortName;

	public RName(String nameSpace, String prefix, String subName, String fullName) {
		super();
		this.nameSpace = nameSpace;
		this.prefix = prefix;
		this.subName = subName;
		this.fullName = fullName;
	}

	public String toString() {
		return getShorName();
	}

	public String getShorName() {

		if (_shortName == null) {
			_shortName = prefix + ":" + subName;
		}
		
		return _shortName;
	}
}
