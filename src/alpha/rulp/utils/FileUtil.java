/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import alpha.rulp.utils.SystemUtil.OSType;

public class FileUtil {

	static AtomicLong ioReadBytes = new AtomicLong(0);

	static AtomicInteger ioReadCount = new AtomicInteger(0);

	static AtomicLong ioWriteBytes = new AtomicLong(0);

	static AtomicInteger ioWriteCount = new AtomicInteger(0);

	public static void reset() {

		ioReadBytes.set(0);
		ioReadCount.set(0);
		ioWriteBytes.set(0);
		ioWriteCount.set(0);

	}

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

	public static boolean containFileInJar(String path) {
		return RulpUtil.class.getClassLoader().getResource(path) != null;
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

	public static String getFilePreName(String fileName) {

		String filePreName = fileName;
		int pos = fileName.lastIndexOf('.');
		if (pos != -1) {
			filePreName = fileName.substring(0, pos).trim();
		}

		return filePreName;
	}

	public static String getFileSubffix(String fileName) {

		String fileSubffix = null;
		int pos = fileName.lastIndexOf('.');
		if (pos != -1) {
			fileSubffix = fileName.substring(pos + 1).trim();
		}

		return fileSubffix;
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

	public static List<String> openTxtFile(String fileName) throws IOException {

		ArrayList<String> lineList = new ArrayList<>();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				lineList.add(line);
			}
		}

		return lineList;
	}

	public static List<String> openTxtFile(String fileName, String charset) throws IOException {

		if (charset == null) {
			return openTxtFile(fileName);
		}

		ArrayList<String> lineList = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				lineList.add(line);
			}
		}

		return lineList;
	}

	public static ArrayList<String> openTxtFileFromJar(String path, String charset) throws IOException {

		ArrayList<String> lineList = new ArrayList<String>();

		try (InputStream is = RulpUtil.class.getClassLoader().getResourceAsStream(path);
				BufferedReader in = charset == null ? new BufferedReader(new InputStreamReader(is))
						: new BufferedReader(new InputStreamReader(is, charset))) {

			String line = null;
			while ((line = in.readLine()) != null) {
				lineList.add(line);
			}
		}

		return lineList;
	}

	public static void saveTxtFile(String outPath, Collection<String> content) throws IOException {

		try (PrintStream out = new PrintStream(outPath)) {
			for (String line : content) {
				if (line != null) {
					out.println(line);
				}
			}
		}

	}

	public static void saveTxtFile(String outPath, Collection<String> content, String charset) throws IOException {

		if (charset == null) {
			saveTxtFile(outPath, content);
			return;
		}

		try (PrintStream out = new PrintStream(outPath, charset)) {
			for (String line : content) {
				if (line != null) {
					out.println(line);
				}
			}
		}
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
