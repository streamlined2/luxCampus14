package org.training.campus.networking.webserver.http.response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.training.campus.networking.webserver.exception.MalformedRequestException;
import org.training.campus.networking.webserver.exception.ResponseFailedException;

public class HttpResponse implements Iterable<ResponseHeader>, AutoCloseable {

	private final LocalDateTime formedTime;
	private final String protocol;
	private final StatusClass statusClass;
	private final int statusCode;
	private Optional<String> reason;
	private Optional<ResponseMessageBody> messageBody;
	private final Map<String, ResponseHeader> headers;

	public HttpResponse(String protocol, int statusCode) {
		this.protocol = Objects.requireNonNull(protocol, "protocol shouldn't be null");
		this.statusClass = StatusClass.getStatusClass(statusCode);
		this.statusCode = statusCode;
		this.headers = new HashMap<>();
		this.reason = Optional.empty();
		this.messageBody = Optional.empty();
		this.formedTime = LocalDateTime.now();
	}

	public HttpResponse(String protocol, int statusCode, String reason, ResponseMessageBody messageBody) {
		this(protocol, statusCode);
		setReason(reason);
		setMessageBody(messageBody);
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

	public void setReason(String reason) {
		this.reason = Optional.of(Objects.requireNonNull(reason, "reason shouldn't be null"));
	}

	public Optional<ResponseMessageBody> getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(ResponseMessageBody messageBody) {
		this.messageBody = Optional.of(Objects.requireNonNull(messageBody, "message body shouldn't be null"));
	}

	public void addHeader(String header, String value) {
		headers.put(header, new ResponseHeader(header, value));
	}

	public void addHeader(ResponseHeader.HeaderType headerType, String value) {
		headers.put(headerType.getName(), new ResponseHeader(headerType.getName(), value));
	}

	public Optional<ResponseHeader> getHeader(String header) {
		return Optional.ofNullable(headers.get(header));
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
		}catch (NumberFormatException e) {
			throw new MalformedRequestException(e);
		}
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
		final var join = new StringJoiner(" ");
		join.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(formedTime)).add(protocol).add(statusClass.name())
				.add(String.valueOf(statusCode));
		reason.ifPresent(r -> join.add(r));
		join.add("\n");
		forEach(header -> join.add(header.toString()).add("\n"));
		messageBody.ifPresent(body -> {
			try {
				join.add(new String(body.getInputStream().readAllBytes())).add("\n");
			} catch (IOException e) {
				throw new ResponseFailedException(e);
			}
		});
		return join.toString();
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
