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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SystemUtil {

	static class CmdProcReader implements Runnable {

		private InputStream is;

		private boolean run = false;

		private List<String> sb;

		public CmdProcReader(InputStream is, List<String> sb) {
			super();
			this.is = is;
			this.sb = sb;
		}

		@Override
		public void run() {

			run = true;

			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				String line = null;
				while (run && (line = br.readLine()) != null) {
					sb.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void stop() {
			this.run = false;
		}
	}

	public enum OSType {
		AIX, Linux, Mac, Win
	}

	static List<String> envPaths = null;

	private static OSType osType = null;

	public static int excuteOsCommand(String cmd, List<String> outBuf, List<String> errBuf)
			throws InterruptedException, IOException {

		Process proc = null;
		switch (SystemUtil.getOSType()) {
		case Mac:
		case Linux:
		case AIX:
			proc = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", cmd });
			break;
		case Win:
		default:
			proc = Runtime.getRuntime().exec(cmd);
		}

		CmdProcReader outReader = null;
		CmdProcReader errReader = null;

		if (outBuf != null) {
			outReader = new CmdProcReader(proc.getInputStream(), outBuf);
			new Thread(outReader).start();
		}

		if (errBuf != null) {
			errReader = new CmdProcReader(proc.getErrorStream(), errBuf);
			new Thread(errReader).start();
		}

		int rc = proc.waitFor();

		if (outReader != null) {
			outReader.stop();
		}
		if (errReader != null) {
			errReader.stop();
		}

		return rc;
	}

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

	public static Properties getOsEnv() throws Exception {

		Properties prop = new Properties();
		String OS = System.getProperty("os.name").toLowerCase();
		Process p = null;

		if (OS.indexOf("windows") > -1) {
			p = Runtime.getRuntime().exec("cmd /c set"); //
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			int i = line.indexOf("=");
			if (i > -1) {
				String key = line.substring(0, i);
				String value = line.substring(i + 1);
				prop.setProperty(key, value);
			}
		}
		return prop;
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
