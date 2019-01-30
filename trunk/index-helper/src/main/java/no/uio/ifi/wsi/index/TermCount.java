package no.uio.ifi.wsi.index;

import lombok.Value;

@Value
public class TermCount {

	private long count;
	private String term;
	private String type;

}
