/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static alpha.rulp.string.Constant.EN_CHAR_ENTER;
import static alpha.rulp.string.Constant.EN_CHAR_NEWLINE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.SystemUtil.OSType;

public class FileUtil {

	static AtomicLong ioReadBytes = new AtomicLong(0);

	static AtomicInteger ioReadCount = new AtomicInteger(0);

	static AtomicLong ioWriteBytes = new AtomicLong(0);

	static AtomicInteger ioWriteCount = new AtomicInteger(0);

	private static void _copyFileTo(File srcFile, File dstFolder, String fileName) throws IOException {

		if (!srcFile.exists()) {
			throw new IOException("File not exist: " + srcFile.getAbsolutePath());
		}

		if (fileName == null) {
			fileName = srcFile.getName();
		}

		File dstFile = new File(dstFolder.getAbsolutePath() + File.separator + fileName);

		if (srcFile.isFile()) {

			if (dstFile.exists()) {
				return;
			}

			copyByChannel(srcFile, dstFile);
			return;
		}

		if (srcFile.isDirectory()) {

			if (!dstFile.exists() && !dstFile.mkdirs()) {
				throw new IOException("Unable to mkdirs: " + dstFile.getAbsolutePath());
			}

			for (File f : srcFile.listFiles()) {
				_copyFileTo(f, dstFile, null);
			}
		}
	}

	private static String _toValidFolderPath(String path) {

		path = path.trim();
		if (path.endsWith(File.separator)) {
			path = path.substring(1, path.length() - 1);
		}

		return path;
	}

	public static void appendTxtFile(String outPath, Collection<String> content) throws IOException {

		long writeBytes = 0;

		try (FileOutputStream output = new FileOutputStream(outPath, true); PrintStream out = new PrintStream(output)) {
			for (String line : content) {
				if (line != null) {
					out.println(line);
					writeBytes += line.length();
				}
			}
		}

		incIOWriteFileCount();
		incIOWriteFileBytes(writeBytes);
	}

	public static void appendTxtFile(String outPath, String line) throws IOException {

		if (line == null) {
			return;
		}

		long writeBytes = 0;

		try (FileOutputStream output = new FileOutputStream(outPath, true); PrintStream out = new PrintStream(output)) {
			out.println(line);
			writeBytes += line.length();
		}

		incIOWriteFileCount();
		incIOWriteFileBytes(writeBytes);
	}

	public static void copyByChannel(File srcFile, File targetFile) throws IOException {

		if (srcFile == null || targetFile == null) {
			throw new IOException("NULL para");
		}

		if (!srcFile.exists() || !srcFile.isFile()) {
			throw new IOException("Source file not exist:" + srcFile.getAbsolutePath());
		}

		if (targetFile.exists()) {
			throw new IOException("target file exist already:" + targetFile.getAbsolutePath());
		}

		long writeBytes = 0;

		try (FileInputStream fi = new FileInputStream(srcFile);
				FileChannel in = fi.getChannel();
				FileOutputStream fo = new FileOutputStream(targetFile);
				FileChannel out = fo.getChannel();) {

			writeBytes = in.transferTo(0, in.size(), out);

		} finally {

			incIOWriteFileCount();
			incIOWriteFileBytes(writeBytes);
		}
	}

	public static void copyFolder(File srcFolder, File dstFolder) throws IOException {

		if (srcFolder == null || dstFolder == null || !srcFolder.exists() || !srcFolder.isDirectory()
				|| !dstFolder.exists() || !dstFolder.isDirectory()) {
			throw new IOException("Invaild parameter ");
		}

		// Check path valid
		// E:\x\a ==> E:\x\a\y
		if (dstFolder.getAbsolutePath().startsWith(srcFolder.getAbsolutePath())) {
			throw new IOException("Invaild path" + srcFolder.getAbsolutePath() + " : " + dstFolder.getAbsolutePath());
		}

		for (File f : srcFolder.listFiles()) {
			_copyFileTo(f, dstFolder, null);
		}
	}

	public static boolean deleteFile(File file) {

		if (!isSupportFile(file)) {
			return false;
		}

		if (file.isFile()) {
			return file.delete();

		} else if (file.isDirectory()) {

			for (File subFile : file.listFiles()) {

				if (!deleteFile(subFile)) {
					return false;
				}
			}

			return file.delete();
		} else {
			return false;
		}
	}

	public static String getFileName(String path) {

		int pos = path.lastIndexOf(File.separatorChar);
		if (pos == -1) {
			return path;
		}

		if (pos == (path.length() - 1)) {
			return getFileName(path.substring(0, pos));
		}

		return path.substring(pos + 1).trim();
	}

	public static String getFileParent(String path) {

		int pos = path.lastIndexOf(File.separatorChar);
		if (pos == -1) {
			return "";
		}

		if (pos == (path.length() - 1)) {
			return getFileParent(path.substring(0, pos));
		}

		return path.substring(0, pos).trim();
	}

	public static String getFilePreName(String fileName) {

		String filePreName = fileName;
		int pos = fileName.lastIndexOf('.');
		if (pos != -1) {
			filePreName = fileName.substring(0, pos).trim();
		}

		return filePreName;
	}

	public static long getFileSize(String path) {

		File file = new File(path);
		if (!file.exists()) {
			return 0;
		}

		if (!file.isFile()) {
			return 0;
		}

		return file.length();
	}

	public static String getFileSubffix(String fileName) {

		String fileSubffix = null;
		int pos = fileName.lastIndexOf('.');
		if (pos != -1) {
			fileSubffix = fileName.substring(pos + 1).trim();
		}

		return fileSubffix;
	}

	public static long getIOReadFileByteNum() {
		return ioReadBytes.get();
	}

	public static int getIOReadFileCount() {
		return ioReadCount.get();
	}

	public static long getIOWriteFileByteNum() {
		return ioWriteBytes.get();
	}

	public static int getIOWriteFileCount() {
		return ioWriteCount.get();
	}

	public static long getLastModifiedTime(String filePath) {

		File file = new File(filePath);
		return file.exists() ? file.lastModified() : 0;
	}

	public static String getLocalHtmlFolder(String htmlPath) {
		return FileUtil.getFilePreName(htmlPath) + "_files";
	}

	public static String getMd5HashCode(InputStream fis) throws IOException, RException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			fis.close();

			byte[] md5Bytes = md.digest();
			BigInteger bigInt = new BigInteger(1, md5Bytes);
			return bigInt.toString(16);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RException(e.toString());
		}
	}

	public static String getMd5HashCode(List<String> lines) throws RException {

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			for (String line : lines) {

				if (line == null || line.isEmpty())
					continue;

				byte[] bytes = line.getBytes();
				int length = bytes.length;

				for (; length > 0; --length) {
					byte b = bytes[length - 1];
					if (b != EN_CHAR_ENTER && b != EN_CHAR_NEWLINE) {
						break;
					}
				}

				if (length == 0)
					continue;

				md.update(bytes, 0, length);
			}

			byte[] md5Bytes = md.digest();
			BigInteger bigInt = new BigInteger(1, md5Bytes);
			return bigInt.toString(16);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RException(e.toString());
		}

	}

	public static String getMd5HashCode(String filePath) throws IOException, RException {
		return getMd5HashCode(new FileInputStream(filePath));
	}

	public static String getMd5HashCode32(InputStream fis) throws IOException, RException {

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			fis.close();

			byte[] md5Bytes = md.digest();
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RException(e.toString());
		}

	}

	public static String getMd5HashCode32(String filePath) throws IOException, RException {
		return getMd5HashCode32(new FileInputStream(filePath));
	}

	public static String getPathName(String path) {

		path = _toValidFolderPath(path);

		int pos = path.lastIndexOf(File.separatorChar);
		if (pos == -1) {
			return path;
		}

		return path.substring(pos + 1);
	}

	public static String getPathParent(String path) {

		path = _toValidFolderPath(path);

		int pos = path.lastIndexOf(File.separatorChar);
		if (pos == -1) {
			return path;
		}

		return _toValidFolderPath(path.substring(0, pos));
	}

	public static void incIOReadFileBytes(long byteNum) {
		ioReadBytes.addAndGet(byteNum);
	}

	public static void incIOReadFileCount() {
		ioReadCount.incrementAndGet();
	}

	public static void incIOReadFileCount(int count) {
		ioReadCount.addAndGet(count);
	}

	public static void incIOWriteFileBytes(long byteNum) {
		ioWriteBytes.addAndGet(byteNum);
	}

	public static void incIOWriteFileCount() {
		ioWriteCount.incrementAndGet();
	}

	public static boolean isAbsPath(String path) {

		if (path == null) {
			return false;
		}

		path = path.trim();
		if (path.isEmpty()) {
			return false;
		}

		char firstChar = path.charAt(0);

		if (SystemUtil.getOSType() == OSType.Win) {

			if (path.length() < 3) {
				return false;
			}

			if (!path.subSequence(1, 3).equals(":\\")) {
				return false;
			}

			return firstChar >= 'C' && firstChar <= 'Z' && path.length() >= 3 && path.subSequence(1, 3).equals(":\\");

		} else {

			return firstChar == '/';
		}
	}

	public static boolean isExistDirectory(String path) {
		File file = new File(path);
		return file.exists() && file.isDirectory();
	}

	public static boolean isExistFile(String path) {

		File file = new File(path);
		return file.exists() && file.isFile();
	}

	public static boolean isExistFile(String path, String name) {

		if (path == null) {
			return false;
		}

		path = path.trim();
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}

		path += name.trim();
		return isExistFile(path);
	}

	static boolean isSupportFile(File file) {

		if (file == null || !file.exists()) {
			return false;
		}

		if (file.isDirectory()) {

			for (File f : file.listFiles()) {
				if (!isSupportFile(f)) {
					return false;
				}
			}
			return true;
		} else if (file.isFile()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean moveFile(File srcFile, File dstFolder) {

		if (srcFile == null || dstFolder == null || !isSupportFile(srcFile) || !dstFolder.exists()
				|| !dstFolder.isDirectory()) {
			return false;
		}

		// Check path valid
		// E:\x\a ==> E:\x\a\y
		if (dstFolder.getAbsolutePath().startsWith(srcFile.getAbsolutePath())) {
			return false;
		}

		String fileName = srcFile.getName();
		File dstFile = new File(dstFolder.getAbsolutePath() + File.separator + fileName);
		if (dstFile.exists()) {
			return false;
		}

		if (srcFile.isDirectory()) {

			if (!dstFile.mkdirs()) {
				return false;
			}
			for (File f : srcFile.listFiles()) {
				if (!moveFile(f, dstFile)) {
					return false;
				}
			}
			return srcFile.delete();
		}

		if (srcFile.isFile()) {
			return srcFile.renameTo(dstFile);
		}

		return false;
	}

	public static List<String> openTxtFile(String fileName) throws IOException {

		ArrayList<String> lineList = new ArrayList<>();
		long readBytes = 0;

		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				readBytes += line.length();
				lineList.add(line);
			}
		} finally {
			incIOReadFileCount();
			incIOReadFileBytes(readBytes);
		}

		return lineList;
	}

	public static List<String> openTxtFile(String fileName, String charset) throws IOException {

		if (charset == null) {
			return openTxtFile(fileName);
		}

		long readBytes = 0;
		ArrayList<String> lineList = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				readBytes += line.length();
				lineList.add(line);
			}
		} finally {
			incIOReadFileCount();
			incIOReadFileBytes(readBytes);
		}

		return lineList;
	}

	public static void reset() {

		ioReadBytes.set(0);
		ioReadCount.set(0);
		ioWriteBytes.set(0);
		ioWriteCount.set(0);
	}

	public static void saveTxtFile(String outPath, Collection<String> content) throws IOException {

		long writeBytes = 0;

		try (PrintStream out = new PrintStream(outPath)) {
			for (String line : content) {
				if (line != null) {
					out.println(line);
					writeBytes += line.length();
				}
			}
		}

		incIOWriteFileCount();
		incIOWriteFileBytes(writeBytes);
	}

	public static void saveTxtFile(String outPath, Collection<String> content, String charset) throws IOException {

		if (charset == null) {
			saveTxtFile(outPath, content);
			return;
		}

		long writeBytes = 0;

		try (PrintStream out = new PrintStream(outPath, charset)) {
			for (String line : content) {
				if (line != null) {
					out.println(line);
					writeBytes += line.length();
				}
			}
		}

		incIOWriteFileCount();
		incIOWriteFileBytes(writeBytes);
	}

	public static String toValidLinuxPath(String path, boolean isFolder) {

		if (path == null || path.isEmpty()) {
			path = new File("").getAbsolutePath();
		}

		StringBuffer sb = new StringBuffer();

		char lastC = 0;

		for (char c : path.toCharArray()) {

			switch (c) {
			case ' ':
				sb.append('\\');
				sb.append(c);
				break;
			default:
				sb.append(c);
			}

			lastC = c;
		}

		if (isFolder && lastC != File.separatorChar) {
			sb.append(File.separatorChar);
		}

		return sb.toString();
	}

	public static String toValidPath(String path) {

		if (path == null || path.isEmpty()) {
			path = new File("").getAbsolutePath();
		}

		if (path != null && !path.endsWith(File.separator))
			return path + File.separator;
		else
			return path;
	}

}
