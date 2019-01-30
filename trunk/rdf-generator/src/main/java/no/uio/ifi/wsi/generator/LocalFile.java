package no.uio.ifi.wsi.generator;

import java.io.File;

import lombok.Value;

@Value
public class LocalFile {

	private File file;
	private String localName;

}
