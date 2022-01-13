package alpha.rulp.lang;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/* Copyright Prolog                                  */

/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

public class RException extends Exception {

	private static final long serialVersionUID = -5868320437069359308L;

	protected List<String> additionalMessages;

	protected IRObject fromObject;

	public RException() {
		super();
	}

	public RException(IRObject fromObject) {
		super();
		this.fromObject = fromObject;
	}

	public RException(IRObject fromObject, String message) {
		super(message);
		this.fromObject = fromObject;
	}

	public RException(String message) {
		super(message);
	}

	public void addMessage(String message) {

		if (additionalMessages == null) {
			additionalMessages = new LinkedList<>();
		}

		additionalMessages.add(message);
	}

	public List<String> getAdditionalMessages() {
		return additionalMessages == null ? Collections.emptyList() : additionalMessages;
	}

	public IRObject getFromObject() {
		return fromObject;
	}

	public String getExceptionMessage() {
		return super.getMessage();
	}

	@Override
	public String getMessage() {

		String message = super.getMessage();
		if (additionalMessages != null) {
			for (String msg : additionalMessages) {
				message += "\n" + msg;
			}
		}

		return message;
	}

}