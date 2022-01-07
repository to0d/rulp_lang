/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRNative;
import alpha.rulp.lang.RType;

public class XRNative extends AbsRefObject implements IRNative {

	private Object obj;

	private Class<?> objClass;

	private String objClassName;

	public XRNative(Object obj) {
		super();
		this.obj = obj;
		this.objClass = obj.getClass();
		this.objClassName = objClass.getName();
	}

	@Override
	public String asString() {
		return objClassName + "#" + obj.toString();
	}

	@Override
	public String getClassName() {
		return objClassName;
	}

	@Override
	public Class<?> getNativeClass() {
		return objClass;
	}

	@Override
	public Object getObject() {
		return obj;
	}

	@Override
	public RType getType() {
		return RType.NATIVE;
	}
}
