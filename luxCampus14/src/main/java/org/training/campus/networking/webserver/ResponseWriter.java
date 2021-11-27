package org.training.campus.networking.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Optional;

import org.training.campus.networking.webserver.exception.ResponseFailedException;
import org.training.campus.networking.webserver.http.HttpToken;
import org.training.campus.networking.webserver.http.response.HttpResponse;
import org.training.campus.networking.webserver.http.response.ResponseHeader;
import org.training.campus.networking.webserver.io.Sink;

public class ResponseWriter {

	public void send(Sink sink, HttpResponse response) {
		try {
			Writer writer = sink.getWriter();
			writeStatusLine(writer, response);
			writeHeaderLines(writer, response);
			writeEmptyLine(writer);
			writeMessageBody(sink.getOutputStream(), response);
		} catch (IOException e) {
			throw new ResponseFailedException(e);
		}
	}

	private void writeStatusLine(Writer writer, HttpResponse response) throws IOException {
		writer.append(response.getProtocol()).append(HttpToken.SPACE.getValue())
				.append(String.valueOf(response.getStatusCode())).append(HttpToken.SPACE.getValue());
		final Optional<String> reason = response.getReason();
		if (reason.isPresent()) {
			writer.append(reason.get());
		}
		writer.append(HttpToken.END_OF_LINE.getValue());
	}

	private void writeHeaderLines(Writer writer, HttpResponse response) throws IOException {
		for (ResponseHeader header : response) {
			writeHeaderLine(writer, header);
		}
	}

	private void writeHeaderLine(Writer writer, ResponseHeader header) throws IOException {
		writer.append(header.name()).append(HttpToken.REQUEST_HEADER_SEPARATOR.getValue()).append(header.value())
				.append(HttpToken.END_OF_LINE.getValue());
	}

	private void writeEmptyLine(Writer writer) throws IOException {
		writer.append(HttpToken.END_OF_LINE.getValue());
	}

	private void writeMessageBody(OutputStream os, HttpResponse response) {
		response.getMessageBody().ifPresent(body -> {
			try {
				body.getInputStream().transferTo(os);
			} catch (IOException e) {
				throw new ResponseFailedException(e);
			}
		});
	}

}
