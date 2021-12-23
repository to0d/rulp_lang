package main;

import java.util.ArrayList;
import java.util.Scanner;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.FormatUtil;

public class DoFormatRulp {

	public static void main(String[] args) throws RException {

		ArrayList<String> lines = new ArrayList<>();

		try (Scanner input = new Scanner(System.in)) {
			while (input.hasNext()) {
				lines.add(input.nextLine());
			}
		}

		for (String line : FormatUtil.format(lines)) {
			System.out.println(line);
		}
	}

}
