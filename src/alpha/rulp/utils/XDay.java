package alpha.rulp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import alpha.rulp.lang.RException;
import alpha.rulp.string.StringMatchUtil;
import alpha.rulp.string.StringMatchUtil.IMatchParser;

public class XDay implements Comparable<XDay> {

	static IMatchParser parser = null;

	static {

		try {
			parser = StringMatchUtil.getMatchParser("%?-%?-%?");
		} catch (RException e) {
			e.printStackTrace();
		}
	}

	public static XDay getDay(int year, int month, int day) {

		if (year < 1970 || month < 1 || month > 12 || day < 1 || day > 31) {
			return null;
		}

		return new XDay(year, month, day);
	}

	public static XDay getDay(String dayString) throws RException {

		if (dayString == null) {
			return null;
		}

		if (!isValidDate(dayString)) {
			throw new RException("Invaild date format[" + dayString + "]");
		}

		return toDay(dayString);
	}

	public static int getDiff(XDay a, XDay b) throws RException {

		if (a.getNumDay() > b.getNumDay()) {
			return -getDiff(b, a);
		} else if (a.getNumDay() == b.getNumDay()) {
			return 0;
		}

		int diff = 1;
		XDay next = a.nextDay();
		while (next.getNumDay() != b.getNumDay()) {
			next = next.nextDay();
			++diff;
		}

		return diff;
	}

	public static XDay getMinDay(int year, int quater) throws RException {
		return XDay.getDay(String.format("%04d-%02d-01", year, (quater - 1) * 3 + 1));
	}

	public static String getSpecifiedDay(String dayString, int num) throws RException {

		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(dayString);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RException("Invaild date formate:" + dayString);
		}

		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + num);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

		return dayBefore;
	}

	public static boolean isValidDate(String dayString) {

		if (dayString == null)
			return false;

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			// parse the inDate parameter
			dateFormat.parse(dayString.trim());

		} catch (ParseException pe) {
			pe.printStackTrace();
			return false;
		}

		return true;
	}

	public static ArrayList<XDay> listDaysInQuarter(int year, int quarter) throws RException {

		if (quarter < 1 || quarter > 4) {
			throw new RException("Invaild quarter: " + quarter);
		}

		int month = (quarter - 1) * 3 + 1;

		XDay day = XDay.getDay(String.format("%d-%d-01", year, month));

		ArrayList<XDay> days = new ArrayList<>();

		while (day.getYear() == year && day.getQuarter() == quarter) {

			if (day.isValidDate()) {
				days.add(day);
			}

			day = day.nextDay();
		}

		return days;
	}

	public static XDay max(XDay a, XDay b) {
		return a.getNumDay() > b.getNumDay() ? a : b;
	}

	public static XDay min(XDay a, XDay b) {

		return a.getNumDay() > b.getNumDay() ? a : b;
	}

	public static XDay today() {

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);

		XDay today = new XDay(year, month + 1, day);
		if (!today.isValidDate()) {
			throw new RuntimeException("Invaild date formate:" + today.toString());
		}

		return today;
	}

	protected static XDay toDay(String dayString) throws RException {

		ArrayList<String> result = new ArrayList<>();

		// yyyy-MM-dd
		if (!parser.match(dayString, result, false) || result.size() != 3) {
			throw new RException("Invaild date formate:" + dayString);
		}

		try {

			int year = Integer.valueOf(result.get(0));
			int month = Integer.valueOf(result.get(1));
			int day = Integer.valueOf(result.get(2));
			return new XDay(year, month, day);

		} catch (Exception e) {
			throw new RException(dayString + ":" + e.toString());
		}
	}

	private int day = 0;

	private int dayOfWeek = -1;

	private int month = 0;

	private int year = 0;

	private XDay(int year, int month, int day) {

		super();
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public int compareTo(XDay o) {
		return this.getNumDay() - o.getNumDay();
	}

	@Override
	public boolean equals(Object day) {
		if (day instanceof XDay) {
			return this.getNumDay() == ((XDay) day).getNumDay();
		} else {
			return super.equals(day);
		}

	}

	public XDay getClone() {
		return new XDay(this.year, this.month, this.day);
	}

	public int getDay() {
		return day;
	}

	public int getDayOfWeek() throws RException {

		if (dayOfWeek == -1) {

			Calendar c = Calendar.getInstance();
			String dayString = toString();
			Date date = null;
			try {
				date = new SimpleDateFormat("yy-MM-dd").parse(dayString);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RException("Invaild date formate:" + dayString);
			}

			c.setTime(date);
			dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}

		return dayOfWeek;
	}

	public int getMonth() {
		return month;
	}

	public int getNumDay() {
		return (year - 1900) * 1000 + (month - 1) * 31 + day;
	}

	public int getQuarter() {
		return (month - 1) / 3 + 1;
	}

	public int getYear() {
		return year;
	}

	public boolean isBiggerThan(XDay day) {
		return this.getNumDay() > day.getNumDay();
	}

	public boolean isSmallThan(XDay day) {
		return this.getNumDay() < day.getNumDay();
	}

	public boolean isValidDate() {
		return (month >= 1 && month <= 12) && (day >= 1 && day <= 31) && isValidDate(toString());
	}

	public boolean isWorkingDay() throws RException {

		return getDayOfWeek() >= 1 && getDayOfWeek() <= 5;
	}

	public XDay lastDay() throws RException {
		return moveDay(-1);
	}

	public XDay moveDay(int n) throws RException {

		String nextDayString = getSpecifiedDay(toString(), n);

		if (!isValidDate(nextDayString)) {
			throw new RException("Invaild date formate:" + nextDayString);
		}

		return toDay(nextDayString);
	}

	public XDay nextDay() throws RException {
		return moveDay(1);
	}

	@Override
	public String toString() {
		return String.format("%d-%02d-%02d", year, month, day);
	}
}
