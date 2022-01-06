/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.runtime.XRInterpreter;

public class XRFactorDate extends AbsAtomFactorAdapter implements IRFactor {

	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	static {
		dateFormat.setLenient(false);
	}

	public XRFactorDate(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() > 2) {
			throw new RException("Invalid parameters: " + args);
		}

		int year = 0;
		int month = 0;
		int day = 0;

		if (args.size() == 2) {

			String dayString = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();

			try {

				Date date = dateFormat.parse(dayString.trim());
				return RulpFactory.createString(dateFormat.format(date));

			} catch (ParseException e) {
				if (XRInterpreter.TRACE) {
					e.printStackTrace();
				}

				throw new RException(e.toString());
			}

		} else {

			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DATE);

			return RulpFactory.createString(String.format("%4d-%02d-%02d", year, month, day));

		}
	}

	public boolean isThreadSafe() {
		return true;
	}
}