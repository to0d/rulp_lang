/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.macro;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.runtime.AbsRefCallableAdapter;

public class XRMacro extends AbsRefCallableAdapter implements IRMacro {

	private String _macroSignature;

	protected IRExpr macroBody;

	protected String macroName;

	protected final List<String> paraNameList;

	public XRMacro(String macroName, List<String> paraNameList, IRExpr macroBody) throws RException {
		this.macroName = macroName;
		this.paraNameList = paraNameList;
		this.macroBody = macroBody;
	}

	@Override
	public String asString() {
		return macroName;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter intepreter, IRFrame frame) throws RException {

		if (args.size() != (paraNameList.size() + 1)) {
			throw new RException("Invalid parameters: " + args);
		}

		Map<String, IRObject> macroMap = new HashMap<>();
		{
			IRIterator<? extends IRObject> valueIter = args.listIterator(1); // Skip factor head element
			Iterator<String> paraIter = paraNameList.iterator();
			while (valueIter.hasNext()) {
				macroMap.put(paraIter.next(), valueIter.next());
			}
		}

		return intepreter.compute(frame, RuntimeUtil.rebuild(macroBody, macroMap));
	}

	@Override
	public IRExpr getMacroBody() {
		return macroBody;
	}

	@Override
	public String getName() {
		return macroName;
	}

	@Override
	public String getSignature() {

		if (_macroSignature == null) {

		}

		return null;
	}

	@Override
	public RType getType() {
		return RType.MACRO;
	}

	@Override
	public String toString() {
		return macroName;
	}
}
