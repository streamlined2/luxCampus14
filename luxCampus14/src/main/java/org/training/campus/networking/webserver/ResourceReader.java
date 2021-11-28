package org.training.campus.networking.webserver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.training.campus.networking.webserver.exception.MalformedResourceUrlException;
import org.training.campus.networking.webserver.exception.ResourceAccessException;
import org.training.campus.networking.webserver.exception.ResourceNotFoundException;
import org.training.campus.networking.webserver.exception.ResourceTooLargeException;

public class ResourceReader {

	private static final String DEFAULT_RESOURCE = "index.html";

	public ByteBuffer readResource(String url, String context) {
		final Path path = mapUrlToPath(url, context);
		if (!Files.exists(path))
			throw new ResourceNotFoundException(String.format("requested resource %s doesn't exist", url));

		try {
			if (Files.size(path) > Integer.MAX_VALUE)
				throw new ResourceTooLargeException(String.format("requested resource %s is too large", url));

			ByteBuffer buffer = ByteBuffer.allocate((int) Files.size(path));
			try (ReadableByteChannel source = FileChannel.open(path, StandardOpenOption.READ)) {
				source.read(buffer);
				buffer.flip();
				return buffer;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceAccessException(e);
		}
	}

	private Path mapUrlToPath(String url, String context) {
		int index = url.indexOf(context);
		if (index == -1)
			throw new MalformedResourceUrlException(
					String.format("resource url '%s' doesn't contain context '%s'", url, context));

		index += context.length();
		if (url.startsWith("/", index)) {
			index++;
		}
		String relativeResource = url.substring(index);
		if (relativeResource.isBlank()) {
			relativeResource = DEFAULT_RESOURCE;
		}

		URL resourceUrl = getClass().getClassLoader().getResource(relativeResource);
		if (resourceUrl == null)
			throw new MalformedResourceUrlException(String.format("resource '%s' can't be located", url));

		try {
			return Path.of(resourceUrl.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new MalformedResourceUrlException(e);
		}
	}

}
