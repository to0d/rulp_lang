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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import alpha.rulp.utils.SystemUtil.OSType;

public class FileUtil {

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
