package org.training.campus.networking.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.training.campus.networking.webserver.exception.MalformedRequestException;
import org.training.campus.networking.webserver.http.HttpMethod;
import org.training.campus.networking.webserver.http.HttpToken;
import org.training.campus.networking.webserver.http.request.ByteArrayRequestMessageBody;
import org.training.campus.networking.webserver.http.request.HttpRequest;
import org.training.campus.networking.webserver.http.request.RequestHeader;
import org.training.campus.networking.webserver.http.request.RequestMessageBody;
import org.training.campus.networking.webserver.io.Source;

public class RequestParser {

	public HttpRequest parse(Source source) {
		try {
			Scanner lineScanner = source.getScanner();
			lineScanner.useDelimiter(HttpToken.END_OF_LINE.getPattern());

			if (!lineScanner.hasNext())
				throw new MalformedRequestException("request line missing");
			HttpRequest request = parseRequestLine(lineScanner.next());

			do {
				if (!lineScanner.hasNext()) {
					throw new MalformedRequestException("empty line after header fields absent");
				}
				String line = lineScanner.next();
				if (line.isEmpty()) {
					break;
				}
				RequestHeader header = parseRequestHeader(line);
				request.addHeader(header.name(), header);
			} while (true);

			getMessageBody(source.getInputStream(), request);
			return request;
		} catch (IOException e) {
			throw new MalformedRequestException(e);
		}
	}

	private HttpRequest parseRequestLine(String line) {
		Matcher matcher = HttpToken.SPACE.getPattern().matcher(line);

		if (!matcher.find())
			throw new MalformedRequestException("request line doesn't contain spaces");
		String method = line.substring(0, matcher.start());

		var httpMethod = HttpMethod.getByName(method);
		if (httpMethod.isEmpty())
			throw new MalformedRequestException(String.format("wrong request method %s", method));

		int index = matcher.end();
		if (!matcher.find(index))
			throw new MalformedRequestException("no space found between URL and protocol version in request line");
		String url = line.substring(index, matcher.start());
		if (url.isBlank())
			throw new MalformedRequestException("URL can't be blank");

		index = matcher.end();
		String protocol = line.substring(index);
		if (protocol.isBlank())
			throw new MalformedRequestException("protocol version can't be blank");

		return new HttpRequest(url, httpMethod.get(), protocol);
	}

	private RequestHeader parseRequestHeader(String line) {
		Matcher matcher = HttpToken.REQUEST_HEADER_SEPARATOR.getPattern().matcher(line);

		if (!matcher.find())
			throw new MalformedRequestException("no separator found between header name and value");
		String header = line.substring(0, matcher.start());
		if (header.isBlank())
			throw new MalformedRequestException("header name can't be blank");

		int index = matcher.end();
		String value = line.substring(index).trim();

		return new RequestHeader(header, value);
	}

	private void getMessageBody(InputStream inputStream, HttpRequest request) throws IOException {
		RequestMessageBody body = new ByteArrayRequestMessageBody();
		request.setMessageBody(Optional.of(body));
		inputStream.transferTo(body.getOutputStream());
	}

}
