/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

public interface IRBlob extends IRObject {

	public IRBlob cloneBlob();

	public byte get(int index) throws RException;

	public byte[] getValue();

	public int length() throws RException;

	public int read(int blob_pos, byte[] buf, int buf_pos, int length) throws RException;

	public void set(int index, byte value) throws RException;

	public int write(int blob_pos, byte[] buf, int buf_pos, int length) throws RException;
}
