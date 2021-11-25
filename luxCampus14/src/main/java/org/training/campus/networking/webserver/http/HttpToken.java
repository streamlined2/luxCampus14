package org.training.campus.networking.webserver.http;

import java.util.regex.Pattern;

public enum HttpToken {

	SPACE(" ", Pattern.compile(" ")), END_OF_LINE("\r\n", Pattern.compile("\r\n")),
	WHITESPACE(" ", Pattern.compile("\s")), REQUEST_HEADER_SEPARATOR(": ", Pattern.compile(":\s*"));

	private final String value;
	private final Pattern pattern;

	private HttpToken(String value, Pattern pattern) {
		this.value = value;
		this.pattern = pattern;
	}

	public String getValue() {
		return value;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return value;
	}

}
