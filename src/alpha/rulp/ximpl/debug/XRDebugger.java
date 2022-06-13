package alpha.rulp.ximpl.debug;

import static alpha.rulp.lang.Constant.A_DEBUG;

import java.util.List;
import java.util.Stack;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRDebugger;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.error.REofException;
import alpha.rulp.ximpl.error.RResumeException;

public class XRDebugger implements IRDebugger {

	static final String F_BREAK = "b";

	static final String F_RESUME = "r";

	protected boolean active = false;

	protected int debugLevel = 0;

	protected Stack<String> frameStack = new Stack<>();

	protected void _load(IRFrame frame) throws RException {
		RulpUtil.addFrameObject(frame, new XRFactorDebugBreak(F_BREAK));
		RulpUtil.addFrameObject(frame, new XRFactorDebugResume(F_RESUME));
	}

	protected void _run(IRInterpreter interpreter, IRFrame frame) {

		IRParser parser = interpreter.getParser();

		DBG: while (active) {

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

				List<IRObject> objs = parser.parse(cmd);

				for (IRObject obj : objs) {
					IRObject rst = interpreter.compute(frame, obj);
					RulpUtil.incRef(rst);
					interpreter.out(RulpUtil.toString(rst) + "\n");
					RulpUtil.decRef(rst);
				}

			} catch (RResumeException e) {
				break;

			} catch (REofException e) {
				interpreter.out("eof");
				break;

			} catch (RException e) {

				try {
					if (RulpUtil.isTrace(frame)) {
						e.printStackTrace();
					}
				} catch (RException e1) {
					e1.printStackTrace();
				}

				interpreter.out(e.toString() + "\n");
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
	public boolean canBreak(IRObject obj) {

		// can't run debugger recursively
		if (debugLevel > 0) {
			return false;
		}

		switch (obj.getType()) {
		case FACTOR:
		case MACRO:
		case FUNC:
			return ((IRCallable) obj).isDebug();

		default:
			return false;
		}
	}

	@Override
	public boolean isDebug() {
		return active;
	}

	@Override
	public void popStack(IRFrame frame) {

		if (!active) {
			return;
		}

		frameStack.pop();
	}

	@Override
	public void pushStack(IRList expr, IRFrame frame) throws RException {

		if (!active) {
			return;
		}

		frameStack.push(String.format("at %s ; %s-%d", expr, frame.getFrameName(), frame.getFrameId()));
	}

	@Override
	public void run(IRInterpreter interpreter, IRFrame frame) throws RException {

		/**************************************************/
		// output current stack
		/**************************************************/
		interpreter.out("debug active:\n");
		int size = frameStack.size();
		while (size > 0) {
			interpreter.out(frameStack.get(size - 1) + "\n");
			--size;
		}

		/**************************************************/
		// init a frame for debugging
		/**************************************************/
		IRFrame debugFrame = RulpFactory.createFrame(frame, A_DEBUG);
		RulpUtil.incRef(debugFrame);
		_load(debugFrame);

		/**************************************************/
		// Run
		/**************************************************/
		try {

			++debugLevel;
			_run(interpreter, debugFrame);

		} finally {

			debugFrame.release();
			RulpUtil.decRef(debugFrame);
			--debugLevel;
		}

	}

	@Override
	public void setup() {
		active = true;
	}

	@Override
	public void shutdown() {

		frameStack.clear();
		active = false;
	}

}
