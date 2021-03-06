package org.training.campus.networking.webserver.http.request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.training.campus.networking.webserver.exception.MalformedRequestException;
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

	public int getContentSize() {
		try {
			int size = 0;
			for (var header : this) {
				if (header.hasContentSize()) {
					size += Integer.parseInt(header.value());
				}
			}
			return size;
		} catch (NumberFormatException e) {
			throw new MalformedRequestException(e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof HttpRequest r) {
			return receivedTime.equals(r.receivedTime) && Objects.equals(url, r.url) && method == r.method
					&& Objects.equals(protocolVersion, r.protocolVersion);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(receivedTime, url, method, protocolVersion);
	}

	@Override
	public String toString() {
		final var join = new StringJoiner(" ");
		join.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(receivedTime)).add(method.name()).add(url)
				.add(protocolVersion).add("\n");
		forEach(header -> join.add(header.toString()).add("\n"));
		messageBody.ifPresent(body -> {
			join.add(new String(body.toByteArray())).add("\n");
		});
		return join.toString();
	}

	@Override
	public Iterator<RequestHeader> iterator() {
		return headers.values().iterator();
	}

	@Override
	public void close() throws Exception {
		if (messageBody.isPresent()) {
			messageBody.get().close();
			messageBody = Optional.empty();
		}
	}

}
