package no.uio.ifi.wsi.search.language.impl.generic;

import lombok.Value;

@Value
public class GenericPattern {

	private char patternChar;
	private char endChar;
	private String name;
	private boolean isBoolean;
	private int level;
}
