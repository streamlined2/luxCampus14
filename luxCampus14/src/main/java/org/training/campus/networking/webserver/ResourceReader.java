package org.training.campus.networking.webserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceReader {
	
	public InputStream readResource(String url) throws IOException {
		return new BufferedInputStream(new FileInputStream(new File(url)));
	}

	public InputStream readResource(File file) throws IOException {
		return new BufferedInputStream(new FileInputStream(file));
	}

}
