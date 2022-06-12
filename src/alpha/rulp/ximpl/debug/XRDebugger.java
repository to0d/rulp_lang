package alpha.rulp.ximpl.debug;

import static alpha.rulp.lang.Constant.A_DEBUG;

import java.util.Stack;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRDebugger;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRDebugger implements IRDebugger {

	static final String F_BREAK = "b";
	
	static final String F_RESUME = "r";

	protected boolean debugCmd = false;

	protected boolean debugMode = false;

	protected Stack<String> frameStack = new Stack<>();

	protected void _load(IRFrame frame) throws RException {
		RulpUtil.addFrameObject(frame, new XRFactorB(F_BREAK));
		RulpUtil.addFrameObject(frame, new XRFactorR(F_RESUME));
	}

	protected void _run(IRInterpreter interpreter, IRFrame frame) {

		DBG: while (debugCmd) {

			interpreter.out("R>");

			try {

				String cmd = interpreter.read().trim();
				if (cmd.isEmpty()) {
					continue DBG;
				}

				if (!cmd.startsWith("(")) {
					cmd = "(" + cmd;
				}

				if (!cmd.endsWith(")")) {
					cmd = cmd + ")";
				}

				interpreter.compute(cmd, (rst) -> {
					System.out.println(RulpUtil.toString(rst));
				});

			} catch (RDebugResumeException e) {
				debugCmd = false;
				break;

			} catch (RException e) {
				e.printStackTrace();
				continue DBG;
			}
		}
	}

	@Override
	public boolean addBreakObject(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (obj.getType() == RType.ATOM) {

			IRObject findObj = RulpUtil.lookup(obj, interpreter, frame);
			if (findObj == null || findObj == obj) {
				throw new RException("object not found: " + obj);
			}

			obj = findObj;
		}

		switch (obj.getType()) {
		case FACTOR:
		case MACRO:
		case FUNC:
			((IRCallable) obj).setDebug(true);
			break;

		default:
			throw new RException("can't debug object: " + obj);
		}

		return true;
	}

	@Override
	public void debugBegin(IRObject e0, IRList expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (!debugMode) {
			return;
		}

		// push call stack
		frameStack.push(String.format("at %s ; %s-%d", expr, frame.getFrameName(), frame.getFrameId()));

		// can't run debugger recursively
		if (debugCmd) {
			return;
		}

		switch (e0.getType()) {
		case FACTOR:
		case MACRO:
		case FUNC:
			if (!((IRCallable) e0).isDebug()) {
				return;
			}

			break;

		default:
			return;
		}

		debugCmd = true;

		interpreter.out("break at object: " + e0);
		int size = frameStack.size();
		while (size > 0) {
			interpreter.out(frameStack.get(size - 1));
		}

		IRFrame debugFrame = RulpFactory.createFrame(frame, A_DEBUG);
		RulpUtil.incRef(debugFrame);
		_load(debugFrame);

		try {
			_run(interpreter, debugFrame);
		} finally {
			debugFrame.release();
			RulpUtil.decRef(debugFrame);
		}

	}

	@Override
	public void debugEnd(IRFrame frame) {

		if (!debugMode) {
			return;
		}

		frameStack.pop();
	}

	@Override
	public boolean isDebug() {
		return debugMode;
	}

	@Override
	public void setup() {
		debugMode = true;
	}

	@Override
	public void shutdown() {

		frameStack.clear();
		debugMode = false;
	}

}
