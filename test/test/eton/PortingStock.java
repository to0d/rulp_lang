package test.eton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import alpha.common.string.StringUtil;
import alpha.rulp.utils.FileUtil;

public class PortingStock {

	public static void process_corp_info(String path) throws IOException {

		ArrayList<String> lines = new ArrayList<>();

		int index = 0;

		for (String line : FileUtil.openTxtFile(path, "utf-8")) {

			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}

			if (!line.startsWith("\"")) {
				throw new IOException("1 invalid line " + index + ": " + line);
			}

			int pos = line.indexOf(" ");
			String day = line.substring(1, pos);
			if (!day.equals("2016-01-26")) {
				throw new IOException("2 invalid line " + index + ": " + line + ",day=" + day);
			}

			line = line.substring(pos + 1).trim();
			pos = line.indexOf(" ");

			int id = Integer.valueOf(line.substring(0, pos));
			if (id < 0 || id > 3000) {
				throw new IOException("3 invalid line " + index + ": " + line);
			}

			if (!codeMap.containsKey(id)) {
				throw new IOException("invalid key: " + index + ": " + id);
			}

			String code = codeMap.get(id);

			line = line.substring(pos + 1).trim();
			pos = line.indexOf(" ");
			String key = line.substring(0, pos);
			if (!key.startsWith("\"") || !key.endsWith("\"")) {
				throw new IOException("4 invalid line " + index + ": " + line);
			}

			switch (key.substring(1, key.length() - 1)) {
			case "公司名称":
			case "公司英文名称":
			case "成立日期":
			case "机构类型":
			case "组织形式":
			case "公司电话":
			case "公司传真":
			case "公司电子邮箱":
			case "公司网站":
			case "注册地址":
			case "注册资本":
			case "办公地址":
			case "公司邮政编码":
			case "公司简介":
			case "经营范围":
				break;
			default:
				throw new IOException("5 invalid line " + index + ": " + line);

			}

			String value = line.substring(pos + 1).trim();
			if (!value.startsWith("\"") || !value.endsWith("\"")) {
				throw new IOException("6 invalid line " + index + ": " + line);
			}

			value = value.substring(1, value.length() - 1);

//			System.out.println(line.substring(0, Math.min(10, line.length())));

			lines.add(String.format("\"%s\" \"%s\" %s \"%s\"", day, code, key, StringUtil.addEscape(value)));
			++index;
		}

		FileUtil.saveTxtFile(path + ".2", lines, "utf-8");
	}

	public static void process_people_info(String path) throws IOException {

		ArrayList<String> lines = new ArrayList<>();

		int index = 0;

		for (String line : FileUtil.openTxtFile(path, "utf-8")) {

			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}

			if (!line.startsWith("\"")) {
				throw new IOException("1 invalid line " + index + ": " + line);
			}

			int pos = line.indexOf(" ");
			String day = line.substring(1, pos - 1);
			if (!day.equals("2016-01-26")) {
				throw new IOException("2 invalid line " + index + ": " + line + ",day=" + day);
			}

			line = line.substring(pos + 1).trim();
			pos = line.indexOf(" ");

			int id = Integer.valueOf(line.substring(0, pos));
			if (id < 0 || id > 3000) {
				throw new IOException("3 invalid line " + index + ": " + line);
			}

			if (!codeMap.containsKey(id)) {
				throw new IOException("invalid key: " + index + ": " + id);
			}

			String code = codeMap.get(id);

			line = line.substring(pos + 1).trim();
			pos = line.indexOf(" ");
			String key = line.substring(0, pos);
			if (!key.startsWith("\"") || !key.endsWith("\"")) {
				throw new IOException("4 invalid line " + index + ": " + line);
			}

			switch (key.substring(1, key.length() - 1)) {
			case "董秘传真":
			case "董秘电话":
			case "董秘姓名":
				break;
			default:
				throw new IOException("5 invalid line " + index + ": " + line);

			}

			String value = line.substring(pos + 1).trim();
			if (!value.startsWith("\"") || !value.endsWith("\"")) {
				throw new IOException("6 invalid line " + index + ": " + line);
			}

			value = value.substring(1, value.length() - 1);

//			System.out.println(line.substring(0, Math.min(10, line.length())));

			lines.add(String.format("\"%s\" \"%s\" %s \"%s\"", day, code, key, StringUtil.addEscape(value)));
			++index;
		}

		FileUtil.saveTxtFile(path + ".2", lines, "utf-8");
	}

	static Map<Integer, String> codeMap;

	public static void main(String[] args) throws IOException {

//		codeMap = processCode("C:\\data\\eton\\code.txt");
		process_daily("D:\\data\\stock\\backup\\stock0000_2016_01_26.sql");

//		int index = 0;
//		try (BufferedReader in = new BufferedReader(
//				new InputStreamReader(new FileInputStream("D:\\data\\stock\\backup\\stock0000_2016_01_26.sql"), "utf-8"))) {
//			String line = null;
//			while ((line = in.readLine()) != null) {
//
//				System.out.println(line);
//
//				if (index++ > 5000) {
//					return;
//				}
//			}
//		}
	}

	public static void process_daily(String path) throws IOException {

		int index = 0;

		ArrayList<String> otherLines = new ArrayList<>();
		ArrayList<String> lines = null;
		String name = null;

		for (String line : FileUtil.openTxtFile(path, "utf-8")) {

			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}

			if (lines == null) {

				ArrayList<String> names = new ArrayList<>();
				if (StringUtil.matchFormat("REPLACE INTO `%?` (`day`, `hp`, `lp`, `op`, `cp`, `tv`, `ta`) VALUES",
						line.trim(), names) && names.size() == 1) {

					name = names.get(0);
					lines = new ArrayList<>();

				} else {
					otherLines.add(line);
				}

			} else {

				boolean end = false;

				if (line.startsWith("(") && line.endsWith(");")) {
					end = true;
					line = line.substring(1, line.length() - 2);
				} else if (line.startsWith("(") && line.endsWith("),")) {
					line = line.substring(1, line.length() - 2);
				} else {
					throw new IOException("invalid line " + index + ": " + line);
				}

				ArrayList<String> array = new ArrayList<>();
				if (!StringUtil.matchFormat("'%?', %?, %?, %?, %?, %?, %?", line, array) || array.size() != 7) {
					throw new IOException("invalid line " + index + ": " + line);
				}

				lines.add(String.format("\"%s\" %s %s %s %s %sL %sL", array.get(0), array.get(1), array.get(2),
						array.get(3), array.get(4), array.get(5), array.get(6)));

				if (end) {
					System.out.println("save:" + name);
					FileUtil.saveTxtFile("C:\\data\\eton\\stock2\\" + name + ".7.mc", lines, "utf-8");
					lines = null;
				}

			}

			index++;
		}

		FileUtil.saveTxtFile(path + ".2", otherLines, "utf-8");
	}

	public static void process_1(String path) throws IOException {

		ArrayList<String> lines = new ArrayList<>();

		for (String line : FileUtil.openTxtFile(path, "utf-8")) {

			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}

			lines.add(line.trim() + "L");
		}

		FileUtil.saveTxtFile(path + ".2", lines, "utf-8");
	}

	public static Map<Integer, String> processCode(String path) throws IOException {

		Map<Integer, String> map = new HashMap<>();

		int index = 0;

		for (String line : FileUtil.openTxtFile(path, "utf-8")) {

			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}

			int pos = line.indexOf(" ");
			String code = line.substring(0, pos);
			int nc = Integer.valueOf(code);
			if (nc < 0 || nc > 900957) {
				throw new IOException("invalid line " + index + ": " + line);
			}

			int id = Integer.valueOf(line.substring(pos + 1));
			if (id < 0 || id > 3000) {
				throw new IOException("invalid line " + index + ": " + line);
			}

			map.put(id, code);

			++index;
		}

		return map;
	}
}
