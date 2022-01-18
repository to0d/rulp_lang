/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.blob;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;

import alpha.rulp.lang.IRBlob;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsAtomObject;

public class XRBlob extends AbsAtomObject implements IRBlob {

	private byte[] value;

	public XRBlob(byte[] buf) {
		this.value = new byte[buf.length];
		System.arraycopy(buf, 0, value, 0, buf.length);
	}

	public XRBlob(int size) {

		if (size > 0) {
			this.value = new byte[size];
			for (int i = 0; i < size; ++i) {
				this.value[i] = 0;
			}
		}
	}

	protected int _copy(int blob_pos, byte[] buf, int buf_pos, int length, boolean read) throws RException {

		if (blob_pos < 0 || blob_pos >= length()) {
			throw new RException("invalid blob index: " + blob_pos);
		}

		if (buf_pos < 0 || buf_pos >= buf.length) {
			throw new RException("invalid buffer index: " + buf_pos);
		}

		int src_max_len = Math.min(value.length - blob_pos, length);
		int dst_max_len = buf.length - buf_pos;

		int cpy_len = Math.min(src_max_len, dst_max_len);

		if (cpy_len > 0) {

			if (read) {
				System.arraycopy(value, blob_pos, buf, buf_pos, cpy_len);
			} else {
				System.arraycopy(buf, buf_pos, value, blob_pos, cpy_len);
			}

		}

		return cpy_len;
	}

	@Override
	public String asString() {
		try {
			return RulpUtil.toString(this);
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	@Override
	public IRBlob cloneBlob() {
		return new XRBlob(value);
	}

	@Override
	public byte get(int index) throws RException {

		if (index < 0 || index >= length()) {
			throw new RException("invalid index: " + index);
		}

		return this.value[index];
	}

	@Override
	public RType getType() {
		return RType.BLOB;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public int length() throws RException {
		return value == null ? 0 : value.length;
	}

	public int read(int blob_pos, byte[] buf, int buf_pos, int length) throws RException {
		return _copy(blob_pos, buf, buf_pos, length, true);
	}

	@Override
	public void set(int index, byte value) throws RException {

		if (index < 0 || index >= length()) {
			throw new RException("invalid index: " + index);
		}

		this.value[index] = value;
	}

	@Override
	public String toString() {

		try {
			return RulpUtil.toString(this, MAX_TOSTRING_LEN);
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	public int write(int blob_pos, byte[] buf, int buf_pos, int length) throws RException {
		return _copy(blob_pos, buf, buf_pos, length, false);
	}

}