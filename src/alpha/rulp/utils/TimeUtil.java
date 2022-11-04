package alpha.rulp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	static SimpleDateFormat standardDateFormat = null;

	public static String formatStandardTime(long time) {

		if (standardDateFormat == null) {
			standardDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}

		Date date = new Date(time);
		return standardDateFormat.format(date);
	}

}
