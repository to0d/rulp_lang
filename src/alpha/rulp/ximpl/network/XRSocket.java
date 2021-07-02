package alpha.rulp.ximpl.network;

import static alpha.rulp.lang.Constant.A_SOCKET;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.EncodeUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRSocket extends AbsRInstance {

	static final String F_MBR_SOCKET_CLOSE = "_socket_close";

	static final String F_MBR_SOCKET_INIT = "_socket_init";

	static final String F_MBR_SOCKET_OPEN = "_socket_open";

	static final String F_MBR_SOCKET_WRITE = "_socket_write";

	public static boolean TRACE = false;

	public static void init(IRInterpreter interpreter, IRFrame systemFrame) throws RException {

		IRClass mapClass = RulpUtil.asClass(systemFrame.getEntry(A_SOCKET).getValue());

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_INIT, new AbsRFactorAdapter(F_MBR_SOCKET_INIT) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 3) {
					throw new RException("Invalid parameters: " + args);
				}

				String addr = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
				int port = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();

				return RulpFactory.createInstanceOfSocket(addr, port);
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_OPEN, new AbsRFactorAdapter(F_MBR_SOCKET_OPEN) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
				socket.open();

				return O_Nil;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_CLOSE, new AbsRFactorAdapter(F_MBR_SOCKET_CLOSE) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 2) {
					throw new RException("Invalid parameters: " + args);
				}

				XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
				socket.close();

				return O_Nil;
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_WRITE, new AbsRFactorAdapter(F_MBR_SOCKET_WRITE) {

			@Override
			public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

				if (args.size() != 3) {
					throw new RException("Invalid parameters: " + args);
				}

				XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
				IRObject obj = interpreter.compute(frame, args.get(2));
				int len = socket.write(obj);

				return RulpFactory.createInteger(len);
			}

			@Override
			public boolean isThreadSafe() {
				return true;
			}
		}, RAccessType.PRIVATE);

	}

	public static void write(OutputStream os, byte[] buf, int offset, int len) throws IOException {

		if (TRACE) {
			System.out.println(
					"write socket: len=" + len + ", bytes=" + EncodeUtil.convertBytesToHexString(buf, offset, len));
		}

		os.write(buf, offset, len);
	}

	private String addr;

	private byte[] buf8 = new byte[8];

	private Socket client;

	private int port;

	public XRSocket(String addr, int port) {
		super();
		this.addr = addr;
		this.port = port;
	}

	public void close() throws RException {

		if (client != null) {

			try {

				client.close();
				client = null;

			} catch (IOException e) {
				if (TRACE) {
					e.printStackTrace();
				}

				throw new RException("fail to close socket<" + addr + ":" + port + ">: " + e.toString());
			}
		}
	}

	public void open() throws RException {

		if (client != null) {
			throw new RException("socket is already open: " + addr + ":" + port);
		}

		try {
			client = new Socket(addr, port);

		} catch (IOException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("fail to open socket<" + addr + ":" + port + ">: " + e.toString());
		}
	}

	public int write(IRObject obj) throws RException {

		if (client == null) {
			throw new RException("socket is not open yet: " + addr + ":" + port);
		}

		int len = 0;
		try {

			switch (obj.getType()) {

			case INT:
				len = EncodeUtil.encode(RulpUtil.asInteger(obj).asInteger(), buf8, 0);
				write(client.getOutputStream(), buf8, 0, len);
				break;

			case LONG:
				len = EncodeUtil.encode(RulpUtil.asLong(obj).asLong(), buf8, 0);
				write(client.getOutputStream(), buf8, 0, len);
				break;

			case FLOAT:
				len = EncodeUtil.encode(RulpUtil.asFloat(obj).asFloat(), buf8, 0);
				write(client.getOutputStream(), buf8, 0, len);
				break;

			case DOUBLE:
				len = EncodeUtil.encode(RulpUtil.asDouble(obj).asDouble(), buf8, 0);
				write(client.getOutputStream(), buf8, 0, len);
				break;

			case STRING:
				byte[] buf = RulpUtil.asString(obj).asString().getBytes();
				len = buf.length;
				write(client.getOutputStream(), buf, 0, len);

				break;

			default:
				throw new RException("do not support write obj to socket: " + obj);

			}

			return len;

		} catch (IOException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException(
					"fail to write obj<" + obj + "> to socket<" + addr + ":" + port + ">: " + e.toString());
		}
	}
}
