package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.A_QUEUE;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;
import java.util.LinkedList;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRQueue extends AbsRInstance implements IRCollection {

	public static class Loader implements IRObjectLoader {

		@Override
		public void load(IRInterpreter interpreter, IRFrame frame) throws RException, IOException {

			IRClass queueClass = RulpUtil.asClass(frame.getEntry(A_QUEUE).getValue());

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_INIT, new AbsAtomFactorAdapter(F_MBR_QUEUE_INIT) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
					if (args.size() != 1) {
						throw new RException("Invalid parameters: " + args);
					}
					return RulpFactory.createInstanceOfQueue(interpreter);
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_PUSH_BACK, new AbsAtomFactorAdapter(F_MBR_QUEUE_PUSH_BACK) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 3) {
						throw new RException("Invalid parameters: " + args);
					}

					XRQueue queue = asQueue(interpreter.compute(frame, args.get(1)));
					IRObject obj = interpreter.compute(frame, args.get(2));
					queue.push_back(obj);

					return O_Nil;
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_PUSH_FRONT, new AbsAtomFactorAdapter(F_MBR_QUEUE_PUSH_FRONT) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 3) {
						throw new RException("Invalid parameters: " + args);
					}

					XRQueue queue = asQueue(interpreter.compute(frame, args.get(1)));
					IRObject obj = interpreter.compute(frame, args.get(2));
					queue.push_front(obj);

					return O_Nil;
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_POP_BACK, new AbsAtomFactorAdapter(F_MBR_QUEUE_POP_BACK) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return asQueue(interpreter.compute(frame, args.get(1))).pop_back();
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_POP_FRONT, new AbsAtomFactorAdapter(F_MBR_QUEUE_POP_FRONT) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return asQueue(interpreter.compute(frame, args.get(1))).pop_front();
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_PEEK_BACK, new AbsAtomFactorAdapter(F_MBR_QUEUE_PEEK_BACK) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return asQueue(interpreter.compute(frame, args.get(1))).peek_back();
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_PEEK_FRONT, new AbsAtomFactorAdapter(F_MBR_QUEUE_PEEK_FRONT) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return asQueue(interpreter.compute(frame, args.get(1))).peek_front();
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_GET, new AbsAtomFactorAdapter(F_MBR_QUEUE_GET) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 3) {
						throw new RException("Invalid parameters: " + args);
					}

					XRQueue queue = asQueue(interpreter.compute(frame, args.get(1)));
					int index = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();

					return queue.get(index);
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_SIZE_OF, new AbsAtomFactorAdapter(F_MBR_QUEUE_SIZE_OF) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return RulpFactory.createInteger(asQueue(interpreter.compute(frame, args.get(1))).size());
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_IS_EMTPY, new AbsAtomFactorAdapter(F_MBR_QUEUE_IS_EMTPY) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return RulpFactory.createBoolean(asQueue(interpreter.compute(frame, args.get(1))).isEmpty());
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_CLEAR, new AbsAtomFactorAdapter(F_MBR_QUEUE_CLEAR) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					asQueue(interpreter.compute(frame, args.get(1))).clear();

					return O_Nil;
				}

			}, RAccessType.PRIVATE);

			RulpUtil.setMember(queueClass, F_MBR_QUEUE_TO_LIST, new AbsAtomFactorAdapter(F_MBR_QUEUE_TO_LIST) {

				@Override
				public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

					if (args.size() != 2) {
						throw new RException("Invalid parameters: " + args);
					}

					return asQueue(interpreter.compute(frame, args.get(1))).toList();
				}

			}, RAccessType.PRIVATE);
		}
	}

	static final String F_MBR_QUEUE_CLEAR = "_queue_clear";

	static final String F_MBR_QUEUE_GET = "_queue_get";

	static final String F_MBR_QUEUE_INIT = "_queue_init";

	static final String F_MBR_QUEUE_IS_EMTPY = "_queue_is_empty";

	static final String F_MBR_QUEUE_PEEK_BACK = "_queue_peek_back";

	static final String F_MBR_QUEUE_PEEK_FRONT = "_queue_peek_front";

	static final String F_MBR_QUEUE_POP_BACK = "_queue_pop_back";

	static final String F_MBR_QUEUE_POP_FRONT = "_queue_pop_front";

	static final String F_MBR_QUEUE_PUSH_BACK = "_queue_push_back";

	static final String F_MBR_QUEUE_PUSH_FRONT = "_queue_push_front";

	static final String F_MBR_QUEUE_SIZE_OF = "_queue_size_of";

	static final String F_MBR_QUEUE_TO_LIST = "_queue_to_list";

	static XRQueue asQueue(IRObject obj) throws RException {

		if (!(obj instanceof XRQueue)) {
			throw new RException("Can't convert object to queue: " + obj);
		}

		return (XRQueue) obj;
	}

	private LinkedList<IRObject> elementList = new LinkedList<>();

	public XRQueue(IRClass noClass) {
		super(noClass, null, null);

	}

	@Override
	public void clear() {
		elementList.clear();
	}

	public IRObject get(int index) throws RException {

		IRObject obj = elementList.get(index);
		if (obj != O_Nil) {
			RulpUtil.decRef(obj);
		}

		return obj;
	}

	@Override
	public boolean isEmpty() {
		return elementList.isEmpty();
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

	public IRList toList() throws RException {
		return RulpFactory.createList(elementList);
	}
}
