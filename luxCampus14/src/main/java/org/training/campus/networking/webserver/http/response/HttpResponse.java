package org.training.campus.networking.webserver.http.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpResponse implements Iterable<ResponseHeader>, AutoCloseable {

	private final LocalDateTime formedTime;
	private final String protocol;
	private final StatusClass statusClass;
	private final int statusCode;
	private final Map<String, ResponseHeader> headers;
	private Optional<String> reason;
	private Optional<ResponseMessageBody> messageBody;

	public HttpResponse(String protocol, int statusCode) {
		this.protocol = Objects.requireNonNull(protocol, "protocol shouldn't be null");
		this.statusClass = StatusClass.getStatusClass(statusCode);
		this.statusCode = statusCode;
		this.headers = new HashMap<>();
		this.reason = Optional.empty();
		this.messageBody = Optional.empty();
		this.formedTime = LocalDateTime.now();
	}

	public LocalDateTime getFormedTime() {
		return formedTime;
	}

	public String getProtocol() {
		return protocol;
	}

	public StatusClass getStatusClass() {
		return statusClass;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public Optional<String> getReason() {
		return reason;
	}

	public void setReason(Optional<String> reason) {
		this.reason = reason;
	}

	public Optional<ResponseMessageBody> getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(Optional<ResponseMessageBody> messageBody) {
		this.messageBody = messageBody;
	}

	public void addHeader(String header, String value) {
		headers.put(header, new ResponseHeader(header, value));
	}

	public Optional<ResponseHeader> getHeader(String header) {
		return Optional.ofNullable(headers.get(header));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof HttpResponse r) {
			return formedTime.equals(r.formedTime) && Objects.equals(protocol, r.protocol)
					&& statusCode == r.statusCode;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(formedTime, protocol, statusCode);
	}

	@Override
	public String toString() {
		return new StringJoiner(",", "[", "]").add(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(formedTime))
				.add(protocol).add(statusClass.name()).add(String.valueOf(statusCode)).toString();
	}

	@Override
	public Iterator<ResponseHeader> iterator() {
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
