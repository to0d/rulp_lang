/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public abstract class AbsRInstance extends AbsRSubject implements IRInstance {

	protected String instanceName;

	protected IRClass rClass;

	public AbsRInstance() {
		super();
	}

	public AbsRInstance(IRClass rClass, String instanceName, IRFrame definedFrame) {
		super(definedFrame);
		this.rClass = rClass;
		this.instanceName = instanceName;
	}

	@Override
	public String asString() {
		return instanceName;
	}

	@Override
	public void delete(IRInterpreter interpreter, IRFrame frame) throws RException {
		this._delete();
	}

	@Override
	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public IRMember getMember(String name) throws RException {

		if (name == null) {
			return null;
		}

		// Instance local member
		IRMember objMbr = super.getMember(name);

		if (objMbr == null) {

			IRMember classMbr = rClass.getMember(name);
			if (classMbr == null) {
				IRClass superClass = rClass.getSuperClass();
				while (classMbr == null && superClass != null) {
					classMbr = superClass.getMember(name);
					superClass = superClass.getSuperClass();
				}
			}

			if (classMbr != null) {

				IRObject classMbrVal = classMbr.getValue();
				IRObject insMbrVal = null;

				// for static member, use it directly
				if (classMbr.isStatic()) {

					switch (classMbrVal.getType()) {
					case FUNC:

						if (RulpUtil.isFunctionList(classMbrVal)) {

							IRFunctionList oldFunList = RulpUtil.asFunctionList(classMbrVal);
							IRFunctionList newFunList = RulpFactory.createFunctionList(oldFunList.getName());

							for (IRFunction f : oldFunList.getAllFuncList()) {
								newFunList.addFunc(f);
							}

							insMbrVal = newFunList;

						} else {

							insMbrVal = classMbrVal;
						}

						break;

					case VAR:
						insMbrVal = classMbrVal;
						break;

					default:
						throw new RException("Invalid member obj: " + classMbrVal);
					}

				} else {

					switch (classMbrVal.getType()) {
					case FUNC:

						if (RulpUtil.isFunctionList(classMbrVal)) {

							IRFunctionList oldFunList = RulpUtil.asFunctionList(classMbrVal);
							IRFunctionList newFunList = RulpFactory.createFunctionList(oldFunList.getName());

							for (IRFunction f : oldFunList.getAllFuncList()) {
								newFunList.addFunc(RulpFactory.createFunctionLambda(f, this.getSubjectFrame()));
							}

							insMbrVal = newFunList;

						} else {

							insMbrVal = RulpFactory.createFunctionLambda(RulpUtil.asFunction(classMbrVal),
									this.getSubjectFrame());
						}

						break;

					case VAR:

						IRVar var = RulpFactory.createVar(name);
						var.setValue(RulpUtil.asVar(classMbrVal).getValue());
						insMbrVal = var;

						break;

					default:
						throw new RException("Invalid member obj: " + classMbrVal);
					}
				}

				objMbr = RulpFactory.createMember(this, name, insMbrVal);
				objMbr.setAccessType(classMbr.getAccessType());
				objMbr.setFinal(classMbr.isFinal());
				objMbr.setStatic(classMbr.isStatic());

				this.setMember(name, objMbr);
			}
		}

		return objMbr;
	}

	@Override
	public IRSubject getParent() {
		return rClass;
	}

	@Override
	public IRClass getRClass() {
		return rClass;
	}

	@Override
	public String getSubjectName() {
		return instanceName;
	}

	@Override
	public RType getType() {
		return RType.INSTANCE;
	}

	@Override
	public void init(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public void setRClass(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public String toString() {
		return asString();
	}

}
