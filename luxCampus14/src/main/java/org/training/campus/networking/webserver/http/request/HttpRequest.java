package org.training.campus.networking.webserver.http.request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.training.campus.networking.webserver.http.HttpMethod;

public class HttpRequest implements Iterable<RequestHeader>, AutoCloseable {

	private final LocalDateTime receivedTime;
	private final HttpMethod method;
	private final String url;
	private final String protocolVersion;
	private final Map<String, RequestHeader> headers;
	private Optional<RequestMessageBody> messageBody;

	public HttpRequest(String url, HttpMethod method, String protocolVersion) {
		this.url = Objects.requireNonNull(url, "request URL shouldn't be null");
		this.method = Objects.requireNonNull(method, "request method shouldn't be null");
		this.protocolVersion = Objects.requireNonNull(protocolVersion, "protocol version shouldn't be null");
		headers = new HashMap<>();
		messageBody = Optional.empty();
		this.receivedTime = LocalDateTime.now();
	}

	public LocalDateTime getReceivedTime() {
		return receivedTime;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public Optional<RequestHeader> getHeader(String header) {
		return Optional.ofNullable(headers.get(header));
	}

	public void addHeader(String headerName, String value) {
		headers.put(Objects.requireNonNull(headerName, "header name shouldn't be null"),
				new RequestHeader(headerName, value));
	}

	public void addHeader(String headerName, RequestHeader header) {
		headers.put(Objects.requireNonNull(headerName, "header name shouldn't be null"), header);
	}

	public Optional<RequestMessageBody> getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(Optional<RequestMessageBody> messageBody) {
		this.messageBody = messageBody;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof HttpRequest r) {
			return receivedTime.equals(r.receivedTime) && Objects.equals(url, r.url) && method == r.method;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(receivedTime, url, method);
	}

	@Override
	public String toString() {
		return new StringJoiner(" ", "[", "]").add(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(receivedTime))
				.add(method.name()).add(url).add(protocolVersion).toString();
	}

	@Override
	public Iterator<RequestHeader> iterator() {
		return headers.values().iterator();
	}

	@Override
	public void close() throws Exception {
		if (messageBody.isPresent()) {
			messageBody.get().close();
		}
	}

}
