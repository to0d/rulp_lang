package alpha.rulp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JarUtil {

	public static boolean containFileInJar(String path) {
		return JarUtil.class.getClassLoader().getResource(path) != null;
	}

	public static List<String> openTxtFileFromJar(String path, String charset) throws IOException {

		long readBytes = 0;
		ArrayList<String> lineList = new ArrayList<String>();

		try (InputStream is = JarUtil.class.getClassLoader().getResourceAsStream(path);
				BufferedReader in = charset == null ? new BufferedReader(new InputStreamReader(is))
						: new BufferedReader(new InputStreamReader(is, charset))) {

			String line = null;
			while ((line = in.readLine()) != null) {
				readBytes += line.length();
				lineList.add(line);
			}
		}

		FileUtil.incIOReadFileCount();
		FileUtil.incIOReadFileBytes(readBytes);

		return lineList;
	}
}
