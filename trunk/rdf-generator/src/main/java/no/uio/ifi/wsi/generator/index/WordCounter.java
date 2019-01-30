package no.uio.ifi.wsi.generator.index;

import java.util.HashMap;

public class WordCounter extends HashMap<String, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6749385114979407198L;

	public void increment(String word) {
		if (!containsKey(word))
			put(word, (long) 1);
		else
			put(word, get(word) + 1);
	}
}
