package org.training.campus.networking.webserver.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Optional;

import org.training.campus.networking.webserver.exception.ResponseFailedException;
import org.training.campus.networking.webserver.http.HttpToken;

public class ResponseWriter {

	public void send(OutputStream os, HttpResponse response) {
		try (Writer writer = new OutputStreamWriter(os)) {
			writeStatusLine(writer, response);
			writeHeaderLines(writer, response);
			writeEmptyLine(writer);
			writeMessageBody(os, response);
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
