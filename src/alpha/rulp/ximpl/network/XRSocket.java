package alpha.rulp.ximpl.network;

import static alpha.rulp.lang.Constant.A_SOCKET;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import alpha.rulp.lang.IRBlob;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.EncodeUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRSocket extends AbsRInstance {

	static final String F_MBR_SOCKET_CLOSE = "_socket_close";

	static final String F_MBR_SOCKET_GET_LOCALADDRESS = "_socket_getHostAddress";

	static final String F_MBR_SOCKET_GET_LOCALHOST = "_socket_getLocalHost";

	static final String F_MBR_SOCKET_INIT = "_socket_init";

	static final String F_MBR_SOCKET_OPEN = "_socket_open";

	static final String F_MBR_SOCKET_READ = "_socket_read";

	static final String F_MBR_SOCKET_READ_BLOB = "_socket_read_blob";

	static final String F_MBR_SOCKET_WRITE = "_socket_write";

	static final String F_MBR_SOCKET_WRITE_BLOB = "_socket_write_blob";

	public static boolean TRACE = false;

	public static void init(IRInterpreter _interpreter, IRFrame systemFrame) throws RException {

		IRClass mapClass = RulpUtil.asClass(systemFrame.getEntry(A_SOCKET).getValue());

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_INIT, (args, interpreter, frame) -> {

			if (args.size() != 3) {
				throw new RException("Invalid parameters: " + args);
			}

			String addr = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
			int port = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();

			return RulpFactory.createInstanceOfSocket(addr, port);

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_OPEN, (args, interpreter, frame) -> {

			if (args.size() != 2) {
				throw new RException("Invalid parameters: " + args);
			}

			XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
			socket.open();

			return O_Nil;

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_CLOSE, (args, interpreter, frame) -> {

			if (args.size() != 2) {
				throw new RException("Invalid parameters: " + args);
			}

			XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
			socket.close();

			return O_Nil;

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_WRITE, (args, interpreter, frame) -> {

			if (args.size() != 3) {
				throw new RException("Invalid parameters: " + args);
			}

			XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
			IRObject obj = interpreter.compute(frame, args.get(2));
			int len = socket.write(obj);

			return RulpFactory.createInteger(len);

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_WRITE_BLOB, (args, interpreter, frame) -> {

			if (args.size() != 5) {
				throw new RException("Invalid parameters: " + args);
			}

			XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
			IRBlob blob = RulpUtil.asBlob(interpreter.compute(frame, args.get(2)));
			int pos = RulpUtil.asInteger(interpreter.compute(frame, args.get(3))).asInteger();
			int len = RulpUtil.asInteger(interpreter.compute(frame, args.get(4))).asInteger();
			int cpy_len = socket.write(blob, pos, len);

			return RulpFactory.createInteger(cpy_len);

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_READ, (args, interpreter, frame) -> {

			if (args.size() != 3) {
				throw new RException("Invalid parameters: " + args);
			}

			XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
			RType type = RType.toType(RulpUtil.asAtom(interpreter.compute(frame, args.get(2))).getName());
			return socket.read(type);

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_READ_BLOB, (args, interpreter, frame) -> {

			if (args.size() != 5) {
				throw new RException("Invalid parameters: " + args);
			}

			XRSocket socket = RulpUtil.asSocket(interpreter.compute(frame, args.get(1)));
			IRBlob blob = RulpUtil.asBlob(interpreter.compute(frame, args.get(2)));
			int pos = RulpUtil.asInteger(interpreter.compute(frame, args.get(3))).asInteger();
			int len = RulpUtil.asInteger(interpreter.compute(frame, args.get(4))).asInteger();
			int cpy_len = socket.read(blob, pos, len);

			return RulpFactory.createInteger(cpy_len);

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_GET_LOCALHOST, (args, interpreter, frame) -> {

			if (args.size() != 1) {
				throw new RException("Invalid parameters: " + args);
			}

			try {
				return RulpFactory.createString(InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				e.printStackTrace();
				throw new RException(e.toString());
			}

		}, RAccessType.PRIVATE);

		RulpUtil.setMember(mapClass, F_MBR_SOCKET_GET_LOCALADDRESS, (args, interpreter, frame) -> {
			if (args.size() != 1) {
				throw new RException("Invalid parameters: " + args);
			}

			try {
				return RulpFactory.createString(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
				throw new RException(e.toString());
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

	private DataInputStream m_in = null;

	private DataOutputStream m_out = null;

	private Socket m_socket;

	private int port;

	public XRSocket(String addr, int port) {
		super();
		this.addr = addr;
		this.port = port;
	}

	public char[] byteArrayToCharArray(byte[] byteValue) {
		if (byteValue == null) {
			return null;
		}

		char[] charValue = new char[byteValue.length / 2];
		int inPos = 0;
		int outPos = 0;

		while (inPos < byteValue.length) {
			switch (byteValue[inPos + 1]) {
			case -35: // translate to the left bracket
				if (byteValue[inPos] == 0) {
					charValue[outPos++] = '[';
					inPos += 2;
				} else
					charValue[outPos++] = (char) (((byteValue[inPos++] & 0xFF) << 8) + (byteValue[inPos++] & 0xFF));
				break;

			case -88: // translate to the right bracket
				if (byteValue[inPos] == 0) {
					charValue[outPos++] = ']';
					inPos += 2;
				} else
					charValue[outPos++] = (char) (((byteValue[inPos++] & 0xFF) << 8) + (byteValue[inPos++] & 0xFF));
				break;

			default:
				charValue[outPos++] = (char) (((byteValue[inPos++] & 0xFF) << 8) + (byteValue[inPos++] & 0xFF));
			}

		}

		return charValue;
	}

	public void close() throws RException {

		if (m_socket != null) {

			try {

				m_socket.close();
				m_socket = null;
				m_in = null;
				m_out = null;

			} catch (IOException e) {
				if (TRACE) {
					e.printStackTrace();
				}

				throw new RException("fail to close socket<" + addr + ":" + port + ">: " + e.toString());
			}
		}
	}

	public void open() throws RException {

		if (m_socket != null) {
			throw new RException("socket is already open: " + addr + ":" + port);
		}

		try {
			m_socket = new Socket(addr, port);
			m_in = new DataInputStream(new BufferedInputStream(m_socket.getInputStream()));
			m_out = new DataOutputStream(new BufferedOutputStream(m_socket.getOutputStream(), 2048));
		} catch (IOException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("fail to open socket<" + addr + ":" + port + ">: " + e.toString());
		}
	}

	public int read(IRBlob blob, int pos, int len) throws RException {

		int read_len = Math.min(blob.length() - pos, len);
		if (read_len <= 0) {
			return 0;
		}

		try {

			int totalBytes = 0;
			int offset = pos;

			while (totalBytes < read_len) {
				int bytesRead = m_in.read(blob.getValue(), offset, read_len - totalBytes);
				totalBytes += bytesRead;
				offset += bytesRead;
			}

			return read_len;

		} catch (IOException e) {

			if (TRACE) {
				e.printStackTrace();
			}
			throw new RException("fail to read blob from socket<" + addr + ":" + port + ">: " + e.toString());
		}
	}

	public IRObject read(RType type) throws RException {

		try {
			switch (type) {

			case INT:
				return RulpFactory.createInteger(m_in.readInt());

			case LONG:
				return RulpFactory.createLong(m_in.readLong());

			case FLOAT:
				return RulpFactory.createFloat(m_in.readFloat());

			case DOUBLE:
				return RulpFactory.createDouble(m_in.readDouble());

			case STRING:
				return RulpFactory.createString(readString());

			default:
				throw new RException("do not support read obj from socket: " + type);

			}
		} catch (IOException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("fail to read obj<type> from socket<" + addr + ":" + port + ">: " + e.toString());
		}

	}

	public byte[] readBytes() throws IOException {

		int byteCount = m_in.readInt();
		byte[] bytes = new byte[byteCount];

		if (byteCount > 0) {
			int totalBytes = 0;
			int offset = 0;

			while (totalBytes < byteCount) {
				int bytesRead = m_in.read(bytes, offset, byteCount - totalBytes);
				totalBytes += bytesRead;
				offset += bytesRead;
			}
		}

		return bytes;
	}

	public String readString() throws IOException {
		return new String(byteArrayToCharArray(readBytes()));
	}

	public int write(IRBlob blob, int pos, int len) throws RException {

		int cpy_len = Math.min(blob.length() - pos, len);
		if (cpy_len <= 0) {
			return 0;
		}
		try {
			write(m_socket.getOutputStream(), blob.getValue(), 0, cpy_len);
			return cpy_len;
		} catch (IOException e) {
			if (TRACE) {
				e.printStackTrace();
			}
			throw new RException("fail to write blob to socket<" + addr + ":" + port + ">: " + e.toString());
		}
	}

	public int write(IRObject obj) throws RException {

		if (m_socket == null) {
			throw new RException("socket is not open yet: " + addr + ":" + port);
		}

		int len = 0;
		try {

			switch (obj.getType()) {

			case INT:
				len = EncodeUtil.encode(RulpUtil.asInteger(obj).asInteger(), buf8, 0);
				write(m_socket.getOutputStream(), buf8, 0, len);
				break;

			case LONG:
				len = EncodeUtil.encode(RulpUtil.asLong(obj).asLong(), buf8, 0);
				write(m_socket.getOutputStream(), buf8, 0, len);
				break;

			case FLOAT:
				len = EncodeUtil.encode(RulpUtil.asFloat(obj).asFloat(), buf8, 0);
				write(m_socket.getOutputStream(), buf8, 0, len);
				break;

			case DOUBLE:
				len = EncodeUtil.encode(RulpUtil.asDouble(obj).asDouble(), buf8, 0);
				write(m_socket.getOutputStream(), buf8, 0, len);
				break;

			case STRING:
				byte[] buf = RulpUtil.asString(obj).asString().getBytes();
				len = buf.length;
				write(m_socket.getOutputStream(), buf, 0, len);

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
