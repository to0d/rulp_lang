/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import java.util.Collections;
import java.util.List;

import alpha.common.string.StringUtil;

public class SystemUtil {

	public enum OSType {
		AIX, Linux, Mac, Win
	}

	static List<String> envPaths = null;

	private static OSType osType = null;

	public static List<String> getEnvPaths() {

		if (envPaths == null) {

			String envName = SystemUtil.getOSType() == OSType.Win ? "Path" : "PATH";
			char sepChar = SystemUtil.getOSType() == OSType.Win ? ';' : ':';

			String pathList = System.getenv(envName);

			if (pathList == null) {
				envPaths = Collections.emptyList();
			} else {
				envPaths = StringUtil.splitStringByChar(pathList, sepChar);
			}
		}

		return envPaths;
	}

	public static OSType getOSType() {

		if (osType == null) {

			String osName = System.getProperty("os.name");

			if (osName.toLowerCase().indexOf("windows") >= 0) {
				osType = OSType.Win;
			} else if (osName.toLowerCase().indexOf("mac") >= 0) {
				osType = OSType.Mac;
			} else if (osName.toLowerCase().indexOf("aix") >= 0) {
				osType = OSType.AIX;
			} else {
				osType = OSType.Linux;
			}
		}

		return osType;
	}

}
