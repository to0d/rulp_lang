/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.A_NIL;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LOOP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.ximpl.control.XRFactorLoop;

public class FormatUtil {

	static class Loc {
		int deep;
		int num;
		int width;

		public Loc(int deep, int num, int width) {
			super();
			this.deep = deep;
			this.num = num;
			this.width = width;
		}
	}

	static final int MAX_LEN = 80;

	static Loc nil_loc = new Loc(0, 1, A_NIL.length());

	static Map<IRObject, Loc> objLocMap = new HashMap<>();

	static String spaceLines[] = new String[1024];

	private static Loc _getLoc(IRObject obj) throws RException {

		if (obj == null) {
			throw new RException("null obj");
		}

		Loc loc = objLocMap.get(obj);
		if (loc == null) {
			switch (obj.getType()) {
			case EXPR:
			case LIST:

				int deep = 0;
				int num = 0;
				int width = 2;

				IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
				while (it.hasNext()) {
					Loc cLoc = _getLoc(it.next());
					deep = Math.max(deep, cLoc.deep);
					width += cLoc.width + 1;
					num++;
				}

				loc = new Loc(deep + 1, num, width);

				break;
			default:
				loc = new Loc(0, 1, toString(obj).length());
				break;
			}

			objLocMap.put(obj, loc);
		}

		return loc;
	}

	private static String _getSpaceLine(int len) {

		if (len < 0) {
			len = 0;
		}

		String line = spaceLines[len];
		if (line == null) {

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < len * 4; ++i) {
				sb.append(' ');
			}
			line = sb.toString();
			spaceLines[len] = line;
		}

		return line;
	}

	private static void _output(IRObject obj, List<String> outLines) throws RException {
		objLocMap.clear();
		_output(obj, outLines, 0);
	}

	private static void _output(IRObject obj, List<String> outLines, int level) throws RException {

		if (obj.getType() == RType.EXPR) {

			switch (((IRList) obj).get(0).asString()) {
			case F_IF:
				_output_if((IRList) obj, outLines, level);
				return;
			case F_DEFUN:
				_output_defun((IRList) obj, outLines, level);
				return;
			case F_LOOP:
				_output_loop((IRList) obj, outLines, level);
				return;
			case "add-rule":
				_output_add_rule((IRList) obj, outLines, level);
				return;
			}
		}

		boolean outputOneLine = false;

		Loc cLoc = _getLoc(obj);
		switch (obj.getType()) {
		case EXPR:
		case LIST:
			if (cLoc.width < MAX_LEN) {
				outputOneLine = true;
			} else {
				outputOneLine = false;
			}
			break;
		default:
			outputOneLine = true;
		}

		if (outputOneLine) {
			outLines.add(_getSpaceLine(level) + toString(obj));
			return;
		}

		IRList list = (IRList) obj;

		IRObject e0 = list.get(0);
		Loc l0 = _getLoc(e0);

		String head = obj.getType() == RType.EXPR ? "(" : "'(";
		IRIterator<? extends IRObject> it;
		// first element is simple
		if (l0.num == 1 || (l0.width < MAX_LEN)) {
			outLines.add(_getSpaceLine(level) + head + toString(e0));
			it = list.listIterator(1);
		} else {
			outLines.add(_getSpaceLine(level) + head);
			it = list.listIterator(0);
		}

		while (it.hasNext()) {
			_output(it.next(), outLines, level + 1);
		}

		outLines.add(_getSpaceLine(level) + ")");
	}

	private static void _output_add_rule(IRList expr, List<String> outLines, int level) throws RException {

		String firsLine = "(";
		int size = expr.size();
		int index = 0;

		// syntax: (add-rule ["name"] model if cond1 cond2 .. do action1 action2)

		int formatType = 0;

		for (; index < size; ++index) {

			IRObject obj = expr.get(index);

			if (obj.getType() == RType.EXPR) {
				formatType = 1;
				break;
			}

			if (RulpUtil.isAtom(obj, F_IF)) {
				formatType = 2;
				break;
			}

			if (index != 0) {
				firsLine += " ";
			}

			firsLine += toString(obj);
		}

		outLines.add(_getSpaceLine(level) + firsLine);
		for (; index < size; ++index) {

			IRObject obj = expr.get(index);
			if (RulpUtil.isAtom(obj, F_IF) || RulpUtil.isAtom(obj, A_DO)) {
				_output(obj, outLines, level + 1);
			} else {
				_output(obj, outLines, level + 2);
			}

		}

		outLines.add(_getSpaceLine(level) + ")");
	}

	private static void _output_defun(IRList expr, List<String> outLines, int level) throws RException {

		String firsLine = "(";
		int size = expr.size();
		for (int index = 0; index < size - 1; ++index) {
			if (index != 0) {
				firsLine += " ";
			}
			firsLine += toString(expr.get(index));
		}

		outLines.add(_getSpaceLine(level) + firsLine);
		_output(expr.get(size - 1), outLines, level + 1);
		outLines.add(_getSpaceLine(level) + ")");
	}

	private static void _output_if(IRList expr, List<String> outLines, int level) throws RException {

		outLines.add(_getSpaceLine(level) + "(" + toString(expr.get(0)));

		for (int i = 1; i < expr.size(); ++i) {
			_output(expr.get(i), outLines, level + 1);
		}

		outLines.add(_getSpaceLine(level) + ")");
	}

	private static void _output_loop(IRList expr, List<String> outLines, int level) throws RException {

		// (loop for x in '(1 2 3) do
		// action
		// )
		if (XRFactorLoop.isLoop1(expr)) {

			int size = expr.size();

			String line1 = _getSpaceLine(level) + "(" + toString(expr.get(0));
			for (int i = 1; i < 6; ++i) {
				line1 += " " + toString(expr.get(i));
			}
			outLines.add(line1);

			for (int i = 6; i < size; ++i) {
				_output(expr.get(i), outLines, level + 1);
			}
			outLines.add(_getSpaceLine(level) + ")");

			return;
		}

		// (loop for x from 1 to 3 do
		// action
		// )
		if (XRFactorLoop.isLoop2(expr)) {

			int size = expr.size();

			String line1 = _getSpaceLine(level) + "(" + toString(expr.get(0));
			for (int i = 1; i < 8; ++i) {
				line1 += " " + toString(expr.get(i));
			}
			outLines.add(line1);

			for (int i = 8; i < size; ++i) {
				_output(expr.get(i), outLines, level + 1);
			}
			outLines.add(_getSpaceLine(level) + ")");

			return;
		}

		// (loop
		// action
		// )

		outLines.add(_getSpaceLine(level) + "(" + toString(expr.get(0)));

		for (int i = 1; i < expr.size(); ++i) {
			_output(expr.get(i), outLines, level + 1);
		}

		outLines.add(_getSpaceLine(level) + ")");
	}

	public static void format(IRObject obj, List<String> outLines, int level) throws RException {
		objLocMap.clear();
		_output(obj, outLines, level);
	}

	public static List<String> format(List<String> lines) throws RException {

		ArrayList<String> outLines = new ArrayList<>();

		IRParser parser = RulpFactory.createParser();

		int size = lines.size();
		int index = 0;

		StringBuffer sb = new StringBuffer();
		int lastInCompletedIndex = -1;

		NEXT_LINE: for (; index < size; ++index) {

			String line = lines.get(index);

			if (line.trim().isEmpty() || line.trim().startsWith(";")) {
				if (lastInCompletedIndex == -1) {
					outLines.add(line);
					continue;
				} else {
					throw new RException(String.format("Not support internal comment yet: line=%d, %s", index, line));
				}
			}

			if (lastInCompletedIndex != -1) {
				sb.append("\n");
			}

			sb.append(line);

			List<IRObject> objs = new ArrayList<>();

			try {
				objs.addAll(parser.parse(sb.toString()));
			} catch (RException e) {
				// statements maybe not in-completed
				if (lastInCompletedIndex == -1) {
					lastInCompletedIndex = index;
				}

				continue NEXT_LINE;
			}

			for (IRObject obj : objs) {
				_output(obj, outLines);
			}

			lastInCompletedIndex = -1;
			sb.setLength(0);
		}

		if (lastInCompletedIndex != -1) {
			throw new RException(String.format("Incomplete line found: line=%d, %s", lastInCompletedIndex,
					lines.get(lastInCompletedIndex)));
		}

		return outLines;
	}

	public static String toString(IRObject obj) throws RException {
		return RulpUtil.toString(obj, true);
	}
}
