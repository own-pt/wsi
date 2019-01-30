package no.uio.ifi.wsi.generator.index;

import java.util.HashMap;

public class WordCache extends HashMap<String, WordCounter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6430729971100315506L;

	public void addType(String type) {
		put(type, new WordCounter());
	}

	public void increment(String type, String word) {
		if (!containsKey(type))
			addType(type);
		get(type).increment(word);
	}

}
