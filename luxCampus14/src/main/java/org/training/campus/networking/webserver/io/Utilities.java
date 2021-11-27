package org.training.campus.networking.webserver.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Utilities {
	private static final int BUFFER_SIZE = 1024;

	private Utilities() {
	}

	public static int transferTo(InputStream is, OutputStream os, int upTo) throws IOException {
		if (upTo <= 0)
			throw new IllegalArgumentException("data size to copy should be positive value");
		byte[] buffer = new byte[BUFFER_SIZE];
		int count = 0;
		while (is.available() > 0 && count < upTo) {
			int toBeRead = Math.min(is.available(), upTo - count);
			int actual = is.read(buffer, 0, toBeRead);
			if (actual == -1) {
				break;
			} else {
				os.write(buffer, 0, actual);
				count += actual;
			}
		}
		return count;
	}

}
