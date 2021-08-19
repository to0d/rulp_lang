package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.*;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRQueue extends AbsRInstance {

	static final String F_MBR_QUEUE_INIT = "_queue_init";

	static final String F_MBR_QUEUE_PEEK_BACK = "_queue_peek_back";

	static final String F_MBR_QUEUE_PEEK_FRONT = "_queue_peek_front";

	static final String F_MBR_QUEUE_POP_BACK = "_queue_pop_back";

	static final String F_MBR_QUEUE_POP_FRONT = "_queue_pop_front";

	static final String F_MBR_QUEUE_PUSH_BACK = "_queue_push_back";

	static final String F_MBR_QUEUE_PUSH_FRONT = "_queue_push_front";

	static final String F_MBR_QUEUE_SIZE_OF = "_queue_size_of";

	static XRQueue asQueue(IRObject obj) throws RException {

		if (!(obj instanceof XRQueue)) {
			throw new RException("Can't convert object to queue: " + obj);
		}

		return (XRQueue) obj;
	}

	public static void init(IRInterpreter interpreter, IRFrame systemFrame) throws RException {

		IRClass queueClass = RulpUtil.asClass(systemFrame.getEntry(A_QUEUE).getValue());

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_INIT, new AbsRFactorAdapter(F_MBR_QUEUE_INIT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
				if (args.size() != 1) {
					throw new RException("Invalid parameters: " + args);
				}
				return RulpFactory.createInstanceOfQueue(interpreter);
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_PUSH_BACK, new AbsRFactorAdapter(F_MBR_QUEUE_PUSH_BACK) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 3) {
					throw new RException("Invalid parameters: " + args);
				}

				XRQueue set = asQueue(interpreter.compute(frame, args.get(1)));
				IRObject obj = interpreter.compute(frame, args.get(2));
				set.push_back(obj);

				return O_Nil;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_PUSH_FRONT, new AbsRFactorAdapter(F_MBR_QUEUE_PUSH_FRONT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 3) {
					throw new RException("Invalid parameters: " + args);
				}

				XRQueue set = asQueue(interpreter.compute(frame, args.get(1)));
				IRObject obj = interpreter.compute(frame, args.get(2));
				set.push_front(obj);

				return O_Nil;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_POP_BACK, new AbsRFactorAdapter(F_MBR_QUEUE_POP_BACK) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				return asQueue(interpreter.compute(frame, args.get(1))).pop_back();
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_POP_FRONT, new AbsRFactorAdapter(F_MBR_QUEUE_POP_FRONT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				return asQueue(interpreter.compute(frame, args.get(1))).pop_front();
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_PEEK_BACK, new AbsRFactorAdapter(F_MBR_QUEUE_PEEK_BACK) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				return asQueue(interpreter.compute(frame, args.get(1))).peek_back();
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_PEEK_FRONT, new AbsRFactorAdapter(F_MBR_QUEUE_PEEK_FRONT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				return asQueue(interpreter.compute(frame, args.get(1))).peek_front();
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(queueClass, F_MBR_QUEUE_SIZE_OF, new AbsRFactorAdapter(F_MBR_QUEUE_SIZE_OF) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				return RulpFactory.createInteger(asQueue(interpreter.compute(frame, args.get(1))).size());
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);
	}

	private LinkedList<IRObject> elementList = new LinkedList<>();

	public XRQueue(IRClass noClass) {
		super(noClass, null, null);

	}

	public IRObject peek_back() throws RException {
		return elementList.peekLast();
	}

	public IRObject peek_front() throws RException {
		return elementList.peekFirst();
	}

	public IRObject pop_back() throws RException {

		IRObject obj = elementList.pollLast();
		if (obj != O_Nil) {
			RulpUtil.decRef(obj);
		}

		return obj;
	}

	public IRObject pop_front() throws RException {

		IRObject obj = elementList.pollFirst();
		if (obj != O_Nil) {
			RulpUtil.decRef(obj);
		}

		return obj;
	}

	public void push_back(IRObject newObj) throws RException {

		if (newObj == null) {
			elementList.addLast(O_Nil);
			return;
		}

		elementList.addLast(newObj);
		RulpUtil.incRef(newObj);
	}

	public void push_front(IRObject newObj) throws RException {

		if (newObj == null) {
			elementList.addFirst(O_Nil);
			return;
		}

		elementList.addFirst(newObj);
		RulpUtil.incRef(newObj);
	}

	public int size() {
		return elementList.size();
	}

}
