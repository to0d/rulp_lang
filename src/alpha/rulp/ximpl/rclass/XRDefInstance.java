package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.F_INIT;
import static alpha.rulp.lang.Constant.F_UNINIT;

import java.util.ArrayList;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class XRDefInstance extends AbsRInstance {

	private IRInterpreter interpreter;

	public XRDefInstance(IRClass rClass, String instanceName, IRFrame definedFrame) {
		super(rClass, instanceName, definedFrame);
	}

	protected void _delete() throws RException {

		/******************************************/
		// Check ~init parameters
		/******************************************/
		IRMember member = getMember(F_UNINIT);
		if (member != null) {

			ArrayList<IRObject> initArgs = new ArrayList<>();
			initArgs.add(member);

			IRList expr = RuntimeUtil.rebuildFuncExpr(RulpUtil.asFunction(member.getValue()),
					RulpFactory.createExpression(initArgs), interpreter, getSubjectFrame());

			interpreter.compute(getSubjectFrame(), expr);
		}

		super._delete();
	}

	@Override
	public void init(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		/******************************************/
		// Check init parameters
		/******************************************/
		IRMember member = getMember(F_INIT);
		if (member == null) {

			// has argument parameter
			if (args != null && args.size() > 0) {
				throw new RException(String.format("not init defined for class<%s>: arg=%s", this, args));
			}

		} else {

			ArrayList<IRObject> initArgs = new ArrayList<>();
			initArgs.add(member);
			RulpUtil.addAll(initArgs, args);

			IRList expr = RulpFactory.createExpression(initArgs);
			RulpUtil.incRef(expr);

			IRList newExpr = RuntimeUtil.rebuildFuncExpr(RulpUtil.asFunction(member.getValue()), expr, interpreter,
					frame);
			RulpUtil.incRef(newExpr);

			try {
				interpreter.compute(getSubjectFrame(), newExpr);
			} finally {
				RulpUtil.decRef(newExpr);
				RulpUtil.decRef(expr);
			}

		}

		this.interpreter = interpreter;
	}
}
